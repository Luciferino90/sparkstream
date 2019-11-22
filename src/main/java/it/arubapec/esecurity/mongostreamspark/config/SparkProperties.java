package it.arubapec.esecurity.mongostreamspark.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spark")
@Data
public class SparkProperties {

	private String appName;

	private String masterUri;

	private String homeDir;

	private String hdfsSaveType;

}
