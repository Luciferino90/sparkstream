/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.arubapec.esecurity.mongostreamspark;

import it.arubapec.esecurity.mongostreamspark.config.JobProperties;
import it.arubapec.esecurity.mongostreamspark.config.SparkProperties;
import it.arubapec.esecurity.mongostreamspark.service.KafkaToHDFS;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.context.properties.*;

@SpringBootApplication(scanBasePackages = {"it.arubapec.esecurity.mongostreamspark",})
@EnableConfigurationProperties({
        JobProperties.class, SparkProperties.class
})
public  class SpringKafkaApplication implements CommandLineRunner {

  @Autowired KafkaToHDFS kafkaToHDFS;
  public static void main(String[] args) throws Exception {
    SpringApplication.run(SpringKafkaApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    kafkaToHDFS.doWork();
  }
}
