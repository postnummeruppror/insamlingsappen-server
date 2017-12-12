package nu.postnummeruppror.insamlingsappen.queries;

import nu.postnummeruppror.insamlingsappen.domain.LocationSample;
import nu.postnummeruppror.insamlingsappen.domain.Root;
import org.prevayler.Query;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author kalle
 * @since 2017-12-13 00:31
 */
public class FindSamplesWithMinimumAccuracy implements Query<Root, Collection<LocationSample>> {

  private int minimumAccuracy;

  public FindSamplesWithMinimumAccuracy(int minimumAccuracy) {
    this.minimumAccuracy = minimumAccuracy;
  }

  @Override
  public Collection<LocationSample> query(Root root, Date date) throws Exception {
    Set<LocationSample> locationSamples = new HashSet<>();
    for (LocationSample locationSample : root.getLocationSamples().values()) {
      if (locationSample.getCoordinate() != null && locationSample.getCoordinate().getAccuracy() >= minimumAccuracy) {
        locationSamples.add(locationSample);
      }
    }
    return locationSamples;
  }
}
