import org.glassfish.tyrus.server.Server;

import javax.websocket.DeploymentException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class WebSocketServer {
  public static void main(String[] args) {
    startServer();
  }

  private static void startServer() {
    Server server = new Server("localhost", 8082, "/websockets", ArrivalTimesByIdServer.class);

    try {
      server.start();
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      System.out.println("Press any key to stop the server.");
      reader.readLine();
    } catch (DeploymentException | IOException e) {
      e.printStackTrace();
    } finally {
      server.stop();
    }
  }
}
