package it.usuratonkachi.mongostreamspark.config;

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
			.add("doctypeid", DataTypes.StringType)
			.add("doctypename", DataTypes.StringType)
			.add("username", DataTypes.StringType)
			.add("status", DataTypes.IntegerType)
			.add("size", NUMBER_LONG_SCHEMA,true)
			.add("mimetype", DataTypes.StringType)
			.add("filename", DataTypes.StringType)
			.add("createddate", DATE_SCHEMA,true)
			.add("uploaddate", DATE_SCHEMA,true);

	public static final StructType KAFKA_DOCUMENT_SCHEMA = new StructType()
			.add("ns",NS_SCHEMA)
			.add("fullDocument",DOCUMENT_SCHEMA);

}
