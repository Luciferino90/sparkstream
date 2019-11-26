package it.arubapec.esecurity.mongostreamspark.service;

import it.arubapec.esecurity.mongostreamspark.config.JobProperties;
import it.arubapec.esecurity.mongostreamspark.config.KafkaSparkTopicConfig;
import it.arubapec.esecurity.mongostreamspark.config.Schemas;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.*;
import org.apache.spark.sql.streaming.DataStreamWriter;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.StructType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import scala.collection.Seq;

import java.io.Serializable;
import java.util.Arrays;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.from_json;

@Component
@Slf4j
public class KafkaToMongoDB implements Serializable {

	@Autowired
	private SparkSession spark;

	@Autowired
	private JobProperties properties;

	public void doWork() throws StreamingQueryException {
		log.info("Using configuration: " + properties);
		// Create DataSet representing the stream of input lines from kafka
		getRowWriter(properties.getDocumentSource(),
				Schemas.KAFKA_DOCUMENT_SCHEMA).start();

		spark.streams().awaitAnyTermination();
	}

	private DataStreamWriter<Row> getRowWriter(KafkaSparkTopicConfig config, StructType schema) {
		Dataset<String> lines = getKafkaMessages(config.getKafkaTopics());
		// Generate running word count
		Dataset<Row> streamingMessages = getStructLines(lines, schema);

		return streamingMessages.writeStream().format("console");
	}
	public Seq<Column> seq(Column... cols)
	{
		return scala.collection.JavaConverters.collectionAsScalaIterableConverter(Arrays.asList(cols)).asScala().toBuffer();
	}

	private Dataset<Row> getStructLines(Dataset<String> lines, StructType schema) {
		String tmpDateField="objectDate";
		return lines
				.select(from_json(col("value"), schema).as("payload"));
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
