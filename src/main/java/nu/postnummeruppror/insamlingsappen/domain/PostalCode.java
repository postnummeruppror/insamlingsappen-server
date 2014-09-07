package nu.postnummeruppror.insamlingsappen.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kalle
 * @since 2014-09-07 02:09
 */
public class PostalCode implements Serializable {

  private static final long serialVersionUID = 1l;

  public PostalCode() {
  }

  private String postalCode;

  private List<LocationSample> locationSamples = new ArrayList<>();

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  public List<LocationSample> getLocationSamples() {
    return locationSamples;
  }

  public void setLocationSamples(List<LocationSample> locationSamples) {
    this.locationSamples = locationSamples;
  }
}
