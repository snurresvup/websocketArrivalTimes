FROM java:latest
ADD target/websocketArrivalTimes-1.0-SNAPSHOT.jar /exec/websocketArrivalTimes-1.0-SNAPSHOT.jar
ADD WEB-INF/web.xml WEB-INF/web.xml
CMD java -jar /exec/websocketArrivalTimes-1.0-SNAPSHOT.jar
EXPOSE 8082