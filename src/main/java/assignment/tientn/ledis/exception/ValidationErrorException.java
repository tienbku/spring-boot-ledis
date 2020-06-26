package assignment.tientn.ledis.exception;

public class ValidationErrorException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public ValidationErrorException(String exception) {
    super(exception);
  }
}