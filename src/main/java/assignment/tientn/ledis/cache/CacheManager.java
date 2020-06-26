package assignment.tientn.ledis.cache;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import assignment.tientn.ledis.exception.WrongTypeException;

public class CacheManager {

  private final TreeMap<String, Object> store = new TreeMap<>();
  private final TreeMap<String, Long> timestamps = new TreeMap<>();

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

  // public V get(Object key) {
  // // TODO Auto-generated method stub
  // return null;
  // }
  //

  public void stringPut(String key, String value) {
    store.put(key, value);
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
  public int listLength(String key) {
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
  public int listRightPush(String key, LinkedList<String> data) {
    Object value = store.get(key);

    if (value == null) {
      store.put(key, data);
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
  public LinkedList<String> listRange(String key, int start, int stop) {
    Object value = store.get(key);

    if (value == null) {
      return null;
    }

    if (!(value instanceof List)) {
      throw new WrongTypeException();
    }
    
    LinkedList<String> data = ((LinkedList<String>) value);
    
    if (stop + 1 > data.size()) {
      stop = data.size() - 1;
    }
    
    return (LinkedList<String>) data.subList(start, stop + 1);
  }

  //
  // public V remove(Object key) {
  // // TODO Auto-generated method stub
  // return null;
  // }
  //
  // public void putAll(Map<? extends K, ? extends V> m) {
  // // TODO Auto-generated method stub
  //
  // }
  //
  // public void clear() {
  // // TODO Auto-generated method stub
  //
  // }
  //
  // public Set<K> keySet() {
  // // TODO Auto-generated method stub
  // return null;
  // }
  //
  // public Collection<V> values() {
  // // TODO Auto-generated method stub
  // return null;
  // }
  //
  // public Set<Entry<K, V>> entrySet() {
  // // TODO Auto-generated method stub
  // return null;
  // }

}
