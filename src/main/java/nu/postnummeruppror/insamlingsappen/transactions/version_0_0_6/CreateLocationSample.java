package nu.postnummeruppror.insamlingsappen.transactions.version_0_0_6;

import nu.postnummeruppror.insamlingsappen.domain.Account;
import nu.postnummeruppror.insamlingsappen.domain.Coordinate;
import nu.postnummeruppror.insamlingsappen.domain.LocationSample;
import nu.postnummeruppror.insamlingsappen.domain.Root;
import org.prevayler.TransactionWithQuery;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kalle
 * @since 2014-09-29 19:04
 */
public class CreateLocationSample implements TransactionWithQuery<Root, LocationSample>, Serializable {

  private static final long serialVersionUID = 1l;

  public CreateLocationSample() {
  }

  private String accountIdentity;
  private Long locationSampleIdentity;

  private String application;
  private String applicationVersion;

  private Coordinate coordinate;

  private Map<String, String> tags = new HashMap<>();

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

    for (Map.Entry<String, String> tag : tags.entrySet()) {
      locationSample.setTag(root.getTagsIntern().internKey(tag.getKey()), root.getTagsIntern().internValue(tag.getKey(), tag.getValue()));
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
