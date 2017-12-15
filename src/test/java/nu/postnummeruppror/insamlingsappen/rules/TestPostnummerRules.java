package nu.postnummeruppror.insamlingsappen.rules;

import junit.framework.Assert;
import nu.postnummeruppror.insamlingsappen.Insamlingsappen;
import nu.postnummeruppror.insamlingsappen.domain.LocationSample;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kalle
 * @since 2017-12-14
 */
public class TestPostnummerRules {

  @Test
  public void testTwoNumberIdentifiedPostalTown() {

    PostnummerRules postnummerRules = new PostnummerRules();
    Assert.assertFalse(postnummerRules.isUtdelningspost("30004")); // HMS Industrial Networks AB Box 4126 300 04 Halmstad Sweden
    Assert.assertTrue(postnummerRules.isUtdelningspost("30242")); // Klammerdammsgatan 27, 30242 Halmstad


  }

  public static void main(String[] args) throws Exception {
    Insamlingsappen.getInstance().open();
    try {

      PostnummerRules rules = new PostnummerRules();

      List<LocationSample> badPostalCodes = new ArrayList<>();
      List<LocationSample> badPostalTowns = new ArrayList<>();
      List<LocationSample> badAccuracy = new ArrayList<>();

      for (LocationSample locationSample : Insamlingsappen.getInstance().getPrevayler().prevalentSystem().getLocationSamples().values()) {
        if (!"true".equals(locationSample.getTag("deprecated"))) {
          String postalCode = locationSample.getTag("addr:postcode");

          if (postalCode != null) {
            postalCode = postalCode.replaceAll("\\D+", "");
            if (postalCode.length() == 5) {
              if (new Boolean(false).equals(rules.isUtdelningspost(postalCode))) {
                badPostalCodes.add(locationSample);
              }

            }
          }
        }

        if (locationSample.getCoordinate() != null
            && locationSample.getCoordinate().getAccuracy() != null
            && locationSample.getCoordinate().getAccuracy() > 1000) {
          badAccuracy.add(locationSample);
        }
      }

      System.currentTimeMillis();

    } finally {
      Insamlingsappen.getInstance().close();
    }

  }

}
