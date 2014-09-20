package nu.postnummeruppror.util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author kalle
 * @since 2014-09-20 12:12
 */
public class JSONParser {


  public static Double getDouble(JSONObject jsonObject, String key) throws JSONException {
    if (!jsonObject.has(key)) {
      return null;
    }
    Object object = jsonObject.get(key);

    if (object == null) {
      return null;
    }

    if (object instanceof Number) {
      return ((Number)object).doubleValue();
    } else {
      String string = object.toString().trim();
      if (string.isEmpty()) {
        return null;
      } else {
        return Double.valueOf(string);
      }
    }
  }

  public static Float getFloat(JSONObject jsonObject, String key) throws JSONException {
    if (!jsonObject.has(key)) {
      return null;
    }
    Object object = jsonObject.get(key);

    if (object == null) {
      return null;
    }

    if (object instanceof Number) {
      return ((Number)object).floatValue();
    } else {
      String string = object.toString().trim();
      if (string.isEmpty()) {
        return null;
      } else {
        return Float.valueOf(string);
      }
    }
  }

}
