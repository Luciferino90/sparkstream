package it.usuratonkachi.mongostreamspark.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.*;
import org.springframework.boot.context.properties.*;

@ConfigurationProperties(prefix = "job")
@Data
public class JobProperties {

	private String kafkaBootstrapServers;
	private String kafkaSubscribeType;

	private KafkaSparkTopicConfig documentsSource;
	private KafkaSparkTopicConfig internalDocumentsSource;

}
