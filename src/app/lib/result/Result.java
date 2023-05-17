package app.lib.result;

import java.util.ArrayList;
import java.util.HashMap;

public class Result {
  private Status status;
  private ResultType type;
  private String text;
  private HashMap<String,ArrayList<Object>> table;
  private String reason;
  private StackTraceElement[] stackTrace;
 
  public Result withStatus(Status status) {
    this.status = status;
    return this;
  }

  public Result withType(ResultType type) {
    this.type = type;
    return this;
  }

  public Result withText(String text) {
    this.text = text;
    return this;
  }

  public Result withTable(HashMap<String,ArrayList<Object>> table) {
    this.table = table;
    return this;
  }

  public Result withReason(String reason) {
    this.reason = reason;
    return this;
  }

  public Result withStackTrace(StackTraceElement[] stackTrace) {
    this.stackTrace = stackTrace;
    return this;
  }

  public Status getStatus() {
    return this.status;
  }

  public ResultType getType() {
    return this.type;
  }

  public String getText() {
    return this.text;
  }

  public HashMap<String,ArrayList<Object>> getTable() {
    return this.table;
  }

  public String getReason() {
    return this.reason;
  }

  public StackTraceElement[] getStackTrace() {
    return this.stackTrace;
  }

}
