package nu.postnummeruppror.insamlingsappen.queries;

import nu.postnummeruppror.insamlingsappen.Insamlingsappen;
import nu.postnummeruppror.insamlingsappen.domain.LocationSample;
import nu.postnummeruppror.insamlingsappen.domain.Root;
import org.prevayler.Query;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author kalle
 * @since 2017-12-12
 */
public class FindPostalTownsWithNumbers implements Query<Root, Set<String>> {

  public static void main(String[] args) throws Exception {
    Insamlingsappen.getInstance().open();
    try {
      Insamlingsappen.getInstance().getPrevayler().execute(new FindPostalTownsWithNumbers());
    } finally {
      Insamlingsappen.getInstance().close();
    }
  }

  @Override
  public Set<String> query(Root root, Date date) throws Exception {
    Set<String> postalTownsWithNumber = new HashSet<>();

    for (LocationSample locationSample : root.getLocationSamples().values()) {
      if ("true".equalsIgnoreCase(locationSample.getTag("deprecated"))) {
        continue;
      }
      String postalTown = locationSample.getTag("addr:city");
      if (postalTown == null) {
        continue;
      }
      if (postalTown.matches(".*\\d.*")) {
        postalTownsWithNumber.add(postalTown);
      }
    }
    return postalTownsWithNumber;
  }
}
