package assignment.tientn.ledis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import assignment.tientn.ledis.messages.ResponseMessage;
import assignment.tientn.ledis.models.Command;
import assignment.tientn.ledis.services.CacheService;
import assignment.tientn.ledis.services.CommandValidator;

@RestController
public class CacheController {

  @Autowired
  CacheService cacheService;

  @Autowired
  CommandValidator validator;

  @PostMapping("/api/ledis")
  public ResponseEntity<ResponseMessage> handleCache(@RequestBody String text) {
    System.out.println(text);
    Command command = validator.checkCommand(text);

    Object response = cacheService.execute(command);

    if (response == null) {
      return new ResponseEntity<ResponseMessage>(new ResponseMessage(""), HttpStatus.NO_CONTENT);
    }
    
    System.out.println(response);
    return new ResponseEntity<ResponseMessage>(new ResponseMessage(response.toString()), HttpStatus.OK);
  }
}
