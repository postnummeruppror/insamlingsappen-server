package nu.postnummeruppror.insamlingsappen.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kalle
 * @since 2014-09-06 00:45
 */
public class LocationSample implements Serializable {

  private static final long serialVersionUID = 1l;

  private Long identity;

  private Account account;

  /**
   * android, ios, webapp, etc
   */
  private String application;

  /** 0.0.1 or what not */
  private String applicationVersion;

  private long timestamp;

  private Coordinate coordinate;

  private Map<String, String> tags = new HashMap<>();

  public String setTag(String key, String value) {
    return getTags().put(key, value);
  }

  public String getTag(String key) {
    return getTags().get(key);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("LocationSample{");
    sb.append("identity=").append(identity);
    sb.append(", account.identity='").append(account.getIdentity()).append('\'');
    sb.append(", application='").append(application).append('\'');
    sb.append(", applicationVersion='").append(applicationVersion).append('\'');
    sb.append(", timestamp=").append(timestamp);
    sb.append(", coordinate=").append(coordinate);
    sb.append(", tags=").append(tags);
    sb.append('}');
    return sb.toString();
  }


  public Long getIdentity() {
    return identity;
  }

  public void setIdentity(Long identity) {
    this.identity = identity;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }


  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }


  public String getApplication() {
    return application;
  }

  public void setApplication(String application) {
    this.application = application;
  }

  public String getApplicationVersion() {
    return applicationVersion;
  }

  public void setApplicationVersion(String applicationVersion) {
    this.applicationVersion = applicationVersion;
  }

  public Coordinate getCoordinate() {
    return coordinate;
  }

  public void setCoordinate(Coordinate coordinate) {
    this.coordinate = coordinate;
  }

  public Map<String, String> getTags() {
    return tags;
  }

  public void setTags(Map<String, String> tags) {
    this.tags = tags;
  }
}
