package assignment.tientn.ledis.validate;

import assignment.tientn.ledis.models.Command;

public class Validatee {
  private EValidStatus status;
  private String message;
  private Command command;

  public Validatee(EValidStatus status, String message, Command command) {
    this.status = status;
    this.message = message;
    this.command = command;
  }

  public EValidStatus getStatus() {
    return status;
  }

  public void setStatus(EValidStatus status) {
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Command getCommand() {
    return command;
  }

  public void setCommand(Command command) {
    this.command = command;
  }

}
