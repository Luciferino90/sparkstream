FROM localhost:5000/docfly-spark3:latest as build
WORKDIR /workspace/app

USER root

ARG JAR_FILE
COPY target/${JAR_FILE} ./target/app.jar
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM localhost:5000/docfly-spark3:latest
VOLUME /tmp
EXPOSE 4040 7077 8080
ENV SPRING_PROFILES_ACTIVE=docker
ENV SPRING_JMX_ENABLED=false

USER root
RUN apt-get update
RUN apt-get install procps -y
RUN apt-get install less -y

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
COPY target/dependency/javax.activation-api-*.jar /opt/spark/jars/
COPY target/dependency/jetty-util-*.jar /opt/spark/jars/
COPY target/dependency/kafka-clients-*.jar /opt/spark/jars/
COPY target/dependency/metrics-core-*.jar /opt/spark/jars/
COPY target/dependency/spark-streaming-kafka-0-10_2.11-*.jar /opt/spark/jars/
COPY target/dependency/spring-aop-*.RELEASE.jar /opt/spark/jars/
COPY target/dependency/spring-beans-*.RELEASE.jar /opt/spark/jars/
COPY target/dependency/spring-boot-*.RELEASE.jar /opt/spark/jars/
COPY target/dependency/spring-boot-autoconfigure-*.RELEASE.jar /opt/spark/jars/
COPY target/dependency/spring-boot-configuration-processor-*.RELEASE.jar /opt/spark/jars/
COPY target/dependency/spring-boot-starter-*.RELEASE.jar /opt/spark/jars/
COPY target/dependency/spring-context-*.RELEASE.jar /opt/spark/jars/
COPY target/dependency/spring-core-*.RELEASE.jar /opt/spark/jars/
COPY target/dependency/spring-expression-*.RELEASE.jar /opt/spark/jars/
COPY target/dependency/spring-jcl-*.RELEASE.jar /opt/spark/jars/
COPY target/dependency/unused-*.jar /opt/spark/jars/
COPY target/dependency/xml-apis-*.jar /opt/spark/jars/
COPY target/${JAR_FILE} /opt/spark/examples/jars/app.jar
COPY --from=build ${DEPENDENCY}/../app.jar /opt/spark/examples/jars/app.jar

RUN mkdir -p /opt/spark/checkpoint-dir

ARG JAR_VERSION
RUN echo ${JAR_VERSION} > /app/version.txt

# https://docs.docker.com/develop/develop-images/dockerfile_best-practices/#user
RUN addgroup --gid 1000 --system serviceapp
RUN adduser --uid 1000 --system serviceapp serviceapp
RUN chown -R serviceapp:serviceapp /app/ /opt/

# https://docs.docker.com/develop/develop-images/dockerfile_best-practices/#user
USER 1000:1000

ARG START_CLASS
ENV STARTCLASSNAME=${START_CLASS}
#ENTRYPOINT java -noverify -XX:TieredStopAtLevel=1 -XX:MaxRAM=800m -Xmx400m -Dreactor.netty.http.server.accessLogEnabled=true -Djava.security.egd=file:/dev/./urandom -cp app:app/lib/* $STARTCLASSNAME
