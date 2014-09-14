package nu.postnummeruppror.insamlingsappen.domain;

import java.io.Serializable;

/**
 * @author kalle
 * @since 2014-09-14 09:56
 */
public class LocationSampleUpdate implements Serializable {

  private static final long serialVersionUID = 1l;

  /** The sample this is an update on */
  private LocationSample locationSample;

  /** Identity of this update */
  private Long identity;

  /** Author of this update */
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


  @Override
  public String toString() {
    return "LocationSampleUpdate{" +
        "locationSample.identity=" + locationSample.getIdentity() +
        ", identity=" + identity +
        ", account.identity='" + account.getIdentity() + '\'' +
        ", application='" + application + '\'' +
        ", applicationVersion='" + applicationVersion + '\'' +
        ", provider='" + provider + '\'' +
        ", timestamp=" + timestamp +
        ", latitude=" + latitude +
        ", longitude=" + longitude +
        ", accuracy=" + accuracy +
        ", altitude=" + altitude +
        ", postalCode='" + postalCode + '\'' +
        ", streetName='" + streetName + '\'' +
        ", houseNumber='" + houseNumber + '\'' +
        ", houseName='" + houseName + '\'' +
        ", postalTown='" + postalTown + '\'' +
        '}';
  }

  public LocationSample getLocationSample() {
    return locationSample;
  }

  public void setLocationSample(LocationSample locationSample) {
    this.locationSample = locationSample;
  }

  public Long getIdentity() {
    return identity;
  }

  public void setIdentity(Long identity) {
    this.identity = identity;
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

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
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

  public String getHouseName() {
    return houseName;
  }

  public void setHouseName(String houseName) {
    this.houseName = houseName;
  }

  public String getPostalTown() {
    return postalTown;
  }

  public void setPostalTown(String postalTown) {
    this.postalTown = postalTown;
  }
}
