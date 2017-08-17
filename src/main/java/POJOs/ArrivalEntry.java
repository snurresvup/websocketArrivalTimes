package POJOs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ArrivalEntry {
  private String id;
  private String stationName;
  private String lineName;
  private double arrivalTime;
  private long timestamp;

  @JsonCreator
  public ArrivalEntry(){

  }

  public ArrivalEntry(String stationName, String lineName, double arrivalTime, long timestamp) {
    this.stationName = stationName;
    this.lineName = lineName;
    this.arrivalTime = arrivalTime;
    this.timestamp = timestamp;
  }

  public ArrivalEntry(String id, String stationName, String lineName, double arrivalTime, long timestamp) {
    this.id = id;
    this.stationName = stationName;
    this.lineName = lineName;
    this.arrivalTime = arrivalTime;
    this.timestamp = timestamp;
  }

  @JsonProperty
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @JsonProperty
  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  @JsonProperty
  public String getStationName() {
    return stationName;
  }

  public void setStationName(String stationName) {
    this.stationName = stationName;
  }

  @JsonProperty
  public String getLineName() {
    return lineName;
  }

  public void setLineName(String lineName) {
    this.lineName = lineName;
  }

  @JsonProperty
  public double getArrivalTime() {
    return arrivalTime;
  }

  public void setArrivalTime(double arrivalTime) {
    this.arrivalTime = arrivalTime;
  }
}
