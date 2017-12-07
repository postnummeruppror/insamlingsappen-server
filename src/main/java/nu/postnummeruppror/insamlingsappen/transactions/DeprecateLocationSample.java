package nu.postnummeruppror.insamlingsappen.transactions;

import nu.postnummeruppror.insamlingsappen.domain.LocationSample;
import nu.postnummeruppror.insamlingsappen.domain.Root;
import org.prevayler.TransactionWithQuery;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author kalle
 * @since 2017-12-07
 */
public class DeprecateLocationSample implements TransactionWithQuery<Root, LocationSample> {

  private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  private static final long serialVersionUID = 1l;

  private Long locationSampleIdentity;
  private String reason;

  public DeprecateLocationSample() {
  }

  public DeprecateLocationSample(Long locationSampleIdentity) {
    this.locationSampleIdentity = locationSampleIdentity;
  }

  public DeprecateLocationSample(Long locationSampleIdentity, String reason) {
    this.locationSampleIdentity = locationSampleIdentity;
    this.reason = reason;
  }

  @Override
  public LocationSample executeAndQuery(Root root, Date date) throws Exception {
    LocationSample sample = root.getLocationSamples().get(locationSampleIdentity);
    sample.setTag(root.getTagsIntern().internKey("deprecated"), root.getTagsIntern().internValue("deprecated", "true"));
    if (reason != null) {
      sample.setTag(root.getTagsIntern().internKey("deprecated_reason"), root.getTagsIntern().internValue("deprecated_reason", reason));
    }
    sample.setTag(root.getTagsIntern().internKey("deprecated_timestamp"), df.format(date));
    return sample;
  }

  public Long getLocationSampleIdentity() {
    return locationSampleIdentity;
  }

  public void setLocationSampleIdentity(Long locationSampleIdentity) {
    this.locationSampleIdentity = locationSampleIdentity;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }
}
