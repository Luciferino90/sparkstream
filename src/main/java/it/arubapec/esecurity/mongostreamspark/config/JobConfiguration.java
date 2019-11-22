package it.arubapec.esecurity.mongostreamspark.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JobConfiguration {

	private final SparkProperties sparkProperties;
	private final JobProperties jobProperties;


	@Bean
	@Lazy
	public SparkSession sparkSession() {
		log.info("Using configuration: " + sparkProperties);
		return SparkSession.builder()
				.config(sparkConf())
				.getOrCreate();
	}


	@Bean
	public SparkConf sparkConf() {
		log.info("Using configuration: " + sparkProperties);
		return new SparkConf()
				.set("spark.sql.session.timeZone", "UTC")
				.setAppName(sparkProperties.getAppName())
				.setMaster(sparkProperties.getMasterUri());
	}


}
