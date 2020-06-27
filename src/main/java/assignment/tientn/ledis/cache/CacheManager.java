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

import assignment.tientn.ledis.exception.CacheManagerException;
import assignment.tientn.ledis.messages.Notification;
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

    if ((_value != null) && !(_value instanceof String)) {
      throw new CacheManagerException(Notification.WRONGTYPE_MESSAGE);
    }

    store.put(key, value);
  }

  public Object stringGet(String key) {
    Object value = store.get(key);

    if (value == null) {
      return null;
    }

    if (!(value instanceof String)) {
      throw new CacheManagerException(Notification.WRONGTYPE_MESSAGE);
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
      throw new CacheManagerException(Notification.WRONGTYPE_MESSAGE);
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
      throw new CacheManagerException(Notification.WRONGTYPE_MESSAGE);
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
      throw new CacheManagerException(Notification.WRONGTYPE_MESSAGE);
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
      throw new CacheManagerException(Notification.WRONGTYPE_MESSAGE);
    }

    LinkedList<String> data = ((LinkedList<String>) value);
    return data.pollLast();
  }

  @SuppressWarnings("unchecked")
  public List<String> listRange(String key, int start, int stop) {
    Object value = store.get(key);

    if (value == null) {
      return null;
    }

    if (!(value instanceof List)) {
      throw new CacheManagerException(Notification.WRONGTYPE_MESSAGE);
    }

    LinkedList<String> data = ((LinkedList<String>) value);

    if (start >= data.size()) {
      return null;
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
      throw new CacheManagerException(Notification.WRONGTYPE_MESSAGE);
    }

    int addedCount = 0;
    set = (HashSet<String>) value;
    for (int i = 0; i < data.size(); i++) {
      if (set.add(data.get(i)))
        addedCount++;
    }

    store.put(key, set);

    return addedCount;
  }

  @SuppressWarnings("unchecked")
  public Object setRemove(String key, ArrayList<String> data) {
    Object value = store.get(key);
    HashSet<String> set;

    if (value == null) {
      return 0;
    }

    if (!(value instanceof Set)) {
      throw new CacheManagerException(Notification.WRONGTYPE_MESSAGE);
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
      return null;
    }

    if (!(value instanceof Set)) {
      throw new CacheManagerException(Notification.WRONGTYPE_MESSAGE);
    }

    return (HashSet<String>) value;
  }

  public Object getAllKeys() {
    Set<String> keys = store.keySet();
    if (keys.isEmpty())
      return null;
    return keys;
  }

  public Object delKeys(String key) {
    if (store.remove(key) == null) {
      return 0;
    }
    return 1;
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
      return (int) (timestampOut - timestamp.getTime()) / 1000;
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
  public Object restore() {
    List<String> files = storageService.getAllFiles();
    if (files.isEmpty()) {
      return null;
    }

    String file = Collections.max(files);

    store = (TreeMap<String, Object>) storageService.load(file);
    return true;
  }

  @SuppressWarnings("unchecked")
  public Object setIntersection(String key, ArrayList<String> data) {
    LinkedList<String> setKeys = new LinkedList<String>(data);
    setKeys.addFirst(key);
    List<HashSet<String>> sets = new LinkedList<HashSet<String>>();

    for (int i = 0; i < setKeys.size(); i++) {
      Object value = store.get(setKeys.get(i));
      if (value == null) {
        return null;
      }

      if (!(value instanceof Set)) {
        throw new CacheManagerException(Notification.WRONGTYPE_MESSAGE);
      }

      sets.add((HashSet<String>) value);
    }

    Set<String> interset = new HashSet<String>(sets.get(0));
    for (int i = 1; i < sets.size(); i++) {
      interset.retainAll(sets.get(i));
    }

    return interset;
  }

  public void deleteKeys() {
    store.clear();
    timestamps.clear();
  }

  public boolean deleteSnapshots() {
    return storageService.deleteAll();
  }

}
