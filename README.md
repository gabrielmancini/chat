pre requisites

	install git
	install maven from http://maven.apache.org/download.html

to build

	git clone git://github.com/gabrielmancini/chat.git
	cd chat
	mvn clean install

to run server

	java -jar server/target/server-0.0.1-SNAPSHOT-jar-with-dependencies.jar 

to run client

	java -jar client/target/client-0.0.1-SNAPSHOT-jar-with-dependencies.jar

