package assignment.tientn.ledis.exception;

public class CommandValidationException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public CommandValidationException(String exception) {
    super(exception);
  }
}