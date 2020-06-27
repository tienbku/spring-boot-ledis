package assignment.tientn.ledis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import assignment.tientn.ledis.exception.ValidationException;
import assignment.tientn.ledis.models.Command;
import assignment.tientn.ledis.models.ECommandType;
import assignment.tientn.ledis.services.CommandValidator;

@SpringBootTest
public class CommandValidatorTest {

  @Autowired
  CommandValidator commandValidator;

  @Test
  void should_return_null_for_empty_input() {
    String text = "";
    Command actual = commandValidator.checkCommand(text);

    assertThat(actual).isEqualTo(null);
  }

  @Test
  public void should_return_command_object() {
    String text = "RPUSH list a b c";
    Command actual = commandValidator.checkCommand(text);

    ArrayList<String> data = new ArrayList<>(Arrays.asList("a","b","c"));

    Command expected = new Command(ECommandType.LIST, "rpush", "list", data);

    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
  }

  @Test
  public void should_return_command_object_for_multiple_spaces_input() {
    String text = "RPUSH   list   a  b   c";
    Command actual = commandValidator.checkCommand(text);

    ArrayList<String> data = new ArrayList<>(Arrays.asList("a","b","c"));

    Command expected = new Command(ECommandType.LIST, "rpush", "list", data);

    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
  }

  @Test
  public void should_throw_exception_for_wrong_command_input() {
    Exception exception = assertThrows(ValidationException.class, () -> {
      commandValidator.checkCommand("dump");
    });

    String expectedMessage = "not recognize the command";
    String actualMessage = exception.getMessage();

    assertEquals(actualMessage, expectedMessage);
  }
  
  @Test
  public void should_throw_exception_for_wrong_number_arguments_input_no_key_command() {
    Exception exception = assertThrows(ValidationException.class, () -> {
      commandValidator.checkCommand("SET foo bar far");
    });

    String expectedMessage = "wrong number of arguments";
    String actualMessage = exception.getMessage();

    assertEquals(actualMessage, expectedMessage);
  }
  
  @Test
  public void should_throw_exception_for_wrong_number_arguments_input_command_no_key() {
    Exception exception = assertThrows(ValidationException.class, () -> {
      commandValidator.checkCommand("SMEMBERS");
    });

    String expectedMessage = "wrong number of arguments";
    String actualMessage = exception.getMessage();

    assertEquals(actualMessage, expectedMessage);
  }
  
  @Test
  public void should_throw_exception_for_more_number_arguments_input_command_with_key() {
    Exception exception = assertThrows(ValidationException.class, () -> {
      commandValidator.checkCommand("SET name Ledis cache");
    });

    String expectedMessage = "wrong number of arguments";
    String actualMessage = exception.getMessage();

    assertEquals(actualMessage, expectedMessage);
  }
  
  @Test
  public void should_throw_exception_for_less_number_arguments_input_command_with_key() {
    Exception exception = assertThrows(ValidationException.class, () -> {
      commandValidator.checkCommand("RPUSH mylist");
    });

    String expectedMessage = "wrong number of arguments";
    String actualMessage = exception.getMessage();

    assertEquals(actualMessage, expectedMessage);
  }
  
  @Test
  public void should_throw_exception_for_list_wrong_format_range_input() {
    Exception exception = assertThrows(ValidationException.class, () -> {
      commandValidator.checkCommand("LRANGE mylist a 2");
    });

    String expectedMessage = "value is not an integer";
    String actualMessage = exception.getMessage();

    assertEquals(actualMessage, expectedMessage);
  }
  
  @Test
  public void should_throw_exception_for_list_wrong_range_input() {
    Exception exception = assertThrows(ValidationException.class, () -> {
      commandValidator.checkCommand("LRANGE mylist 6 2");
    });

    String expectedMessage = "range is wrong";
    String actualMessage = exception.getMessage();

    assertEquals(actualMessage, expectedMessage);
  }
  
  @Test
  public void should_throw_exception_for_list_wrong_value_range_input() {
    Exception exception = assertThrows(ValidationException.class, () -> {
      commandValidator.checkCommand("LRANGE mylist -2 2");
    });

    String expectedMessage = "range must be non-negative integer";
    String actualMessage = exception.getMessage();

    assertEquals(actualMessage, expectedMessage);
  }
  
  @Test
  public void should_throw_exception_for_expire_wrong_value_input() {
    Exception exception = assertThrows(ValidationException.class, () -> {
      commandValidator.checkCommand("EXPIRE key -2");
    });

    String expectedMessage = "value must be non-negative integer";
    String actualMessage = exception.getMessage();

    assertEquals(actualMessage, expectedMessage);
  }
}
