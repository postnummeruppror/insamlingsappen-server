package nu.postnummeruppror.insamlingsappen.domain;

import com.sleepycat.persist.model.*;

/**
 * @author kalle
 * @since 2014-09-06 00:45
 */
@Entity(version = 1)
public class LocationSample {

  @PrimaryKey
  private Long identity;

  @SecondaryKey(relate = Relationship.MANY_TO_ONE, relatedEntity = Account.class, onRelatedEntityDelete = DeleteAction.NULLIFY)
  private String accountIdentity;

  private String device;

  private long timestamp;

  private double latitude;
  private double longitude;

  private double accuracy;

  private String postalCode;
  private String streetName;
  private String houseNumber;

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

  public Long getIdentity() {
    return identity;
  }

  public void setIdentity(Long identity) {
    this.identity = identity;
  }

  public String getAccountIdentity() {
    return accountIdentity;
  }

  public void setAccountIdentity(String accountIdentity) {
    this.accountIdentity = accountIdentity;
  }

  public String getDevice() {
    return device;
  }

  public void setDevice(String device) {
    this.device = device;
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
}
