package assignment.tientn.ledis.models;

import java.util.ArrayList;
import java.util.LinkedList;

public class Command {
  private ECommandType type;
  private String cmd;
  private String key;
  private LinkedList<String> data;

  public Command(ECommandType type, String cmd, String key, LinkedList<String> data) {
    this.type = type;
    this.cmd = cmd;
    this.key = key;
    this.data = new LinkedList<String>(data);
  }

  public ECommandType getType() {
    return type;
  }

  public void setType(ECommandType type) {
    this.type = type;
  }

  public String getCmd() {
    return cmd;
  }

  public void setCmd(String cmd) {
    this.cmd = cmd;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public LinkedList<String> getData() {
    return data;
  }

  public void setData(ArrayList<String> data) {
    this.data = new LinkedList<String>(data);
  }

  @Override
  public String toString() {
    return "Command [type=" + type + ", cmd=" + cmd + ", key=" + key + ", data=" + data + "]";
  }

}
