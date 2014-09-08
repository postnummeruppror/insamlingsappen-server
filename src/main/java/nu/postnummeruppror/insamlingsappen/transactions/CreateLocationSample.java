package nu.postnummeruppror.insamlingsappen.transactions;

import nu.postnummeruppror.insamlingsappen.domain.Account;
import nu.postnummeruppror.insamlingsappen.domain.LocationSample;
import nu.postnummeruppror.insamlingsappen.domain.PostalCode;
import nu.postnummeruppror.insamlingsappen.domain.Root;
import org.prevayler.TransactionWithQuery;

import java.io.Serializable;
import java.util.Date;

/**
 * @author kalle
 * @since 2014-09-06 20:30
 */
public class CreateLocationSample implements TransactionWithQuery<Root, LocationSample>, Serializable {

  private static final long serialVersionUID = 1l;

  public CreateLocationSample() {
  }

  private String accountIdentity;
  private Long locationSampleIdentity;

  private String application;
  private String applicationVersion;

  private String provider;
  private double latitude;
  private double longitude;
  private double accuracy;
  private double altitude;

  private String postalCode;

  private String streetName;
  private String houseNumber;
  private String postalTown;


  @Override
  public LocationSample executeAndQuery(Root root, Date executionTime) throws Exception {

    Account account = root.getAccounts().get(accountIdentity);
    if (account == null) {
      // create new account if not existing
      account = new Account();
      account.setTimestampCreated(executionTime.getTime());
      account.setIdentity(accountIdentity);
      root.getAccounts().put(account.getIdentity(), account);
    }

    LocationSample locationSample = new LocationSample();
    locationSample.setIdentity(locationSampleIdentity);

    locationSample.setTimestamp(executionTime.getTime());

    locationSample.setApplication(root.getApplicationIntern().intern(application));
    locationSample.setApplicationVersion(root.getApplicationVersionIntern().intern(applicationVersion));

    locationSample.setProvider(root.getProviderIntern().intern(provider));
    locationSample.setAccuracy(accuracy);
    locationSample.setLatitude(latitude);
    locationSample.setLongitude(longitude);
    locationSample.setAltitude(altitude);

    PostalCode postalCode = root.getPostalCodes().get(this.postalCode);
    if (postalCode == null) {
      postalCode = new PostalCode();
      postalCode.setPostalCode(this.postalCode);
      root.getPostalCodes().put(postalCode.getPostalCode(), postalCode);
    }

    locationSample.setPostalCode(postalCode);

    locationSample.setStreetName(root.getStreetNameIntern().intern(streetName));
    locationSample.setHouseNumber(root.getHouseNumberIntern().intern(houseNumber));
    locationSample.setPostalTown(root.getPostalTownIntern().intern(postalTown));

    locationSample.setAccount(account);
    account.getLocationSamples().add(locationSample);

    root.getLocationSamples().put(locationSample.getIdentity(), locationSample);

    postalCode.getLocationSamples().add(locationSample);

    return locationSample;
  }

  public String getAccountIdentity() {
    return accountIdentity;
  }

  public void setAccountIdentity(String accountIdentity) {
    this.accountIdentity = accountIdentity;
  }

  public Long getLocationSampleIdentity() {
    return locationSampleIdentity;
  }

  public void setLocationSampleIdentity(Long locationSampleIdentity) {
    this.locationSampleIdentity = locationSampleIdentity;
  }

  public String getProvider() {
    return provider;
  }

  public void setProvider(String provider) {
    this.provider = provider;
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

  public double getAltitude() {
    return altitude;
  }

  public void setAltitude(double altitude) {
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
}
