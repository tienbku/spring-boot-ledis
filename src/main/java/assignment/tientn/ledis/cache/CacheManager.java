package assignment.tientn.ledis.cache;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;

import assignment.tientn.ledis.exception.EmptyListSetException;
import assignment.tientn.ledis.exception.WrongTypeException;
import assignment.tientn.ledis.services.FilesStorageService;

public class CacheManager {

  private TreeMap<String, Object> store = new TreeMap<>();
  private TreeMap<String, Long> timestamps = new TreeMap<>();

  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
  private FilesStorageService storageService = new FilesStorageService();;

  private static CacheManager instance;

  private CacheManager() {
  }

  public static CacheManager getInstance() {
    if (instance == null) {
      synchronized (CacheManager.class) {
        if (instance == null) {
          instance = new CacheManager();
        }
      }
    }
    return instance;
  }

  public void stringSet(String key, String value) {
    Object _value = store.get(key);

    if (_value == null) {
      store.put(key, value);
      return;
    }

    if (!(_value instanceof String)) {
      throw new WrongTypeException();
    }
  }

  public Object stringGet(String key) {
    Object value = store.get(key);

    if (value == null) {
      return null;
    }

    if (!(value instanceof String)) {
      throw new WrongTypeException();
    }

    return value;
  }

  @SuppressWarnings("unchecked")
  public Object listLength(String key) {
    Object value = store.get(key);

    if (value == null) {
      return 0;
    }

    if (!(value instanceof List)) {
      throw new WrongTypeException();
    }

    return ((LinkedList<String>) value).size();
  }

  @SuppressWarnings("unchecked")
  public int listRightPush(String key, ArrayList<String> data) {
    Object value = store.get(key);

    if (value == null) {
      store.put(key, new LinkedList<String>(data));
      return data.size();
    }

    if (!(value instanceof List)) {
      throw new WrongTypeException();
    }

    LinkedList<String> _data = ((LinkedList<String>) value);
    _data.addAll(data);

    return _data.size();
  }

  @SuppressWarnings("unchecked")
  public Object listLeftPop(String key) {
    Object value = store.get(key);

    if (value == null) {
      return null;
    }

    if (!(value instanceof List)) {
      throw new WrongTypeException();
    }

    LinkedList<String> data = ((LinkedList<String>) value);
    return data.pollFirst();
  }

  @SuppressWarnings("unchecked")
  public Object listRightPop(String key) {
    Object value = store.get(key);

    if (value == null) {
      return null;
    }

    if (!(value instanceof List)) {
      throw new WrongTypeException();
    }

    LinkedList<String> data = ((LinkedList<String>) value);
    return data.pollLast();
  }

  @SuppressWarnings("unchecked")
  public List<String> listRange(String key, int start, int stop) {
    Object value = store.get(key);

    if (value == null) {
      throw new EmptyListSetException();
    }

    if (!(value instanceof List)) {
      throw new WrongTypeException();
    }

    LinkedList<String> data = ((LinkedList<String>) value);

    if (start >= data.size()) {
      throw new EmptyListSetException();
    }

    if (stop + 1 > data.size()) {
      stop = data.size() - 1;
    }

    return data.subList(start, stop + 1);
  }

  @SuppressWarnings("unchecked")
  public int setAdd(String key, ArrayList<String> data) {
    Object value = store.get(key);
    HashSet<String> set;

    if (value == null) {
      set = new HashSet<String>(data);
      store.put(key, set);
      return set.size();
    }

    if (!(value instanceof Set)) {
      throw new WrongTypeException();
    }

    set = (HashSet<String>) value;
    set.addAll(data);

    store.put(key, set);

    return set.size();
  }

  @SuppressWarnings("unchecked")
  public Object setRemove(String key, ArrayList<String> data) {
    Object value = store.get(key);
    HashSet<String> set;

    if (value == null) {
      return 0;
    }

    if (!(value instanceof Set)) {
      throw new WrongTypeException();
    }

    set = (HashSet<String>) value;
    int removedCount = 0;
    for (int i = 0; i < data.size(); i++) {
      if (set.remove(data.get(i)))
        removedCount++;
    }

    return removedCount;
  }

  @SuppressWarnings("unchecked")
  public HashSet<String> setMembers(String key) {
    Object value = store.get(key);

    if (value == null) {
      throw new EmptyListSetException();
    }

    if (!(value instanceof Set)) {
      throw new WrongTypeException();
    }

    return (HashSet<String>) value;
  }

  public Object getAllKeys() {
    return store.keySet();
  }

  public Object delKeys(String key) {
    return store.remove(key);
  }

  public Object expire(String key, int time) {
    Object value = store.get(key);

    if (value == null) {
      return 0;
    }

    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    long timestampOut = timestamp.getTime() + time * 1000;
    timestamps.put(key, timestampOut);

    scheduler.schedule(new Runnable() {
      public void run() {
        store.remove(key);
        timestamps.remove(key);
      }
    }, time, SECONDS);

    return time;
  }

  public Object ttl(String key) {
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    Long timestampOut = timestamps.get(key);
    if (timestampOut != null) {
      return (timestampOut - timestamp.getTime()) / 1000;
    }

    if (store.get(key) != null) {
      return -1;
    }

    return -2;
  }

  public void save() {
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    String file = "snapshot-" + sdf.format(timestamp);
    storageService.save(file, store);
  }

  @SuppressWarnings("unchecked")
  public void restore() {
    List<String> files = storageService.getAllFiles();
    String file = Collections.max(files);

    store = (TreeMap<String, Object>) storageService.load(file);
  }

  @SuppressWarnings("unchecked")
  public Object setIntersection(String key, ArrayList<String> data) {
    LinkedList<String> setKeys = new LinkedList<String>(data);
    setKeys.addFirst(key);
    List<HashSet<String>> sets = new LinkedList<HashSet<String>>();

    setKeys.forEach(setKey -> {
      Object value = store.get(setKey);
      if (value == null) {
        throw new EmptyListSetException();
      }

      if (!(value instanceof Set)) {
        throw new WrongTypeException();
      }

      sets.add((HashSet<String>) value);
    });

    Set<String> interset = new HashSet<String>(sets.get(0));
    for (int i = 1; i < sets.size(); i++) {
      interset.retainAll(sets.get(i));
    }

    return interset;
  }

  public void deleteKeys() {
    store.clear();
  }

  public boolean deleteSnapshots() {
    return storageService.deleteAll();
  }

}
