# mongo-stream-spark-job

Job che elabora i dati da mongo in live stream



Build spark
docker build -t docfly-spark:latest -f kubernetes/dockerfiles/spark/Dockerfile .
docker tag docfly-spark localhost:5000/docfly-spark:latest
docker push localhost:5000/docfly-spark:latest


Build Java

cp /Users/luca/workbench/techitalia/Java/sparkstream/mongo-stream-spark-job-master/target/mongo-spark-streaming-0.0.1.jar examples/jars/
rm examples/jars/mongo.jar 
mv examples/jars/mongo-spark-streaming-0.0.1.jar examples/jars/mongo.jar

docker build -t tech-spark:latest -f kubernetes/dockerfiles/spark/Dockerfile .
docker tag tech-spark localhost:5000/tech-spark:latest
docker push localhost:5000/tech-spark:latest

// working
bin/spark-submit \
	--master k8s://https://kubernetes.docker.internal:6443 \
	--deploy-mode cluster \
	--conf spark.executor.instances=2 \
	--conf spark.kubernetes.authenticate.driver.serviceAccountName=default \
	--conf spark.kubernetes.container.image=localhost:5000/mongo-spark-streaming:0.0.1 \
	--class it.arubapec.esecurity.mongostreamspark.SpringKafkaApplication \
	--conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=file:/app/log4j.properties" \
	--conf "spark.driver.extraJavaOptions=-Dlog4j.configuration=file:/app/log4j.properties" \
	--name spark-pi \
	-v \
	local:///opt/spark/examples/jars/mongo.jar \
	-Dlog4j.debug=true \
	-Dlog4j.configuration=file:/app/log4j.properties




bin/spark-submit \
	--master k8s://https://kubernetes.docker.internal:6443 \
	--deploy-mode cluster \
	--conf spark.executor.instances=2 \
	--conf spark.kubernetes.authenticate.driver.serviceAccountName=default \
	--conf spark.kubernetes.container.image=localhost:5000/mongo-spark-streaming:0.0.1 \
	--class it.arubapec.esecurity.mongostreamspark.SpringKafkaApplication \
	--conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=file:/app/log4j.properties" \
	--conf "spark.driver.extraJavaOptions=-Dlog4j.configuration=file:/app/log4j.properties" \
	--name spark-pi \
	-v \
	local:///opt/spark/examples/jars/app.jar/app.jar \
	-Dlog4j.debug=true \
	-Dlog4j.configuration=file:/app/log4j.properties






	il punto è:
	col dockerfile funziona ma se usiamo spring boot riusciamo a scompattare le cose
	se usiamo il plugin maven riusciamo ad avviare l'applicazione