mvn clean
mvn package
docker build --no-cache -t departure-times-websocket-server:latest .
docker stop websocketServer
docker rm websocketServer
docker run --name websocketServer -p 8082:8082 --link departure-times-api:departure-times-api -tid departure-times-websocket-server
