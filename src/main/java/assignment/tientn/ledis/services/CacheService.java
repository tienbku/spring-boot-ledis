package assignment.tientn.ledis.services;

import java.util.List;

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

        List<String> range = cacheManager.listRange(key, start, stop);
        if (range == null) {
          return "(empty list or set)";
        }

        return range;
      }

    case SET:
      key = command.getKey();

      if (cmd.equals("SADD")) {
        return cacheManager.setAdd(key, command.getData());
      }
      
      if (cmd.equals("SREM")) {
        return cacheManager.setRemove(key, command.getData());
      }
      
      if (cmd.equals("SMEMBERS")) {
        return cacheManager.setMembers(key);
      }
      
    case EXPIRATION:
      if (cmd.equals("KEYS")) {
        return cacheManager.getAllKeys();
      }
      
      key = command.getKey();

      if (cmd.equals("SADD")) {
        return cacheManager.delKeys(key);
      }
      
      if (cmd.equals("EXPIRE")) {
        return cacheManager.expire(key, Integer.parseInt(command.getData().get(0)));
      }
      
      if (cmd.equals("TTL")) {
        return cacheManager.ttl(key);
      }

    default:
      break;
    }

    return null;
  }
}
