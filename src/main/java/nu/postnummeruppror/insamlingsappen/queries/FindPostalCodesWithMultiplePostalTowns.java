package nu.postnummeruppror.insamlingsappen.queries;

import nu.postnummeruppror.insamlingsappen.Insamlingsappen;
import nu.postnummeruppror.insamlingsappen.domain.LocationSample;
import nu.postnummeruppror.insamlingsappen.domain.Root;
import org.prevayler.Query;

import java.util.*;

/**
 * @author kalle
 * @since 2017-12-12
 */
public class FindPostalCodesWithMultiplePostalTowns implements Query<Root, Map<String, Set<String>>> {

  public static void main(String[] args) throws Exception {
    Insamlingsappen.getInstance().open();
    try {
      Insamlingsappen.getInstance().getPrevayler().execute(new FindPostalCodesWithMultiplePostalTowns());
    } finally {
      Insamlingsappen.getInstance().close();
    }
  }

  @Override
  public Map<String, Set<String>> query(Root root, Date date) throws Exception {
    Map<String, Set<String>> postalTownsByPostalCode = new HashMap<>();

    for (LocationSample locationSample : root.getLocationSamples().values()) {
      if ("true".equalsIgnoreCase(locationSample.getTag("deprecated"))) {
        continue;
      }
      String postalCode = locationSample.getTag("addr:postcode");
      if (postalCode == null) {
        continue;
      }
      postalCode = postalCode.replaceAll("\\D+", "");
      if (postalCode.length() != 5) {
        continue;
      }
      String postalTown = locationSample.getTag("addr:city");
      if (postalTown == null) {
        continue;
      }
      postalTown = postalTown.trim();
      postalTown = postalTown.toUpperCase();
      if (postalTown.isEmpty()) {
        continue;
      }

      Set<String> postalTowns = postalTownsByPostalCode.get(postalCode);
      if (postalTowns == null) {
        postalTowns = new HashSet<>();
        postalTownsByPostalCode.put(postalCode, postalTowns);
      }
      postalTowns.add(postalTown);
    }

    for (Iterator<Map.Entry<String, Set<String>>> iterator = postalTownsByPostalCode.entrySet().iterator(); iterator.hasNext();) {
      Map.Entry<String, Set<String>> entry = iterator.next();
      if (entry.getValue().size() == 1) {
        iterator.remove();
      }
    }

    return postalTownsByPostalCode;

  }
}
