package assignment.tientn.ledis.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import assignment.tientn.ledis.messages.ResponseMessage;

@ControllerAdvice
public class CacheExceptionHandler {

  @ExceptionHandler(WrongTypeException.class)
  public final ResponseEntity<ResponseMessage> handleWrongTypeException(WrongTypeException ex, WebRequest request) {

    return new ResponseEntity<ResponseMessage>(
        new ResponseMessage("WRONGTYPE Operation against a key holding the wrong kind of value"),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ValidationErrorException.class)
  public final ResponseEntity<ResponseMessage> handleValidationErrorException(ValidationErrorException ex,
      WebRequest request) {

    return new ResponseEntity<ResponseMessage>(new ResponseMessage(ex.getLocalizedMessage()), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(FileStorageException.class)
  public final ResponseEntity<ResponseMessage> handleFileStorageException(FileStorageException ex, WebRequest request) {

    return new ResponseEntity<ResponseMessage>(new ResponseMessage(ex.getLocalizedMessage()),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
