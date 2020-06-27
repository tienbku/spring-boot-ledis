package assignment.tientn.ledis.services;

import java.util.List;

import org.springframework.stereotype.Service;

import assignment.tientn.ledis.cache.CacheManager;
import assignment.tientn.ledis.messages.Notification;
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

      if (cmd.equals("set")) {
        cacheManager.stringSet(key, command.getData().get(0));
        return "OK";
      }

      if (cmd.equals("get")) {
        return cacheManager.stringGet(key);
      }

      break;

    case LIST:
      key = command.getKey();

      if (cmd.equals("llen")) {
        return cacheManager.listLength(key);
      }

      if (cmd.equals("rpush")) {
        return cacheManager.listRightPush(key, command.getData());
      }

      if (cmd.equals("lpop")) {
        return cacheManager.listLeftPop(key);
      }

      if (cmd.equals("rpop")) {
        return cacheManager.listRightPop(key);
      }

      if (cmd.equals("lrange")) {
        int start = Integer.parseInt(command.getData().get(0));
        int stop = Integer.parseInt(command.getData().get(1));

        List<String> range = cacheManager.listRange(key, start, stop);
        if (range == null)
          return Notification.EMPTY_LIST_SET_MESSAGE;

        return range;
      }

      break;

    case SET:
      key = command.getKey();

      if (cmd.equals("sadd")) {
        return cacheManager.setAdd(key, command.getData());
      }

      if (cmd.equals("srem")) {
        return cacheManager.setRemove(key, command.getData());
      }

      if (cmd.equals("smembers")) {
        Object members = cacheManager.setMembers(key);
        if (members == null)
          return Notification.EMPTY_LIST_SET_MESSAGE;

        return members;
      }

      if (cmd.equals("sinter")) {
        Object intersection = cacheManager.setIntersection(key, command.getData());
        if (intersection == null)
          return Notification.EMPTY_LIST_SET_MESSAGE;

        return intersection;
      }

      break;

    case EXPIRATION:
      if (cmd.equals("keys")) {
        Object keys = cacheManager.getAllKeys();
        if (keys == null)
          return Notification.EMPTY_LIST_SET_MESSAGE;

        return keys;
      }

      key = command.getKey();

      if (cmd.equals("del")) {
        return cacheManager.delKeys(key);
      }

      if (cmd.equals("expire")) {
        return cacheManager.expire(key, Integer.parseInt(command.getData().get(0)));
      }

      if (cmd.equals("ttl")) {
        return cacheManager.ttl(key);
      }

      break;

    case SNAPSHOT:
      if (cmd.equals("save")) {
        cacheManager.save();
        return "OK";
      }

      if (cmd.equals("restore")) {
        if (cacheManager.restore() != null)
          return "OK";
        return "no snapshot";
      }

      break;

    case DELETE:
      if (cmd.equals("delkeys")) {
        cacheManager.deleteKeys();
        return "delete all keys successfully";
      }

      if (cmd.equals("delss")) {
        if (cacheManager.deleteSnapshots())
          return "delete all snapshot files successfully";
        return "could not delete snapshot files";
      }

      break;

    default:
      break;
    }

    return null;
  }
}
