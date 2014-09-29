package nu.postnummeruppror.insamlingsappen.transactions;

import nu.postnummeruppror.insamlingsappen.domain.LocationSample;
import nu.postnummeruppror.insamlingsappen.domain.Root;
import org.prevayler.TransactionWithQuery;

import java.util.Date;

/**
 * @author kalle
 * @since 2014-09-26 18:31
 */
public class DeleteLocationSample implements TransactionWithQuery<Root, LocationSample> {

  private static final long serialVersionUID = 1l;

  private Long locationSampleIdentity;


  public DeleteLocationSample() {
  }

  public DeleteLocationSample(LocationSample locationSample) {
    this(locationSample.getIdentity());
  }


  public DeleteLocationSample(Long locationSampleIdentity) {
    this.locationSampleIdentity = locationSampleIdentity;
  }

  @Override
  public LocationSample executeAndQuery(Root root, Date executionTime) throws Exception {
    LocationSample locationSample = root.getLocationSamples().remove(locationSampleIdentity);
    locationSample.getAccount().getLocationSamples().remove(locationSample);
    return locationSample;
  }

  public Long getLocationSampleIdentity() {
    return locationSampleIdentity;
  }

  public void setLocationSampleIdentity(Long locationSampleIdentity) {
    this.locationSampleIdentity = locationSampleIdentity;
  }
}
