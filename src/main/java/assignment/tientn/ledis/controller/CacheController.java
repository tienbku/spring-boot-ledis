package assignment.tientn.ledis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import assignment.tientn.ledis.messages.ResponseMessage;
import assignment.tientn.ledis.services.CacheService;
import assignment.tientn.ledis.validate.CommandValidatorService;
import assignment.tientn.ledis.validate.EValidStatus;
import assignment.tientn.ledis.validate.Validatee;

@RestController
public class CacheController {

  @Autowired
  CacheService cacheService;

  @Autowired
  CommandValidatorService validatorService;

  @PostMapping("/api/ledis")
  public ResponseEntity<ResponseMessage> handleCache(@RequestBody String text) {
    Validatee validatee = validatorService.checkCommand(text);

    if (validatee.getStatus() == EValidStatus.PASS) {
      Object response = cacheService.execute(validatee.getCommand());
      return new ResponseEntity<ResponseMessage>(new ResponseMessage(response.toString()), HttpStatus.OK);
    }

    return new ResponseEntity<ResponseMessage>(new ResponseMessage(validatee.getMessage()), HttpStatus.OK);
  }
}
