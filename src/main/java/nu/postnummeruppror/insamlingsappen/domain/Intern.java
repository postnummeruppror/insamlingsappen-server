package nu.postnummeruppror.insamlingsappen.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Assures that only a single instance of objects that equals is kept in memory.
 *
 * I.e. every instance of the String "android" consume 112 bytes more heap
 * than pointing at an existing object with that value.
 *
 * @author kalle
 * @since 2014-09-07 02:05
 */
public class Intern<T> implements Serializable {

  private static final long serialVersionUID = 1l;

  public Intern() {
  }

  private Map<T, T> map = new HashMap<>();

  public T intern(T value) {
    T interned = map.get(value);
    if (interned == null) {
      getMap().put(value, value);
      interned = value;
    }
    return interned;
  }

  public Map<T, T> getMap() {
    return map;
  }

  public void setMap(Map<T, T> map) {
    this.map = map;
  }
}
