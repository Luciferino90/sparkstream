package it.arubapec.esecurity.mongostreamspark.config;

import lombok.*;
import org.springframework.boot.context.properties.*;

@ConfigurationProperties(prefix = "job")
@Data
public class JobProperties {

	private String kafkaBootstrapServers;
	private String kafkaSubscribeType;

	private KafkaSparkTopicConfig documentSource;
	private KafkaSparkTopicConfig eventSource;




}
