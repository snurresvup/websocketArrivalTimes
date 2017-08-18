mvn clean
mvn package
docker build --no-cache -t departure-times-websocket-server:latest .
docker stop websocketServer
docker rm websocketServer
docker run --name websocketServer -p 8082:8082 -ti -d departure-times-websocket-server