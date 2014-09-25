package nu.postnummeruppror.insamlingsappen.transactions;

import nu.postnummeruppror.insamlingsappen.domain.*;
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

  private String name;
  private PostalAddress postalAddress;
  private Coordinate coordinate;

  private String ipAddress;
  private String ipAddressHost;

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

    locationSample.setIpAddress(ipAddress);
    locationSample.setIpAddressHost(ipAddressHost);

    if (postalAddress != null) {

      if (postalAddress.getPostalTown() != null) {
        postalAddress.setPostalTown(postalAddress.getPostalTown().trim());
        if (postalAddress.getPostalTown().isEmpty()) {
          postalAddress.setPostalTown(null);
        } else {
          postalAddress.setPostalTown(root.getPostalTownIntern().intern(postalAddress.getPostalTown()));
        }
      }

      if (postalAddress.getPostalCode() != null) {
        postalAddress.setPostalCode(postalAddress.getPostalCode().trim());
        if (postalAddress.getPostalCode().isEmpty()) {
          postalAddress.setPostalCode(null);
        } else {
          postalAddress.setPostalCode(root.getPostalCodeIntern().intern(postalAddress.getPostalCode()));
        }
      }

      if (postalAddress.getStreetName() != null) {
        postalAddress.setStreetName(postalAddress.getStreetName().trim());
        if (postalAddress.getStreetName().isEmpty()) {
          postalAddress.setStreetName(null);
        } else {
          postalAddress.setStreetName(root.getStreetNameIntern().intern(postalAddress.getStreetName()));
        }
      }

      if (postalAddress.getHouseNumber() != null) {
        postalAddress.setHouseNumber(postalAddress.getHouseNumber().trim());
        if (postalAddress.getHouseNumber().isEmpty()) {
          postalAddress.setHouseNumber(null);
        } else {
          postalAddress.setHouseNumber(root.getHouseNumberIntern().intern(postalAddress.getHouseNumber()));
        }
      }

      if (postalAddress.getHouseName() != null) {
        postalAddress.setHouseName(postalAddress.getHouseName().trim());
        if (postalAddress.getHouseName().isEmpty()) {
          postalAddress.setHouseName(null);
        } else {
          postalAddress.setHouseName(root.getHouseNameIntern().intern(postalAddress.getHouseName()));
        }
      }

      if (postalAddress.getStreetName() == null
          && postalAddress.getHouseNumber() == null
          && postalAddress.getHouseName() == null
          && postalAddress.getPostalCode() == null
          && postalAddress.getPostalTown() == null) {

        // All fields are null! todo is this an exception?

      } else {
        locationSample.setPostalAddress(postalAddress);
      }
    }

    if (coordinate != null) {

      if (coordinate.getProvider() != null) {
        coordinate.setProvider(coordinate.getProvider().trim());
        if (coordinate.getProvider().isEmpty()) {
          coordinate.setProvider(null);
        } else {
          coordinate.setProvider(root.getProviderIntern().intern(coordinate.getProvider()));
        }
      }

      locationSample.setCoordinate(coordinate);

    }

    if (name != null) {
      name = name.trim();
      if (!name.isEmpty()) {
        locationSample.setName(root.getNameIntern().intern(name));
      }
    }


    locationSample.setAccount(account);
    account.getLocationSamples().add(locationSample);


    root.getLocationSamples().put(locationSample.getIdentity(), locationSample);

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

  public PostalAddress getPostalAddress() {
    return postalAddress;
  }

  public void setPostalAddress(PostalAddress postalAddress) {
    this.postalAddress = postalAddress;
  }

  public Coordinate getCoordinate() {
    return coordinate;
  }

  public void setCoordinate(Coordinate coordinate) {
    this.coordinate = coordinate;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  public String getIpAddressHost() {
    return ipAddressHost;
  }

  public void setIpAddressHost(String ipAddressHost) {
    this.ipAddressHost = ipAddressHost;
  }
}
