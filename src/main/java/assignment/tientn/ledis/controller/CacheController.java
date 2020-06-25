package assignment.tientn.ledis.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CacheController {

  @PostMapping("/api/ledis")
  public ResponseEntity<?> handleCache(@RequestBody String command) {
    System.out.println(command);
    return ResponseEntity.ok(command);
  }
}
