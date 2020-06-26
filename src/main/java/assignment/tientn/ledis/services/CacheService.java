package assignment.tientn.ledis.services;

import java.util.LinkedList;

import org.springframework.stereotype.Service;

import assignment.tientn.ledis.cache.CacheManager;
import assignment.tientn.ledis.models.Command;

@Service
public class CacheService {

  private CacheManager cacheManager;

  public CacheService() {
    this.cacheManager = CacheManager.getInstance();
  }

  public Object execute(Command command) {
    String cmd = command.getCmd();
    String key = "";
    
    switch (command.getType()) {
    case STRING:
      key = command.getKey();

      if (cmd.equals("SET")) {
        cacheManager.stringPut(key, command.getData().get(0));
        return "OK";
      }

      if (cmd.equals("GET")) {
        return cacheManager.stringGet(key);
      }

    case LIST:
      key = command.getKey();
      
      if (cmd.equals("LLEN")) {
        return cacheManager.listLength(key);
      }
      
      if (cmd.equals("RPUSH")) {
        return cacheManager.listRightPush(key, command.getData());
      }
      
      if (cmd.equals("LPOP")) {
        return cacheManager.listLeftPop(key);
      }
      
      if (cmd.equals("RPOP")) {
        return cacheManager.listRightPop(key);
      }
      
      if (cmd.equals("LRANGE")) {
        int start = Integer.parseInt(command.getData().get(0));
        int stop = Integer.parseInt(command.getData().get(1));
        
        LinkedList<String> range = cacheManager.listRange(key, start, stop);
        if (range == null) {
          return "(empty list or set)";
        }
        
        return range;
      }
      
    default:
      break;
    }

    return command.toString();
  }
}
