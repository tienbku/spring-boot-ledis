package assignment.tientn.ledis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import assignment.tientn.ledis.cache.CacheManager;
import assignment.tientn.ledis.exception.CacheManagerException;
import assignment.tientn.ledis.messages.Notification;
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

    Exception exception = assertThrows(CacheManagerException.class, () -> {
      cacheService.execute(command);
    });

    String expectedMessage = Notification.WRONGTYPE_MESSAGE;
    String actualMessage = exception.getLocalizedMessage();

    assertEquals(actualMessage, expectedMessage);
  }

  @Test
  public void GET_should_return_value_for_correct_key() {
    CacheManager.getInstance().stringSet("name", "ledis");

    Command command = new Command(ECommandType.STRING, "get", "name", null);
    Object actual = cacheService.execute(command);

    assertThat(actual).isEqualTo("ledis");
  }

  @Test
  public void GET_should_return_null_for_key_not_existing() {
    Command command = new Command(ECommandType.STRING, "get", "name", null);
    Object actual = cacheService.execute(command);

    assertThat(actual).isEqualTo(null);
  }

  @Test
  public void GET_should_throw_wrong_type_exception_for_existing_different_type_key() {
    ArrayList<String> listdata = new ArrayList<>(Arrays.asList("ledis", "cache"));
    CacheManager.getInstance().listRightPush("name", listdata);

    Command command = new Command(ECommandType.STRING, "get", "name", null);

    Exception exception = assertThrows(CacheManagerException.class, () -> {
      cacheService.execute(command);
    });

    String expectedMessage = Notification.WRONGTYPE_MESSAGE;
    String actualMessage = exception.getLocalizedMessage();

    assertEquals(actualMessage, expectedMessage);
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
  public void LLEN_should_throw_wrong_type_exception_for_existing_different_type_key() {
    CacheManager.getInstance().stringSet("list", "ledislist");

    Command command = new Command(ECommandType.LIST, "llen", "list", null);

    Exception exception = assertThrows(CacheManagerException.class, () -> {
      cacheService.execute(command);
    });

    String expectedMessage = Notification.WRONGTYPE_MESSAGE;
    String actualMessage = exception.getLocalizedMessage();

    assertEquals(actualMessage, expectedMessage);
  }

  @Test
  public void RPUSH_should_return_length_of_list_input() {
    ArrayList<String> data = new ArrayList<>(Arrays.asList("ledis", "cache", "well"));

    Command command = new Command(ECommandType.LIST, "rpush", "list", data);
    Object actual = cacheService.execute(command);

    assertThat(actual).isEqualTo(3);
  }

  @Test
  public void RPUSH_should_throw_wrong_type_exception_for_existing_different_type_key() {
    CacheManager.getInstance().stringSet("list", "ledislist");

    Command command = new Command(ECommandType.LIST, "rpush", "list", null);

    Exception exception = assertThrows(CacheManagerException.class, () -> {
      cacheService.execute(command);
    });

    String expectedMessage = Notification.WRONGTYPE_MESSAGE;
    String actualMessage = exception.getLocalizedMessage();

    assertEquals(actualMessage, expectedMessage);
  }

  @Test
  public void LPOP_should_remove_and_return_first_item_of_list() {
    ArrayList<String> listdata = new ArrayList<>(Arrays.asList("ledis", "cache", "well"));
    CacheManager.getInstance().listRightPush("list", listdata);

    Command command = new Command(ECommandType.LIST, "lpop", "list", null);
    Object actual = cacheService.execute(command);

    Object length = CacheManager.getInstance().listLength("list");

    assertThat(length).isEqualTo(2);
    assertThat(actual).isEqualTo("ledis");
  }

  @Test
  public void LPOP_should_return_null_for_empty_list() {
    ArrayList<String> listdata = new ArrayList<>();
    CacheManager.getInstance().listRightPush("list", listdata);

    Command command = new Command(ECommandType.LIST, "lpop", "list", null);
    Object actual = cacheService.execute(command);

    assertThat(actual).isEqualTo(null);
  }

  @Test
  public void LPOP_should_throw_wrong_type_exception_for_existing_different_type_key() {
    CacheManager.getInstance().stringSet("list", "ledislist");

    Command command = new Command(ECommandType.LIST, "lpop", "list", null);

    Exception exception = assertThrows(CacheManagerException.class, () -> {
      cacheService.execute(command);
    });

    String expectedMessage = Notification.WRONGTYPE_MESSAGE;
    String actualMessage = exception.getLocalizedMessage();

    assertEquals(actualMessage, expectedMessage);
  }

  @Test
  public void RPOP_should_remove_and_return_first_item_of_list() {
    ArrayList<String> listdata = new ArrayList<>(Arrays.asList("ledis", "cache", "well"));
    CacheManager.getInstance().listRightPush("list", listdata);

    Command command = new Command(ECommandType.LIST, "rpop", "list", null);
    Object actual = cacheService.execute(command);

    Object length = CacheManager.getInstance().listLength("list");

    assertThat(length).isEqualTo(2);
    assertThat(actual).isEqualTo("well");
  }

  @Test
  public void RPOP_should_return_null_for_empty_list() {
    ArrayList<String> listdata = new ArrayList<>();
    CacheManager.getInstance().listRightPush("list", listdata);

    Command command = new Command(ECommandType.LIST, "rpop", "list", null);
    Object actual = cacheService.execute(command);

    assertThat(actual).isEqualTo(null);
  }

  @Test
  public void RPOP_should_throw_wrong_type_exception_for_existing_different_type_key() {
    CacheManager.getInstance().stringSet("list", "ledislist");

    Command command = new Command(ECommandType.LIST, "rpop", "list", null);

    Exception exception = assertThrows(CacheManagerException.class, () -> {
      cacheService.execute(command);
    });

    String expectedMessage = Notification.WRONGTYPE_MESSAGE;
    String actualMessage = exception.getLocalizedMessage();

    assertEquals(actualMessage, expectedMessage);
  }

  @Test
  public void LRANGE_should_return_list_items_in_range() {
    ArrayList<String> data = new ArrayList<>(Arrays.asList("ledis", "cache", "well", "let", "go", "caching"));
    CacheManager.getInstance().listRightPush("list", data);

    ArrayList<String> range = new ArrayList<>(Arrays.asList("2", "4"));
    Command command = new Command(ECommandType.LIST, "lrange", "list", range);
    Object actual = cacheService.execute(command);

    List<String> expected = Arrays.asList("well", "let", "go");

    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
  }

  @Test
  public void LRANGE_should_return_list_items_to_max_index_if_stop_larger_than_index() {
    ArrayList<String> data = new ArrayList<>(Arrays.asList("ledis", "cache", "well", "let", "go", "caching"));
    CacheManager.getInstance().listRightPush("list", data);

    ArrayList<String> range = new ArrayList<>(Arrays.asList("2", "12"));
    Command command = new Command(ECommandType.LIST, "lrange", "list", range);
    Object actual = cacheService.execute(command);

    List<String> expected = Arrays.asList("well", "let", "go", "caching");

    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
  }

  @Test
  public void LRANGE_should_throw_wrong_type_exception_for_existing_different_type_key() {
    CacheManager.getInstance().stringSet("list", "ledislist");

    ArrayList<String> range = new ArrayList<>(Arrays.asList("2", "4"));
    Command command = new Command(ECommandType.LIST, "lrange", "list", range);

    Exception exception = assertThrows(CacheManagerException.class, () -> {
      cacheService.execute(command);
    });

    String expectedMessage = Notification.WRONGTYPE_MESSAGE;
    String actualMessage = exception.getLocalizedMessage();

    assertEquals(actualMessage, expectedMessage);
  }

  @Test
  public void LRANGE_should_return_empty_list_set_message_for_range_outside() {
    ArrayList<String> data = new ArrayList<>(Arrays.asList("ledis", "cache", "well"));
    CacheManager.getInstance().listRightPush("list", data);

    ArrayList<String> range = new ArrayList<>(Arrays.asList("5", "8"));
    Command command = new Command(ECommandType.LIST, "lrange", "list", range);

    Object actual = cacheService.execute(command);

    assertThat(actual).isEqualTo(Notification.EMPTY_LIST_SET_MESSAGE);
  }

  @Test
  public void LRANGE_should_return_empty_list_set_message_for_empty_list() {
    ArrayList<String> data = new ArrayList<>(Arrays.asList());
    CacheManager.getInstance().listRightPush("list", data);

    ArrayList<String> range = new ArrayList<>(Arrays.asList("0", "2"));
    Command command = new Command(ECommandType.LIST, "lrange", "list", range);

    Object actual = cacheService.execute(command);

    assertThat(actual).isEqualTo(Notification.EMPTY_LIST_SET_MESSAGE);
  }

  @Test
  public void LRANGE_should_return_empty_list_set_message_for_key_not_existing() {
    ArrayList<String> range = new ArrayList<>(Arrays.asList("0", "2"));
    Command command = new Command(ECommandType.LIST, "lrange", "list", range);

    Object actual = cacheService.execute(command);

    assertThat(actual).isEqualTo(Notification.EMPTY_LIST_SET_MESSAGE);
  }

  @Test
  public void SADD_should_return_number_items_input_to_set() {
    ArrayList<String> data = new ArrayList<>(Arrays.asList("ledis", "cache", "well"));

    Command command = new Command(ECommandType.SET, "sadd", "set", data);
    Object actual = cacheService.execute(command);

    assertThat(actual).isEqualTo(3);
  }

  @Test
  public void SADD_should_return_number_items_input_to_existing_set() {
    ArrayList<String> data = new ArrayList<>(Arrays.asList("ledis", "cache", "well"));
    CacheManager.getInstance().setAdd("set", data);

    ArrayList<String> items = new ArrayList<>(Arrays.asList("well", "let", "go"));
    Command command = new Command(ECommandType.SET, "sadd", "set", items);
    Object actual = cacheService.execute(command);

    assertThat(actual).isEqualTo(2);
  }

  @Test
  public void SADD_should_throw_wrong_type_exception_for_existing_different_type_key() {
    CacheManager.getInstance().stringSet("set", "ledisset");

    ArrayList<String> items = new ArrayList<>(Arrays.asList("well", "let", "go"));
    Command command = new Command(ECommandType.SET, "sadd", "set", items);

    Exception exception = assertThrows(CacheManagerException.class, () -> {
      cacheService.execute(command);
    });

    String expectedMessage = Notification.WRONGTYPE_MESSAGE;
    String actualMessage = exception.getLocalizedMessage();

    assertEquals(actualMessage, expectedMessage);
  }

  @Test
  public void SREM_should_return_number_items_to_be_removed() {
    ArrayList<String> data = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5"));
    CacheManager.getInstance().setAdd("set", data);

    ArrayList<String> items = new ArrayList<>(Arrays.asList("4", "5", "6"));
    Command command = new Command(ECommandType.SET, "srem", "set", items);
    Object actual = cacheService.execute(command);

    assertThat(actual).isEqualTo(2);
  }

  @Test
  public void SREM_should_return_zero_for_not_existing_set() {
    ArrayList<String> data = new ArrayList<>(Arrays.asList());
    CacheManager.getInstance().setAdd("set", data);

    ArrayList<String> items = new ArrayList<>(Arrays.asList("4", "5", "6"));
    Command command = new Command(ECommandType.SET, "srem", "set", items);
    Object actual = cacheService.execute(command);

    assertThat(actual).isEqualTo(0);
  }

  @Test
  public void SREM_should_throw_wrong_type_exception_for_existing_different_type_key() {
    CacheManager.getInstance().stringSet("set", "ledisset");

    ArrayList<String> items = new ArrayList<>(Arrays.asList("well", "let", "go"));
    Command command = new Command(ECommandType.SET, "srem", "set", items);

    Exception exception = assertThrows(CacheManagerException.class, () -> {
      cacheService.execute(command);
    });

    String expectedMessage = Notification.WRONGTYPE_MESSAGE;
    String actualMessage = exception.getLocalizedMessage();

    assertEquals(actualMessage, expectedMessage);
  }

  @Test
  public void SMEMBERS_should_return_all_items_for_correct_key_input() {
    ArrayList<String> data = new ArrayList<>(Arrays.asList("ledis", "cache", "well"));
    CacheManager.getInstance().setAdd("set", data);

    Command command = new Command(ECommandType.SET, "smembers", "set", null);
    Object actual = cacheService.execute(command);

    HashSet<String> expected = new HashSet<>(Arrays.asList("ledis", "cache", "well"));

    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
  }

  @Test
  public void SMEMBERS_should_return_empty_list_set_message_for_key_not_existing() {
    Command command = new Command(ECommandType.SET, "smembers", "set", null);

    Object actual = cacheService.execute(command);

    assertThat(actual).isEqualTo(Notification.EMPTY_LIST_SET_MESSAGE);
  }

  @Test
  public void SMEMBERS_should_throw_wrong_type_exception_for_existing_different_type_key() {
    CacheManager.getInstance().stringSet("set", "ledisset");

    Command command = new Command(ECommandType.SET, "smembers", "set", null);

    Exception exception = assertThrows(CacheManagerException.class, () -> {
      cacheService.execute(command);
    });

    String expectedMessage = Notification.WRONGTYPE_MESSAGE;
    String actualMessage = exception.getLocalizedMessage();

    assertEquals(actualMessage, expectedMessage);
  }

  @Test
  public void SINTER_should_return_intersection_for_correct_set_keys_input() {
    ArrayList<String> data1 = new ArrayList<>(
        Arrays.asList("Apple", "Avocado", "Banana", "Coconut", "Durian", "Orange", "Feijoa"));
    CacheManager.getInstance().setAdd("set1", data1);

    ArrayList<String> data2 = new ArrayList<>(
        Arrays.asList("Apple", "Coconut", "Kiwifruit", "Kumquat", "Lemon", "Orange"));
    CacheManager.getInstance().setAdd("set2", data2);

    ArrayList<String> data3 = new ArrayList<>(Arrays.asList("Apple", "Blackberry", "Currant", "Cherry", "Coconut"));
    CacheManager.getInstance().setAdd("set3", data3);

    ArrayList<String> sets = new ArrayList<>(Arrays.asList("set2", "set3"));
    Command command = new Command(ECommandType.SET, "sinter", "set1", sets);
    Object actual = cacheService.execute(command);

    HashSet<String> expected = new HashSet<>(Arrays.asList("Apple", "Coconut"));

    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
  }

  @Test
  public void SINTER_should_return_all_items_for_only_one_set_input() {
    ArrayList<String> data1 = new ArrayList<>(
        Arrays.asList("Apple", "Avocado", "Banana", "Coconut", "Durian", "Orange", "Feijoa"));
    CacheManager.getInstance().setAdd("set1", data1);

    Command command = new Command(ECommandType.SET, "sinter", "set1", new ArrayList<>());
    Object actual = cacheService.execute(command);

    HashSet<String> expected = new HashSet<>(data1);

    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
  }

  @Test
  public void SINTER_should_throw_wrong_type_exception_for_any_different_type_key() {
    ArrayList<String> data1 = new ArrayList<>(
        Arrays.asList("Apple", "Avocado", "Banana", "Coconut", "Durian", "Orange", "Feijoa"));
    CacheManager.getInstance().setAdd("set1", data1);
    CacheManager.getInstance().stringSet("set", "ledisset");

    ArrayList<String> sets = new ArrayList<>(Arrays.asList("set2", "set"));
    Command command = new Command(ECommandType.SET, "sinter", "set", sets);

    Exception exception = assertThrows(CacheManagerException.class, () -> {
      cacheService.execute(command);
    });

    String expectedMessage = Notification.WRONGTYPE_MESSAGE;
    String actualMessage = exception.getLocalizedMessage();

    assertEquals(actualMessage, expectedMessage);
  }

  @Test
  public void SINTER_should_return_empty_list_set_message_for_any_key_not_existing() {
    ArrayList<String> data1 = new ArrayList<>(
        Arrays.asList("Apple", "Avocado", "Banana", "Coconut", "Durian", "Orange", "Feijoa"));
    CacheManager.getInstance().setAdd("set1", data1);

    ArrayList<String> sets = new ArrayList<>(Arrays.asList("set2", "set"));
    Command command = new Command(ECommandType.SET, "sinter", "set", sets);

    Object actual = cacheService.execute(command);

    assertThat(actual).isEqualTo(Notification.EMPTY_LIST_SET_MESSAGE);
  }

  @Test
  public void KEYS_should_return_all_available_keys_sorted() {
    ArrayList<String> data = new ArrayList<>(Arrays.asList("ledis", "cache", "well"));
    CacheManager.getInstance().setAdd("set", data);
    CacheManager.getInstance().listRightPush("list", data);
    CacheManager.getInstance().stringSet("string", "ledis");
    CacheManager.getInstance().stringSet("another", "cache");

    Command command = new Command(ECommandType.EXPIRATION, "keys", null, null);
    Object actual = cacheService.execute(command);

    HashSet<String> expected = new HashSet<>(Arrays.asList("another", "list", "set", "string"));

    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
  }

  @Test
  public void KEYS_should_return_empty_list_set_message_for_no_key() {
    Command command = new Command(ECommandType.EXPIRATION, "keys", null, null);

    Object actual = cacheService.execute(command);

    assertThat(actual).isEqualTo(Notification.EMPTY_LIST_SET_MESSAGE);
  }

  @Test
  public void DEL_should_delete_and_return_1_for_correct_key() {
    CacheManager.getInstance().stringSet("name", "ledis");

    Command command = new Command(ECommandType.EXPIRATION, "del", "name", null);
    Object actual = cacheService.execute(command);

    assertThat(actual).isEqualTo(1);
  }

  @Test
  public void DEL_should_delete_and_return_0_for_key_not_existing() {
    Command command = new Command(ECommandType.EXPIRATION, "del", "name", null);
    Object actual = cacheService.execute(command);

    assertThat(actual).isEqualTo(0);
  }

  @Test
  public void EXPIRE_should_return_number_for_correct_key() {
    CacheManager.getInstance().stringSet("name", "ledis");

    ArrayList<String> data = new ArrayList<>(Arrays.asList("10"));
    Command command = new Command(ECommandType.EXPIRATION, "expire", "name", data);
    Object actual = cacheService.execute(command);

    assertThat(actual).isEqualTo(10);
  }

  @Test
  public void EXPIRE_should_return_0_for_key_not_existing() {
    ArrayList<String> data = new ArrayList<>(Arrays.asList("10"));
    Command command = new Command(ECommandType.EXPIRATION, "expire", "name", data);
    Object actual = cacheService.execute(command);

    assertThat(actual).isEqualTo(0);
  }

  @Test
  public void TTL_should_return_timeout_for_correct_key() {
    CacheManager.getInstance().stringSet("name", "ledis");
    CacheManager.getInstance().expire("name", 10);

    Command command = new Command(ECommandType.EXPIRATION, "ttl", "name", null);
    Object actual = cacheService.execute(command);

    assertThat(actual).isInstanceOf(Integer.class);
  }

  @Test
  public void TTL_should_return_Neg2_for_key_not_existing() {
    Command command = new Command(ECommandType.EXPIRATION, "ttl", "name", null);
    Object actual = cacheService.execute(command);

    assertThat(actual).isEqualTo(-2);
  }

  @Test
  public void TTL_should_return_Neg1_for_not_set_expire_key() {
    CacheManager.getInstance().stringSet("name", "ledis");

    Command command = new Command(ECommandType.EXPIRATION, "ttl", "name", null);
    Object actual = cacheService.execute(command);

    assertThat(actual).isEqualTo(-1);
  }
}
