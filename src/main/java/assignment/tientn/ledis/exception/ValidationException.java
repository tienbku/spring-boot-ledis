package assignment.tientn.ledis.exception;

public class ValidationException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public ValidationException(String exception) {
    super(exception);
  }
}