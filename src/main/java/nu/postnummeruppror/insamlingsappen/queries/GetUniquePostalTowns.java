package nu.postnummeruppror.insamlingsappen.queries;

import nu.postnummeruppror.insamlingsappen.Insamlingsappen;
import nu.postnummeruppror.insamlingsappen.domain.LocationSample;
import nu.postnummeruppror.insamlingsappen.domain.Root;
import org.prevayler.Query;

import java.util.*;

/**
 * @author kalle
 * @since 2014-09-26 18:45
 */
public class GetUniquePostalTowns implements Query<Root, Set<String>> {

  public static void main(String[] args) throws Exception {
    Insamlingsappen.getInstance().open();
    try {

      List<String> postalTowns = new ArrayList<>(Insamlingsappen.getInstance().getPrevayler().execute(new GetUniquePostalTowns()));
      Collections.sort(postalTowns);

      for (String postalTown : postalTowns) {
        System.out.println(postalTown);
      }



    } finally {
      Insamlingsappen.getInstance().close();
    }
  }

  @Override
  public Set<String> query(Root root, Date executionTime) throws Exception {
    Set<String> normalizedPostalTowns = new HashSet<>(2000);
    for (LocationSample locationSample : root.getLocationSamples().values()) {
      String normalizedPostalTown = normalizePostalTown(locationSample.getTag("addr:city"));
      if (normalizedPostalTown != null) {
        normalizedPostalTowns.add(normalizedPostalTown);
      }
    }
    return normalizedPostalTowns;
  }

  private String normalizePostalTown(String postalTown) {
    if (postalTown == null) {
      return null;
    }
    postalTown = postalTown.replaceAll("\\s+", " ");
    postalTown = postalTown.trim();
    postalTown = postalTown.toUpperCase();
    return postalTown;
  }
}
