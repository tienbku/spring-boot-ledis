package assignment.tientn.ledis.exception;

public class CacheManagerException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public CacheManagerException(String exception) {
    super(exception);
  }
}
