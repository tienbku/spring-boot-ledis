package assignment.tientn.ledis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import assignment.tientn.ledis.exception.CommandValidationException;
import assignment.tientn.ledis.messages.Notification;
import assignment.tientn.ledis.models.Command;
import assignment.tientn.ledis.models.ECommandType;
import assignment.tientn.ledis.validate.CommandValidator;
import assignment.tientn.ledis.validate.EValidStatus;
import assignment.tientn.ledis.validate.Validatee;

@SpringBootTest
public class CommandValidatorTest {

  @Autowired
  CommandValidator commandValidator;

  @Test
  void should_return_null_for_empty_input() {
    String text = "";
    Command actual = commandValidator.checkCommand(text).getCommand();

    assertThat(actual).isEqualTo(null);
  }

  @Test
  public void should_return_command_object() {
    String text = "RPUSH list a b c";
    Command actual = commandValidator.checkCommand(text).getCommand();

    ArrayList<String> data = new ArrayList<>(Arrays.asList("a", "b", "c"));

    Command expected = new Command(ECommandType.LIST, "rpush", "list", data);

    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
  }

  @Test
  public void should_return_command_object_for_multiple_spaces_input() {
    String text = "RPUSH   list   a  b   c";
    Command actual = commandValidator.checkCommand(text).getCommand();

    ArrayList<String> data = new ArrayList<>(Arrays.asList("a", "b", "c"));

    Command expected = new Command(ECommandType.LIST, "rpush", "list", data);

    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
  }

  @Test
  public void should_return_null_for_wrong_command_input() {
    String text = "dump";
    Command actual = commandValidator.checkCommand(text).getCommand();

    assertThat(actual).isEqualTo(null);
  }

  @Test
  public void should_return_wrong_number_arguments_for_command_with_excess_key() {
    String text = "SET foo bar far";
    Validatee actual = commandValidator.checkCommand(text);

    Validatee expected = new Validatee(EValidStatus.FAIL, Notification.WRONG_NUMBER_ARGS, null);

    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
  }

  @Test
  public void should_return_wrong_number_arguments_for_command_no_key() {
    String text = "SMEMBERS";
    Validatee actual = commandValidator.checkCommand(text);

    Validatee expected = new Validatee(EValidStatus.FAIL, Notification.WRONG_NUMBER_ARGS, null);

    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
  }

  @Test
  public void should_return_wrong_number_arguments_for_command_no_need_key() {
    String text = "KEYS ledis";
    Validatee actual = commandValidator.checkCommand(text);

    Validatee expected = new Validatee(EValidStatus.FAIL, Notification.WRONG_NUMBER_ARGS, null);

    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
  }

  @Test
  public void should_return_wrong_number_arguments_for_less_number_arguments() {
    String text = "RPUSH mylist";
    Validatee actual = commandValidator.checkCommand(text);

    Validatee expected = new Validatee(EValidStatus.FAIL, Notification.WRONG_NUMBER_ARGS, null);

    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
  }

  @Test
  public void should_throw_exception_for_list_wrong_format_range_input() {
    Exception exception = assertThrows(CommandValidationException.class, () -> {
      commandValidator.checkCommand("LRANGE mylist a 2");
    });

    String expectedMessage = Notification.VALUE_NOT_NUMBER;
    String actualMessage = exception.getMessage();

    assertEquals(actualMessage, expectedMessage);
  }

  @Test
  public void should_return_message_for_list_wrong_range_input() {
    String text = "LRANGE mylist 6 2";
    Validatee actual = commandValidator.checkCommand(text);

    Validatee expected = new Validatee(EValidStatus.FAIL, Notification.WRONG_RANGE, null);

    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
  }

  @Test
  public void should_return_message_for_list_wrong_value_range_input() {
    String text = "LRANGE mylist -2 2";
    Validatee actual = commandValidator.checkCommand(text);

    Validatee expected = new Validatee(EValidStatus.FAIL, Notification.RANGE_NEG_NUMBER, null);

    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
  }

  @Test
  public void should_return_message_for_expire_wrong_value_input() {
    String text = "EXPIRE key -2";
    Validatee actual = commandValidator.checkCommand(text);

    Validatee expected = new Validatee(EValidStatus.FAIL, Notification.VALUE_NEG_NUMBER, null);

    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
  }
}
