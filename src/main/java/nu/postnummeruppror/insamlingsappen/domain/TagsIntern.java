package nu.postnummeruppror.insamlingsappen.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kalle
 * @since 2014-09-29 19:13
 */
public class TagsIntern implements Serializable {

  private static final long serialVersionUID = 1l;

  private Intern<String> keys = new Intern<>();
  private Map<String, Intern<String>> values = new HashMap<>();

  public String internKey(String key) {
    return keys.intern(key);
  }

  public String internValue(String key, String value) {
    key = internKey(key);
    Intern<String> values = this.values.get(key);
    if (values == null) {
      values = new Intern<>();
      this.values.put(key, values);
    }
    return values.intern(value);
  }


  public Intern<String> getKeys() {
    return keys;
  }

  public void setKeys(Intern<String> keys) {
    this.keys = keys;
  }

  public Map<String, Intern<String>> getValues() {
    return values;
  }

  public void setValues(Map<String, Intern<String>> values) {
    this.values = values;
  }
}
