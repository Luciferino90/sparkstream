package it.usuratonkachi.mongostreamspark.config;

import lombok.Data;

@Data
public class KafkaSparkTopicConfig {

	private String kafkaTopics;
	private String checkpointLocation;
	private String fsSavePath;
	private String fsSaveType;
}
