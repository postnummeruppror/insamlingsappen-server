package nu.postnummeruppror.insamlingsappen.transactions.version_0_0_6;

import nu.postnummeruppror.insamlingsappen.domain.Account;
import nu.postnummeruppror.insamlingsappen.domain.Coordinate;
import nu.postnummeruppror.insamlingsappen.domain.LocationSample;
import nu.postnummeruppror.insamlingsappen.domain.Root;
import org.prevayler.TransactionWithQuery;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kalle
 * @since 2017-12-11 22:22
 */
public class ReplaceLocationSample implements TransactionWithQuery<Root, LocationSample>, Serializable {

  private static final long serialVersionUID = 1l;

  private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  public ReplaceLocationSample() {
  }

  private String accountIdentity;

  private Long previousLocationSampleIdentity;
  private Long nextLocationSampleIdentity;

  private String application;
  private String applicationVersion;

  private Coordinate coordinate;

  private Map<String, String> tags = new HashMap<>();


  @Override
  public LocationSample executeAndQuery(Root root, Date executionTime) throws Exception {

    LocationSample previousLocationSample = root.getLocationSamples().get(previousLocationSampleIdentity);
    if (previousLocationSample == null) {
      throw new IllegalArgumentException("No LocationSample with identity " + previousLocationSampleIdentity);
    }

    Account account = root.getAccounts().get(accountIdentity);
    if (account == null) {
      // create new account if not existing
      account = new Account();
      account.setTimestampCreated(executionTime.getTime());
      account.setIdentity(accountIdentity);
      root.getAccounts().put(account.getIdentity(), account);
    }

    LocationSample nextLocationSample = new LocationSample();
    nextLocationSample.setIdentity(nextLocationSampleIdentity);

    nextLocationSample.setTimestamp(executionTime.getTime());

    nextLocationSample.setApplication(root.getApplicationIntern().intern(application));
    nextLocationSample.setApplicationVersion(root.getApplicationVersionIntern().intern(applicationVersion));

    if (coordinate != null) {

      if (coordinate.getProvider() != null) {
        coordinate.setProvider(coordinate.getProvider().trim());
        if (coordinate.getProvider().isEmpty()) {
          coordinate.setProvider(null);
        } else {
          coordinate.setProvider(root.getProviderIntern().intern(coordinate.getProvider()));
        }
      }

      if (coordinate.getLatitude() == null
          && coordinate.getLongitude() == null) {
        coordinate = null;
      }


      nextLocationSample.setCoordinate(coordinate);

    }

    for (Map.Entry<String, String> tag : tags.entrySet()) {
      nextLocationSample.setTag(root.getTagsIntern().internKey(tag.getKey()), root.getTagsIntern().internValue(tag.getKey(), tag.getValue()));
    }


    nextLocationSample.setAccount(account);
    account.getLocationSamples().add(nextLocationSample);


    root.getLocationSamples().put(nextLocationSample.getIdentity(), nextLocationSample);

    // deprecate previous
    previousLocationSample.getTags().put("deprecated", "true");
    previousLocationSample.getTags().put("deprecated_reason", "Replaced due to change");
    previousLocationSample.getTags().put("deprecated_replacement", String.valueOf(nextLocationSampleIdentity));
    previousLocationSample.getTags().put("deprecated_timestamp", df.format(executionTime));

    // return new
    return nextLocationSample;


  }

  public String getAccountIdentity() {
    return accountIdentity;
  }

  public void setAccountIdentity(String accountIdentity) {
    this.accountIdentity = accountIdentity;
  }

  public Long getPreviousLocationSampleIdentity() {
    return previousLocationSampleIdentity;
  }

  public void setPreviousLocationSampleIdentity(Long previousLocationSampleIdentity) {
    this.previousLocationSampleIdentity = previousLocationSampleIdentity;
  }

  public Long getNextLocationSampleIdentity() {
    return nextLocationSampleIdentity;
  }

  public void setNextLocationSampleIdentity(Long nextLocationSampleIdentity) {
    this.nextLocationSampleIdentity = nextLocationSampleIdentity;
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


