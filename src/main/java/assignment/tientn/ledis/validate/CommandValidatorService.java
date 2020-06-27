package assignment.tientn.ledis.validate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import assignment.tientn.ledis.exception.CommandValidationException;
import assignment.tientn.ledis.models.Command;
import assignment.tientn.ledis.models.ECommandType;

@Service
public class CommandValidatorService {

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

      put("delkeys", new ValidCmdStructure(ECommandType.DELETE, 0, 0, 0));
      put("delss", new ValidCmdStructure(ECommandType.DELETE, 0, 0, 0));
    }
  };

  public CommandValidatorService() {
  }

  public Validatee checkCommand(String _text) {
    // refine chain of space characters
    String text = _text.trim().replaceAll(" +", " ");

    if (text.length() == 0) {
      return new Validatee(EValidStatus.FAIL, "empty command", null);
    }

    List<String> rawCommand = Arrays.asList(text.split(" "));
    String CMD = rawCommand.size() > 0 ? rawCommand.get(0).toLowerCase() : null;
    String KEY = rawCommand.size() > 1 ? rawCommand.get(1) : null;
    ArrayList<String> data = new ArrayList<String>();
    for (int i = 2; i < rawCommand.size(); i++) {
      data.add(rawCommand.get(i));
    }

    ValidCmdStructure validstructure = commandVadInfos.get(CMD);

    if (validstructure == null) {
      return new Validatee(EValidStatus.FAIL, "not recognize the command", null);
    }

    // command without KEY
    if (KEY == null) {
      if (validstructure.getNumOfKeys() > 0) {
        return new Validatee(EValidStatus.FAIL, "wrong number of arguments", null);
      }

      return new Validatee(EValidStatus.PASS, null, new Command(validstructure.getType(), CMD, null, null));
    }

    // command with KEY
    if (data.size() < validstructure.getMinOfArgs() || data.size() > validstructure.getMaxOfArgs()) {
      return new Validatee(EValidStatus.FAIL, "wrong number of arguments", null);
    }

    if (CMD.equals("lrange")) {
      int start = getNumber(data.get(0));
      int stop = getNumber(data.get(1));

      if (start > stop) {
        return new Validatee(EValidStatus.FAIL, "range is wrong", null);
      }

      if (start < 0 || stop < 0) {
        return new Validatee(EValidStatus.FAIL, "range must be non-negative integer", null);
      }
    }

    if (CMD.equals("expire")) {
      int time = getNumber(data.get(0));

      if (time < 0) {
        return new Validatee(EValidStatus.FAIL, "value must be non-negative integer", null);
      }
    }

    return new Validatee(EValidStatus.PASS, null, new Command(validstructure.getType(), CMD, KEY, data));
  }

  private int getNumber(String strNum) {
    try {
      int i = Integer.parseInt(strNum);
      return i;
    } catch (NumberFormatException nfe) {
      throw new CommandValidationException("value is not an integer");
    }
  }
}
