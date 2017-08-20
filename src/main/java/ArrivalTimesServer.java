
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.grizzly.streams.StreamOutput;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * An older websocket implementation that is no longer used.
 * This socket would get the arrival predictions from the API by the name of the station.
 */

@ServerEndpoint("/arrivalPredictions")
public class ArrivalTimesServer {
  private Map<String, Set<Session>> stationToSessionsMap;
  private Map<Session, String> sessionToStationMap;
  private ObjectMapper objectMapper;
  //private HashMap<String, HashMap<String, ArrivalEntry>> arrivalTimes;

  public ArrivalTimesServer() {
    //arrivalTimes = new HashMap<>();
    objectMapper = new ObjectMapper();
    stationToSessionsMap = Collections.synchronizedMap(new HashMap<String, Set<Session>>());
    sessionToStationMap = Collections.synchronizedMap(new HashMap<Session, String>());

    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    scheduler.scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {
        getInstantPredictions();
      }
    }, 5,  5, TimeUnit.SECONDS);
  }

  @OnOpen
  public void onOpen(Session session){
    System.out.println(session.getId() + " has opened a websocket connection.");
    sessionToStationMap.put(session, null);
  }

  @OnMessage
  public void onMessage(String message, Session session){
    System.out.println("Received: " + message);
    if(message != null && message.contains("currentStop:")){
      String currentStation = message.split(":")[1];

      String prevStation = sessionToStationMap.get(session);

      //If the session is already in a set, remove it from the set
      if(prevStation != null) stationToSessionsMap.get(prevStation).remove(session);

      //Insert the session in the set for the new  station
      if(!stationToSessionsMap.containsKey(currentStation)){
        Set<Session> sessions = Collections.synchronizedSet(new HashSet<Session>());
        sessions.add(session);
        stationToSessionsMap.put(currentStation, sessions);
      } else {
        stationToSessionsMap.get(currentStation).add(session);
      }

      //Update the station pointed to bu the session
      sessionToStationMap.put(session, currentStation);

      try {
        sendData();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @OnClose
  public void onClose(Session session){
    System.out.println("Closing session: " + session.getId());
    String prevStation = sessionToStationMap.get(session);
    stationToSessionsMap.get(prevStation).remove(session);
  }

  public StreamOutput getInstantPredictions(){
    try {
      //arrivalTimes.clear();

      InputStream inputStream = (new URL("http://countdown.api.tfl.gov.uk/interfaces/ura/instant_V1")).openStream();

      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

      String line = bufferedReader.readLine();

      final String[] data = parseArray(line);
      long timeStamp = data == null ? 0 : Long.parseLong(data[2]);

      String[] line1 = data;

      while (line1 != null) {
        if (line1.length < 4) {
          line1 = parseArray(bufferedReader.readLine());
          continue;
        }
        int arrivalTime = (int) ((Long.parseLong(line1[3]) - timeStamp) / 1000);
        String stationName = line1[1];
        String lineName = line1[2];


        //if(!arrivalTimes.containsKey(stationName)){
        //  arrivalTimes.put(stationName, new HashMap<String, ArrivalEntry>());
        //}
        //if(!arrivalTimes.get(stationName).containsKey(lineName) ||
        //    arrivalTimes.get(stationName).get(lineName).getArrivalTime() > arrivalTime){
        //  arrivalTimes.get(stationName).put(lineName, new ArrivalEntry(stationName, lineName, arrivalTime, timeStamp));
        //}

        /*if(stationToSessionsMap.get(stationName) != null) {
          for (Session s : stationToSessionsMap.get(stationName)) {
            String payload = objectMapper.writeValueAsString(arrivalTimes.get(stationName));
            s.getBasicRemote().sendText(payload);
          }
        }*/

        line1 = parseArray(bufferedReader.readLine());
      }
      bufferedReader.close();

      sendData();

    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  private void sendData() throws IOException {
    for(Session session : sessionToStationMap.keySet()){
      //if(arrivalTimes.get(sessionToStationMap.get(session)) == null) continue;
      //session.getBasicRemote().sendText(objectMapper.writeValueAsString(arrivalTimes.get(sessionToStationMap.get(session))));
    }
  }

  private String[] parseArray(String arrayString){
    if(arrayString == null) return null;
    //Remove array notation
    String temp = arrayString.replaceAll("\\[", "")
        .replaceAll("\\]", "");
    String[] result = temp.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
    //Remove quotes
    for (int i = 0; i < result.length; i++) {
      result[i] = result[i].replaceAll("\"", "");
    }
    return result;
  }
}
