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
  public void testGetPostalTown() {

    PostnummerRules postnummerRules = new PostnummerRules();

    Assert.assertNull("Halmstad", postnummerRules.getPostalTown("3"));
    Assert.assertEquals("Halmstad", postnummerRules.getPostalTown("30"));
    Assert.assertEquals("Halmstad", postnummerRules.getPostalTown("302"));
    Assert.assertEquals("Halmstad", postnummerRules.getPostalTown("3023"));
    Assert.assertEquals("Halmstad", postnummerRules.getPostalTown("30234"));

  }

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

      for (LocationSample locationSample : Insamlingsappen.getInstance().getPrevayler().prevalentSystem().getLocationSamples().values()) {
        if (!"true".equals(locationSample.getTag("deprecated"))) {
          String postalCode = locationSample.getTag("addr:postcode");

          if (postalCode != null) {
            postalCode = postalCode.replaceAll("\\D+", "");
            if (postalCode.length() == 5) {
              if (new Boolean(false).equals(rules.isUtdelningspost(postalCode))) {
                badPostalCodes.add(locationSample);
              }

              String postalTown = locationSample.getTag("addr:city");
              if (postalTown != null) {
                postalTown = postalTown.trim();
                String rulePostalTown = rules.getPostalTown(postalCode);
                if (rulePostalTown != null) {
                  if (!postalTown.equalsIgnoreCase(rulePostalTown)) {
                    badPostalTowns.add(locationSample);
                  }
                }
              }

            }
          }


        }
      }

      System.currentTimeMillis();

    } finally {
      Insamlingsappen.getInstance().close();
    }

  }

}
