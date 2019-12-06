package it.usuratonkachi.mongostreamspark.service;

import it.usuratonkachi.mongostreamspark.config.JobProperties;
import it.usuratonkachi.mongostreamspark.config.KafkaSparkTopicConfig;
import it.usuratonkachi.mongostreamspark.config.Schemas;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.*;
import org.apache.spark.sql.streaming.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import scala.collection.Seq;

import java.io.*;
import java.sql.Struct;
import java.util.Arrays;

import static org.apache.spark.sql.functions.*;

@Component
@Slf4j
public class KafkaToHDFS implements Serializable {

	@Autowired
	private SparkSession spark;

	@Autowired
	private JobProperties properties;

	public void doWork() throws StreamingQueryException {
		log.info("Using configuration: " + properties);

		getRowWriter(properties.getDocumentsSource(),
				Schemas.KAFKA_DOCUMENT_SCHEMA,
				coalesce(seq(
						col("uploaddate.$date"),
						col("createddate.$date")
				))).start();
		getRowWriter(properties.getInternalDocumentsSource(),
				Schemas.KAFKA_DOCUMENT_SCHEMA,
				coalesce(seq(
						col("uploaddate.$date"),
						col("createddate.$date")
				))).start();
		spark.streams().awaitAnyTermination();
	}

	private DataStreamWriter<Row> getRowWriter(KafkaSparkTopicConfig config, StructType schema, Column date) {
		Dataset<String> lines = getKafkaMessages(config.getKafkaTopics());
		Dataset<Row> streamingMessages = getStructLines(lines, schema,date);

		return streamingMessages.writeStream()
				.option("checkpointLocation", config.getCheckpointLocation() )
				.partitionBy("collectionName", "year", "month", "day", "hour")
				.format(config.getFsSaveType())
				.option("path", config.getFsSavePath());
	}
	public Seq<Column> seq(Column... cols)
	{
		return scala.collection.JavaConverters.collectionAsScalaIterableConverter(Arrays.asList(cols)).asScala().toBuffer();
	}

	private Dataset<Row> getStructLines(Dataset<String> lines, StructType schema, Column dateColumn) {
		String tmpDateField="objectDate";
		return lines
				.select(from_json(col("value"), schema).as("payload"))
				.select(col("payload.fullDocument.*"),col("payload.ns.coll").as("collectionName"))
				.select(col("*"),
						dateColumn
								.$div(1000).cast(DataTypes.TimestampType)
								.as(tmpDateField))
				.select(
						col("collectionName"),
						year(col(tmpDateField)).as("year"),
						month(col(tmpDateField)).as("month"),
						dayofmonth(col(tmpDateField)).as("day"),
						hour(col(tmpDateField)).as("hour"),
						col("*")
				)
				.where(col(tmpDateField).isNotNull())
				;
	}

	private Dataset<String> getKafkaMessages(String topics) {
		return spark
				.readStream()
				.format("kafka")
				.option("kafka.bootstrap.servers", properties.getKafkaBootstrapServers())
				.option(properties.getKafkaSubscribeType(), topics)
				.load()
				.selectExpr("CAST(value AS STRING)")
				.as(Encoders.STRING());
	}
}
