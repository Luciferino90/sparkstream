FROM localhost:5000/spark-home:latest as build
WORKDIR /workspace/app

ARG JAR_FILE
COPY target/${JAR_FILE} ./target/app.jar
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)


FROM localhost:5000/spark-home:latest
VOLUME /tmp
EXPOSE 4040 7077 8080
ENV SPRING_PROFILES_ACTIVE=docker
ENV SPRING_JMX_ENABLED=false

#ENV TIME_ZONE=Europe/Rome
#ENV TZ=$TIME_ZONE
#RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime
#RUN echo $TZ > /etc/timezone
# RUN dpkg-reconfigure -f noninteractive tzdata

RUN apk update
RUN apk add procps -y
RUN apk add less -y
#RUN apk add inetutils-telnet ???
#RUN apk add iputils
#RUN apk add bind-tools

# https://docs.docker.com/develop/develop-images/dockerfile_best-practices/#user
RUN addgroup --gid 1000 --system serviceapp
RUN adduser --uid 1000 --system serviceapp serviceapp


ARG DEPENDENCY=/workspace/app/target/dependency
COPY target/dependency /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY target/classes /app

RUN chmod go+r /opt/spark/jars/kubernetes-client-*.jar

RUN rm -f /opt/spark/jars/snakeyaml-*.jar
COPY target/dependency/snakeyaml-*.jar /opt/spark/jars/

RUN rm -f /opt/spark/jars/validation-api-*.jar
COPY target/dependency/validation-api-*.jar /opt/spark/jars/

RUN rm -f /opt/spark/jars/gson-*.jar
COPY target/dependency/gson-*.jar /opt/spark/jars/

COPY target/dependency/amqp-client-*.jar /opt/spark/jars/
COPY target/dependency/bson-*.jar /opt/spark/jars/
#COPY target/dependency/classmate-*.jar /opt/spark/jars/
#COPY target/dependency/hibernate-validator-*.Final.jar /opt/spark/jars/
COPY target/dependency/javax.activation-api-*.jar /opt/spark/jars/
#COPY target/dependency/jboss-logging-*.Final.jar /opt/spark/jars/
COPY target/dependency/jetty-util-*.jar /opt/spark/jars/
COPY target/dependency/kafka-clients-*.jar /opt/spark/jars/
#COPY target/dependency/log4j-api-*.jar /opt/spark/jars/
#COPY target/dependency/log4j-to-slf4j-*.jar /opt/spark/jars/
#COPY target/dependency/logback-classic-*.jar /opt/spark/jars/
#COPY target/dependency/logback-core-*.jar /opt/spark/jars/
COPY target/dependency/metrics-core-*.jar /opt/spark/jars/
#COPY target/dependency/mongo-java-driver-*.jar /opt/spark/jars/
#COPY target/dependency/mongo-spark-connector_2.11-*.jar /opt/spark/jars/
COPY target/dependency/spark-streaming-kafka-0-10_2.11-*.jar /opt/spark/jars/
COPY target/dependency/spring-aop-*.RELEASE.jar /opt/spark/jars/
COPY target/dependency/spring-beans-*.RELEASE.jar /opt/spark/jars/
COPY target/dependency/spring-boot-*.RELEASE.jar /opt/spark/jars/
COPY target/dependency/spring-boot-autoconfigure-*.RELEASE.jar /opt/spark/jars/
COPY target/dependency/spring-boot-configuration-processor-*.RELEASE.jar /opt/spark/jars/
COPY target/dependency/spring-boot-starter-*.RELEASE.jar /opt/spark/jars/
#COPY target/dependency/spring-boot-starter-logging-*.RELEASE.jar /opt/spark/jars/
#COPY target/dependency/spring-boot-starter-validation-*.RELEASE.jar /opt/spark/jars/
COPY target/dependency/spring-context-*.RELEASE.jar /opt/spark/jars/
COPY target/dependency/spring-core-*.RELEASE.jar /opt/spark/jars/
COPY target/dependency/spring-expression-*.RELEASE.jar /opt/spark/jars/
COPY target/dependency/spring-jcl-*.RELEASE.jar /opt/spark/jars/
#COPY target/dependency/tomcat-embed-el-*.jar /opt/spark/jars/
COPY target/dependency/unused-*.jar /opt/spark/jars/
COPY target/dependency/xml-apis-*.jar /opt/spark/jars/
COPY target/${JAR_FILE} /opt/spark/examples/jars/app.jar
COPY --from=build ${DEPENDENCY}/../app.jar /opt/spark/examples/jars/app.jar

RUN mkdir -p /opt/spark/checkpoint-dir

ARG JAR_VERSION
RUN echo ${JAR_VERSION} > /app/version.txt

RUN mkdir -p /Users/luca/.kubernetes

RUN chown -R serviceapp:serviceapp /app/ /opt/ /Users/luca/.kubernetes/

# https://docs.docker.com/develop/develop-images/dockerfile_best-practices/#user
USER 1000:1000

ARG START_CLASS
ENV STARTCLASSNAME=${START_CLASS}
#ENTRYPOINT java -noverify -XX:TieredStopAtLevel=1 -XX:MaxRAM=800m -Xmx400m -Dreactor.netty.http.server.accessLogEnabled=true -Djava.security.egd=file:/dev/./urandom -cp app:app/lib/* $STARTCLASSNAME
