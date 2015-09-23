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
public class GetUniquePostalCodes implements Query<Root, Set<Map.Entry<String, String>>> {

  @Override
  public Set<Map.Entry<String, String>> query(Root root, Date executionTime) throws Exception {

    Map<String, String> postalTownByPostalCode = new HashMap<>();
    for (LocationSample locationSample : root.getLocationSamples().values()) {
      String normalizedPostalTown = normalizePostalTown(locationSample.getTag("addr:city"));
      if (normalizedPostalTown != null) {
        String normalizedPostalCode = normalizePostalCode(locationSample.getTag("addr:postcode"));
        if (normalizedPostalCode != null) {
          postalTownByPostalCode.put(normalizedPostalCode, normalizedPostalTown);
        }
      }
    }
    return postalTownByPostalCode.entrySet();
  }

  private String normalizePostalCode(String postalCode) {
    if (postalCode == null) {
      return null;
    }
    return postalCode.replaceAll("\\s+", "");
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
