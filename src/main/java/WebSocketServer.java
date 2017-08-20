import org.glassfish.tyrus.server.Server;

import javax.websocket.DeploymentException;

public class WebSocketServer {
  public static void main(String[] args) {
    startServer();
  }

  private static void startServer() {
    Server server = new Server("localhost", 8082, "/websockets", ArrivalTimesByIdServer.class);

    try {
      server.start();
      while(!Thread.interrupted()){

      }
    } catch (DeploymentException e) {
      e.printStackTrace();
    } finally {
      server.stop();
    }
  }
}
