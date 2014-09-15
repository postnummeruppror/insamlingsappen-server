package nu.postnummeruppror.insamlingsappen.domain;

import java.io.Serializable;
import java.util.List;

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


  /** Generic POI name. 'A och B livsmedelsbutik */
  private String name;

  private PostalAddress postalAddress;


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
    sb.append(", name='").append(name).append('\'');
    sb.append(", postalAddress=").append(postalAddress);
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Coordinate getCoordinate() {
    return coordinate;
  }

  public void setCoordinate(Coordinate coordinate) {
    this.coordinate = coordinate;
  }

  public PostalAddress getPostalAddress() {
    return postalAddress;
  }

  public void setPostalAddress(PostalAddress postalAddress) {
    this.postalAddress = postalAddress;
  }
}
