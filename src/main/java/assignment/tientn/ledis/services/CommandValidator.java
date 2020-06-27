package assignment.tientn.ledis.services;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Service;

import assignment.tientn.ledis.exception.ValidationException;
import assignment.tientn.ledis.models.Command;
import assignment.tientn.ledis.models.ECommandType;

@Service
public class CommandValidator {

  private HashMap<String, ValidCmdStructure> commandVadInfos = new HashMap<String, ValidCmdStructure>() {
    private static final long serialVersionUID = 1L;
    {
      put("get", new ValidCmdStructure(ECommandType.STRING, 1, 0, 0));
      put("set", new ValidCmdStructure(ECommandType.STRING, 1, 1, 1));

      put("llen", new ValidCmdStructure(ECommandType.LIST, 1, 0, 0));
      put("rpush", new ValidCmdStructure(ECommandType.LIST, 1, 1, Integer.MAX_VALUE - 8));
      put("lpop", new ValidCmdStructure(ECommandType.LIST, 1, 0, 0));
      put("rpop", new ValidCmdStructure(ECommandType.LIST, 1, 0, 0));
      put("lrange", new ValidCmdStructure(ECommandType.LIST, 1, 2, 2));

      put("sadd", new ValidCmdStructure(ECommandType.SET, 1, 1, Integer.MAX_VALUE - 8));
      put("srem", new ValidCmdStructure(ECommandType.SET, 1, 1, Integer.MAX_VALUE - 8));
      put("smembers", new ValidCmdStructure(ECommandType.SET, 1, 0, 0));
      put("sinter", new ValidCmdStructure(ECommandType.SET, 1, 0, Integer.MAX_VALUE - 8));

      put("keys", new ValidCmdStructure(ECommandType.EXPIRATION, 0, 0, 0));
      put("del", new ValidCmdStructure(ECommandType.EXPIRATION, 1, 0, 0));
      put("expire", new ValidCmdStructure(ECommandType.EXPIRATION, 1, 1, 1));
      put("ttl", new ValidCmdStructure(ECommandType.EXPIRATION, 1, 0, 0));

      put("save", new ValidCmdStructure(ECommandType.SET, 0, 0, 0));
      put("restore", new ValidCmdStructure(ECommandType.SET, 0, 0, 0));
    }
  };

  public CommandValidator() {
  }

  public Command checkCommand(String _text) {
    // refine chain of space characters
    String text = _text.trim().replaceAll(" +", " ");

    if (text.length() == 0) {
      return null;
    }

    List<String> rawCommand = Arrays.asList(text.split(" "));
    String CMD = rawCommand.size() > 0 ? rawCommand.get(0).toLowerCase() : null;
    String KEY = rawCommand.size() > 1 ? rawCommand.get(1) : null;
    LinkedList<String> data = new LinkedList<String>();
    for (int i = 2; i < rawCommand.size(); i++) {
      data.add(rawCommand.get(i));
    }

    ValidCmdStructure validstructure = commandVadInfos.get(CMD);

    if (validstructure == null) {
      throw new ValidationException("not recognize the command");
    }

    // command without KEY
    if (KEY == null) {
      if (validstructure.getNumOfKeys() > 0) {
        throw new ValidationException("wrong number of arguments");
      }

      return new Command(validstructure.getType(), CMD, null, null);
    }

    // command with KEY
    if (data.size() < validstructure.getMinOfArgs() || data.size() > validstructure.getMaxOfArgs()) {
      throw new ValidationException("wrong number of arguments");
    }

    if (CMD.equals("lrange")) {
      int start = getNumber(data.get(0));
      int stop = getNumber(data.get(1));

      if (start > stop) {
        throw new ValidationException("range is wrong");
      }

      if (start < 0 || stop < 0) {
        throw new ValidationException("range must be non-negative integer");
      }
    }

    if (CMD.equals("expire")) {
      int time = getNumber(data.get(0));

      if (time < 0) {
        throw new ValidationException("range is wrong");
      }
    }

    return new Command(validstructure.getType(), CMD, KEY, data);
  }

  private int getNumber(String strNum) {
    try {
      int i = Integer.parseInt(strNum);
      return i;
    } catch (NumberFormatException nfe) {
      throw new ValidationException("value is not an integer");
    }
  }
}
