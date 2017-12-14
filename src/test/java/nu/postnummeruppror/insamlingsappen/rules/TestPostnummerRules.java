package nu.postnummeruppror.insamlingsappen.rules;

import junit.framework.Assert;
import org.junit.Test;

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

}
