mvn clean
mvn package
docker build --no-cache -t departure-times-websocket-server:latest .
docker stop websocketServer
docker rm websocketServer
docker run --name websocketServer --net=host -p 127.0.0.1:8082:8082 -ti -d departure-times-websocket-server