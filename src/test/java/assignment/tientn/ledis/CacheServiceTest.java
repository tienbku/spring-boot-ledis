package assignment.tientn.ledis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import assignment.tientn.ledis.cache.CacheManager;
import assignment.tientn.ledis.exception.WrongTypeException;
import assignment.tientn.ledis.models.Command;
import assignment.tientn.ledis.models.ECommandType;
import assignment.tientn.ledis.services.CacheService;

@SpringBootTest
public class CacheServiceTest {

  @Autowired
  CacheService cacheService;

  @BeforeEach
  public void init() {
    CacheManager.getInstance().deleteKeys();
  }

  @Test
  public void SET_should_return_ok_for_correct_arguments() {
    ArrayList<String> data = new ArrayList<>(Arrays.asList("ledis"));

    Command command = new Command(ECommandType.STRING, "set", "name", data);
    Object actual = cacheService.execute(command);

    assertThat(actual).isEqualTo("OK");
  }

  @Test
  public void SET_should_throw_wrong_type_exception_for_existing_different_type_key() {
    ArrayList<String> listdata = new ArrayList<>(Arrays.asList("ledis", "cache"));
    CacheManager.getInstance().listRightPush("name", listdata);

    ArrayList<String> strdata = new ArrayList<>(Arrays.asList("ledis"));
    Command command = new Command(ECommandType.STRING, "set", "name", strdata);

    assertThrows(WrongTypeException.class, () -> {
      cacheService.execute(command);
    });
  }

  @Test
  public void GET_should_return_value_for_correct_key() {
    CacheManager.getInstance().stringSet("name", "ledis");

    Command command = new Command(ECommandType.STRING, "get", "name", null);
    Object actual = cacheService.execute(command);

    assertThat(actual).isEqualTo("ledis");
  }

  @Test
  public void GET_should_return_null_for_not_existing_key() {
    Command command = new Command(ECommandType.STRING, "get", "name", null);
    Object actual = cacheService.execute(command);

    assertThat(actual).isEqualTo(null);
  }

  @Test
  public void GET_should_throw_wrong_type_exception_for_existing_different_type_key() {
    ArrayList<String> listdata = new ArrayList<>(Arrays.asList("ledis", "cache"));
    CacheManager.getInstance().listRightPush("name", listdata);

    Command command = new Command(ECommandType.STRING, "get", "name", null);

    assertThrows(WrongTypeException.class, () -> {
      cacheService.execute(command);
    });
  }

  @Test
  public void LLEN_should_return_length_of_list() {
    ArrayList<String> listdata = new ArrayList<>(Arrays.asList("ledis", "cache", "well"));
    CacheManager.getInstance().listRightPush("list", listdata);

    Command command = new Command(ECommandType.LIST, "llen", "list", null);
    Object actual = cacheService.execute(command);

    assertThat(actual).isEqualTo(3);
  }

  @Test
  public void LLEN_should_return_zero_for_not_existing_list() {
    Command command = new Command(ECommandType.LIST, "llen", "list", null);
    Object actual = cacheService.execute(command);

    assertThat(actual).isEqualTo(0);
  }

  @Test
  public void LLEN_should_throw_zero_wrong_type_exception_for_existing_different_type_key() {
    CacheManager.getInstance().stringSet("list", "ledislist");

    Command command = new Command(ECommandType.LIST, "llen", "list", null);

    assertThrows(WrongTypeException.class, () -> {
      cacheService.execute(command);
    });
  }

  
}
