package assignment.tientn.ledis.services;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Service;

import assignment.tientn.ledis.exception.ValidationErrorException;
import assignment.tientn.ledis.models.Command;
import assignment.tientn.ledis.models.ECommandType;

@Service
public class CommandValidator {

  private HashMap<String, ValidCmdStructure> commandVadInfos = new HashMap<String, ValidCmdStructure>() {
    private static final long serialVersionUID = 1L;
    {
      put("GET", new ValidCmdStructure(ECommandType.STRING, 1, 0, 0));
      put("SET", new ValidCmdStructure(ECommandType.STRING, 1, 1, 1));

      put("LLEN", new ValidCmdStructure(ECommandType.LIST, 1, 0, 0));
      put("RPUSH", new ValidCmdStructure(ECommandType.LIST, 1, 1, Integer.MAX_VALUE));
      put("LPOP", new ValidCmdStructure(ECommandType.LIST, 1, 0, 0));
      put("RPOP", new ValidCmdStructure(ECommandType.LIST, 1, 0, 0));
      put("LRANGE", new ValidCmdStructure(ECommandType.LIST, 1, 2, 2));

      put("SADD", new ValidCmdStructure(ECommandType.SET, 1, 1, Integer.MAX_VALUE));
      put("SREM", new ValidCmdStructure(ECommandType.SET, 1, 1, Integer.MAX_VALUE));
      put("SMEMBERS", new ValidCmdStructure(ECommandType.SET, 1, 0, 0));
      // put("SINTER", new VadInfo(ECommandType.SET, 1, 1, Integer.MAX_VALUE));

      put("KEYS", new ValidCmdStructure(ECommandType.EXPIRATION, 0, 0, 0));
      put("DEL", new ValidCmdStructure(ECommandType.EXPIRATION, 1, 0, 0));
      put("EXPIRE", new ValidCmdStructure(ECommandType.EXPIRATION, 1, 1, 1));
      put("TTL", new ValidCmdStructure(ECommandType.EXPIRATION, 1, 0, 0));

      put("SAVE", new ValidCmdStructure(ECommandType.SET, 0, 0, 0));
      put("RESTORE", new ValidCmdStructure(ECommandType.SET, 0, 0, 0));
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
    String CMD = rawCommand.size() > 0 ? rawCommand.get(0) : null;
    String KEY = rawCommand.size() > 1 ? rawCommand.get(1) : null;
    LinkedList<String> data = new LinkedList<String>();
    for (int i = 2; i < rawCommand.size(); i++) {
      data.add(rawCommand.get(i));
    }

    ValidCmdStructure validstructure = commandVadInfos.get(CMD);

    if (validstructure == null) {
      throw new ValidationErrorException("not recognize the command");
    }

    // command without KEY
    if (KEY == null) {
      if (validstructure.getNumOfKeys() > 0) {
        throw new ValidationErrorException("wrong number of arguments");
      }

      return new Command(validstructure.getType(), CMD, null, null);
    }

    // command with KEY
    if (data.size() < validstructure.getMinOfArgs() || data.size() > validstructure.getMaxOfArgs()) {
      throw new ValidationErrorException("wrong number of arguments");
    }

    if (CMD.equals("LRANGE")) {
      int start = getNumber(data.get(0));
      int stop = getNumber(data.get(1));

      if (start > stop) {
        throw new ValidationErrorException("range is wrong");
      }

      if (start < 0 || stop < 0) {
        throw new ValidationErrorException("range must be non-negative integer");
      }
    }

    if (CMD.equals("EXPIRE")) {
      int time = getNumber(data.get(0));

      if (time < 0) {
        throw new ValidationErrorException("range is wrong");
      }
    }

    return new Command(validstructure.getType(), CMD, KEY, data);
  }

  private int getNumber(String strNum) {
    try {
      int i = Integer.parseInt(strNum);
      return i;
    } catch (NumberFormatException nfe) {
      throw new ValidationErrorException("value is not an integer");
    }
  }
}
