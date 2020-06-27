package assignment.tientn.ledis.models;

import java.util.ArrayList;

public class Command {
  private ECommandType type;
  private String cmd;
  private String key;
  private ArrayList<String> data;

  public Command(ECommandType type, String cmd, String key, ArrayList<String> data) {
    this.type = type;
    this.cmd = cmd;
    this.key = key;
    if (data != null)
      this.data = new ArrayList<String>(data);
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

  public ArrayList<String> getData() {
    return data;
  }

  public void setData(ArrayList<String> data) {
    if (data != null)
      this.data = new ArrayList<String>(data);
  }

  @Override
  public String toString() {
    return "Command [type=" + type + ", cmd=" + cmd + ", key=" + key + ", data=" + data + "]";
  }

}
