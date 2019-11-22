package it.arubapec.esecurity.mongostreamspark.config;

import lombok.Data;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

public class Schemas {


	public static final StructType DATE_SCHEMA = new StructType()
			.add("$date", DataTypes.LongType);

	public static final StructType NUMBER_LONG_SCHEMA = new StructType()
			.add("$numberLong", DataTypes.StringType);
	public static final StructType NS_SCHEMA = new StructType()
			.add("db", DataTypes.StringType)
			.add("coll", DataTypes.StringType);

	public static final StructType DOCUMENT_SCHEMA = new StructType()
			.add("docclassid", DataTypes.StringType)
			.add("docclassname", DataTypes.StringType)
			.add("status", DataTypes.IntegerType)
			.add("size", NUMBER_LONG_SCHEMA,true)
			.add("origin", DataTypes.IntegerType)
			.add("totaldocnumber", NUMBER_LONG_SCHEMA,true)
			.add("createddate", DATE_SCHEMA,true)
			.add("uploaddate", DATE_SCHEMA,true)
			;


	public static final StructType EVENT_SCHEMA = new StructType()
			.add("code", DataTypes.IntegerType)
			.add("category", DataTypes.IntegerType)
			.add("origin", DataTypes.IntegerType)
			.add("eventdate", DATE_SCHEMA);

	public static final StructType KAFKA_DOCUMENT_SCHEMA = new StructType()
			.add("ns",NS_SCHEMA)
			.add("fullDocument",DOCUMENT_SCHEMA);
	public static final StructType KAFKA_EVENT_SCHEMA = new StructType()
			.add("ns",NS_SCHEMA)
			.add("fullDocument",EVENT_SCHEMA);
	public static final StructType EXAMPLE = new StructType()
			.add("field", DataTypes.StringType);


}
