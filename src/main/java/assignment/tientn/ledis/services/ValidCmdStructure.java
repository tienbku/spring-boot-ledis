package assignment.tientn.ledis.services;

import assignment.tientn.ledis.models.ECommandType;

public class ValidCmdStructure {
  private ECommandType type;
  private int numOfKeys;
  private int minOfArgs;
  private int maxOfArgs;

  public ValidCmdStructure(ECommandType type, int numOfKeys, int minOfArgs, int maxOfArgs) {
    this.type = type;
    this.numOfKeys = numOfKeys;
    this.minOfArgs = minOfArgs;
    this.maxOfArgs = maxOfArgs;
  }

  public ECommandType getType() {
    return type;
  }

  public void setType(ECommandType type) {
    this.type = type;
  }

  public int getNumOfKeys() {
    return numOfKeys;
  }

  public void setNumOfKeys(int numOfKeys) {
    this.numOfKeys = numOfKeys;
  }

  public int getMinOfArgs() {
    return minOfArgs;
  }

  public void setMinOfArgs(int minOfArgs) {
    this.minOfArgs = minOfArgs;
  }

  public int getMaxOfArgs() {
    return maxOfArgs;
  }

  public void setMaxOfArgs(int maxOfArgs) {
    this.maxOfArgs = maxOfArgs;
  }

}
