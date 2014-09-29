package nu.postnummeruppror.insamlingsappen.queries;

import nu.postnummeruppror.insamlingsappen.Insamlingsappen;
import nu.postnummeruppror.insamlingsappen.domain.Account;
import nu.postnummeruppror.insamlingsappen.domain.LocationSample;
import nu.postnummeruppror.insamlingsappen.domain.Root;
import org.prevayler.Query;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author kalle
 * @since 2014-09-30 00:06
 */
public class CountUniquePostalTowns implements Query<Root, Integer> {

  public static void main(String[] args) throws Exception {
    Insamlingsappen.getInstance().open();
    try {
      System.out.println(Insamlingsappen.getInstance().getPrevayler().execute(new CountUniquePostalTowns()));
    } finally {
      Insamlingsappen.getInstance().close();
    }
  }

  @Override
  public Integer query(Root root, Date executionTime) throws Exception {
    Set<String> normalizedPostalTowns = new HashSet<>(2000);
    for (LocationSample locationSample : root.getLocationSamples().values()) {
      String postalTown = locationSample.getTag("addr:city");
      if (postalTown != null) {
        postalTown = postalTown.replaceAll("-", " ");
        postalTown = postalTown.replaceAll("\\s+", "");
        postalTown = postalTown.toUpperCase();
        normalizedPostalTowns.add(postalTown);
      }
    }
    return normalizedPostalTowns.size();
  }
}
