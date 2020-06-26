package assignment.tientn.ledis.exception;

public class FileStorageException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public FileStorageException(String exception) {
    super(exception);
  }
}
