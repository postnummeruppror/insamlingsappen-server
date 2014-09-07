package nu.postnummeruppror.insamlingsappen.domain;

import java.io.Serializable;

/**
 * @author kalle
 * @since 2014-09-06 00:45
 */
public class LocationSample implements Serializable {

  private static final long serialVersionUID = 1l;

  private Long identity;

  private Account account;

  private String provider;

  private long timestamp;

  private double latitude;
  private double longitude;

  private double accuracy;

  private double altitude;

  private PostalCode postalCode;

  private String streetName;
  private String houseNumber;


  public PostalCode getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(PostalCode postalCode) {
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

  public double getAccuracy() {
    return accuracy;
  }

  public void setAccuracy(double accuracy) {
    this.accuracy = accuracy;
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

  public double getAltitude() {
    return altitude;
  }

  public void setAltitude(double altitude) {
    this.altitude = altitude;
  }
}
