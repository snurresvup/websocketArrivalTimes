package POJOs;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Timing {
  @JsonProperty("$type")
  private String type;
  @JsonProperty
  private String countdownServerAdjustment;
  @JsonProperty
  private String source;
  @JsonProperty
  private String insert;
  @JsonProperty
  private String read;
  @JsonProperty
  private String sent;
  @JsonProperty
  private String received;

  public String getCountdownServerAdjustment() {
    return countdownServerAdjustment;
  }

  public void setCountdownServerAdjustment(String countdownServerAdjustment) {
    this.countdownServerAdjustment = countdownServerAdjustment;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getInsert() {
    return insert;
  }

  public void setInsert(String insert) {
    this.insert = insert;
  }

  public String getRead() {
    return read;
  }

  public void setRead(String read) {
    this.read = read;
  }

  public String getSent() {
    return sent;
  }

  public void setSent(String sent) {
    this.sent = sent;
  }

  public String getReceived() {
    return received;
  }

  public void setReceived(String received) {
    this.received = received;
  }
}