import POJOs.ArrivalPrediction;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * A websocket for providing a continuous stream of messages containing arrival predictions for London buses.
 * For each connection the service executes a runnable every 5 seconds, that fetches arrival info from the API, and relays it through the websocket.
 */
@ServerEndpoint("/arrivalPredictionsById")
public class ArrivalTimesByIdServer {
  private Map<Session, ScheduledFuture> sessionToFutureMap;
  private Map<Session, String> sessionToStopIdMap;
  private ScheduledExecutorService scheduler;
  private static ObjectMapper objectMapper = new ObjectMapper();

  public ArrivalTimesByIdServer() {
    this.sessionToFutureMap = Collections.synchronizedMap(new HashMap<Session, ScheduledFuture>());
    this.sessionToStopIdMap = Collections.synchronizedMap(new HashMap<Session, String>());
    ScheduledThreadPoolExecutor sched = new ScheduledThreadPoolExecutor(1);
    sched.setRemoveOnCancelPolicy(true);
    this.scheduler = sched;
  }

  @OnOpen
  public void onOpen(final Session session){
     System.out.println("Connection established to: " + session.getId());
     sessionToStopIdMap.put(session, null);

     Runnable runnable = new Runnable() {
       @Override
       public void run() {
         if(sessionToStopIdMap.get(session) == null) return;
         List<ArrivalPrediction> arrivalPredictions = getArrivalPredictionsForId(sessionToStopIdMap.get(session));
         sendArrivalEntriesToSession(arrivalPredictions, session);
       }
     };

     ScheduledFuture future = scheduler.scheduleAtFixedRate(runnable, 5, 5, TimeUnit.SECONDS);
     sessionToFutureMap.put(session, future);
  }


  @OnMessage
  public void onMessage(String message, Session session){
    System.out.println(message);
    if(message != null && message.contains("currentStopId:")){
      String currentStopId = message.split(":")[1];
      sessionToStopIdMap.put(session, currentStopId);
    }
  }

  @OnClose
  public void onClose(Session session){
    sessionToFutureMap.get(session).cancel(true);
    sessionToFutureMap.remove(session);
    sessionToStopIdMap.remove(session);
  }

  @OnError
  public void onError(Session session, Throwable throwable){
    sessionToFutureMap.get(session).cancel(true);
  }

  private void sendArrivalEntriesToSession(List<ArrivalPrediction> arrivalPredictions, Session session) {
    try {
      session.getBasicRemote().sendText(objectMapper.writeValueAsString(arrivalPredictions));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Gets the current arrival predictions for the stop ID from the API component
   * @param id the stop id for which to get arrival predictions
   * @return A list of ArrivalPrediction for the given stop id
   */
  private List<ArrivalPrediction> getArrivalPredictionsForId(String id) {
    try {
      InputStream inputStream = (new URL("http://departure-times-api:8080/arrival-predictions?id=" + id)).openStream();
      List<ArrivalPrediction> arrivalPredictions = objectMapper.readValue(inputStream, new TypeReference<List<ArrivalPrediction>>(){});
      return arrivalPredictions;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return new ArrayList<>();
  }
}
