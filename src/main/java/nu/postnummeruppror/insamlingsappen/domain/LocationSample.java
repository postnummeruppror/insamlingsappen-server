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

  /**
   * gps, network, wifi, human, etc.
   */
  private String provider;

  private long timestamp;

  private double latitude;
  private double longitude;

  private Double accuracy;
  private Double altitude;

  private String postalCode;
  private String streetName;
  private String houseNumber;
  private String houseName;
  private String postalTown;

  private List<LocationSampleUpdate> updates;


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("LocationSample{");
    sb.append("identity=").append(identity);
    sb.append(", account.identity='").append(account.getIdentity()).append('\'');
    sb.append(", application='").append(application).append('\'');
    sb.append(", applicationVersion='").append(applicationVersion).append('\'');
    sb.append(", provider='").append(provider).append('\'');
    sb.append(", timestamp=").append(timestamp);
    sb.append(", latitude=").append(latitude);
    sb.append(", longitude=").append(longitude);
    sb.append(", accuracy=").append(accuracy);
    sb.append(", altitude=").append(altitude);
    sb.append(", postalCode='").append(postalCode).append('\'');
    sb.append(", streetName='").append(streetName).append('\'');
    sb.append(", houseNumber='").append(houseNumber).append('\'');
    sb.append(", houseName='").append(houseName).append('\'');
    sb.append(", postalTown='").append(postalTown).append('\'');
    sb.append(", postalTown='").append(postalTown).append('\'');
    sb.append(", updates=").append(updates);
    sb.append('}');
    return sb.toString();
  }

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  public Long getIdentity() {
    return identity;
  }

  public void setIdentity(Long identity) {
    this.identity = identity;
  }

  public String getProvider() {
    return provider;
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }


  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  public String getStreetName() {
    return streetName;
  }

  public void setStreetName(String streetName) {
    this.streetName = streetName;
  }

  public String getHouseNumber() {
    return houseNumber;
  }

  public void setHouseNumber(String houseNumber) {
    this.houseNumber = houseNumber;
  }

  public Double getAccuracy() {
    return accuracy;
  }

  public void setAccuracy(Double accuracy) {
    this.accuracy = accuracy;
  }

  public Double getAltitude() {
    return altitude;
  }

  public void setAltitude(Double altitude) {
    this.altitude = altitude;
  }

  public String getPostalTown() {
    return postalTown;
  }

  public void setPostalTown(String postalTown) {
    this.postalTown = postalTown;
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

  public String getHouseName() {
    return houseName;
  }

  public void setHouseName(String houseName) {
    this.houseName = houseName;
  }

  public List<LocationSampleUpdate> getUpdates() {
    return updates;
  }

  public void setUpdates(List<LocationSampleUpdate> updates) {
    this.updates = updates;
  }
}
