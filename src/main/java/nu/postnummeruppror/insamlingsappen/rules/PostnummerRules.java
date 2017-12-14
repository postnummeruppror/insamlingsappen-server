package nu.postnummeruppror.insamlingsappen.rules;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kalle
 * @since 2017-12-12 12:27
 */
public class PostnummerRules {

  private Map<String, String> postalTownByPostalCodePrefix = new HashMap<>();
  private Map<String, String> postalTownByTwoDigitPostalCodePrefix = new HashMap<>();

  public PostnummerRules() {
    postalTownByTwoDigitPostalCodePrefix.put("22", "Lund");
    postalTownByTwoDigitPostalCodePrefix.put("25", "Helsingborg");
    postalTownByTwoDigitPostalCodePrefix.put("30", "Halmstad");
    postalTownByTwoDigitPostalCodePrefix.put("35", "Växjö");
    postalTownByTwoDigitPostalCodePrefix.put("39", "Kalmar");
    postalTownByTwoDigitPostalCodePrefix.put("50", "Borås");
    postalTownByTwoDigitPostalCodePrefix.put("55", "Jönköping");
    postalTownByTwoDigitPostalCodePrefix.put("58", "Linköping");
    postalTownByTwoDigitPostalCodePrefix.put("60", "Norrköping");
    postalTownByTwoDigitPostalCodePrefix.put("63", "Eskilstuna");
    postalTownByTwoDigitPostalCodePrefix.put("65", "Karlstad");
    postalTownByTwoDigitPostalCodePrefix.put("70", "Örebro");
    postalTownByTwoDigitPostalCodePrefix.put("72", "Västerås");
    postalTownByTwoDigitPostalCodePrefix.put("75", "Uppsala");
    postalTownByTwoDigitPostalCodePrefix.put("80", "Gävle");
    postalTownByTwoDigitPostalCodePrefix.put("85", "Sundsvall");
    postalTownByTwoDigitPostalCodePrefix.put("90", "Umeå");
    postalTownByTwoDigitPostalCodePrefix.put("95", "Luleå");

    postalTownByPostalCodePrefix.putAll(postalTownByTwoDigitPostalCodePrefix);
    postalTownByPostalCodePrefix.put("10", "Stockholm");
    postalTownByPostalCodePrefix.put("11", "Stockholm");
    postalTownByPostalCodePrefix.put("20", "Malmö");
    postalTownByPostalCodePrefix.put("21", "Malmö");
    postalTownByPostalCodePrefix.put("40", "Göteborg");
    postalTownByPostalCodePrefix.put("41", "Göteborg");

  }

  public String getPostalTown(String postalCode) {
    for (int i = 1; i < 5 && i <= postalCode.length(); i++) {
      String searchPrefix = postalCode.substring(0, i);
      String postalTown = postalTownByPostalCodePrefix.get(searchPrefix);
      if (postalTown != null) {
        return postalTown;
      }
    }
    return null;
  }

  /**
   *
   * @param postnummer
   * @return True if utdelningsadress, false if not, null if unknown.
   */
  public Boolean isUtdelningspost(String postnummer) {

    if (postnummer == null) {
      throw new NullPointerException();
    }

    postnummer = postnummer.replaceAll("\\D+", "");
    if (postnummer.length() != 5) {
      throw new IllegalArgumentException("Postal codes are 5 digits: " + postnummer);
    }

    // Stockholm
    if (postnummer.startsWith("100")          // Postala enheter
        || postnummer.startsWith("101")       // Tidigare förbehållet boxar vid postkontoret Stockholm 1
        || postnummer.startsWith("1022")      // Boxpost. Inom utdelningsområde 112
        || postnummer.startsWith("1023")      // Boxpost. Inom utdelningsområde 113
        || postnummer.startsWith("1024")      // Boxpost. Inom utdelningsområde 114
        || postnummer.startsWith("1025")      // Boxpost. Inom utdelningsområde 115
        || postnummer.startsWith("1026")      // Boxpost. Inom utdelningsområde 116
        || postnummer.startsWith("1027")      // Boxpost. Inom utdelningsområde 117
        || postnummer.startsWith("1030")      // Boxpost, postala enheter
        || postnummer.startsWith("1031")      // Boxpost, postala enheter
        || postnummer.startsWith("1032")      // Boxpost, postala enheter, storkund
        || postnummer.startsWith("1033")      // Storkundspostnummer
        || postnummer.startsWith("1034")      // Används ej
        || postnummer.startsWith("1035")      // Storkund, boxpost
        || postnummer.startsWith("1036")      // Boxpost, postala enheter, storkund
        || postnummer.startsWith("1037")      // Storkundspostnummer
        || postnummer.startsWith("1038")      // Boxpost, postala enheter, storkund
        || postnummer.startsWith("1039")      // Boxpost
        || postnummer.startsWith("10405")     // Boxpost, postal enhet
        || postnummer.startsWith("1042")      // Boxpost. Inom utdelningsområde 112
        || postnummer.startsWith("1043")      // Boxpost. Inom utdelningsområde 113
        || postnummer.startsWith("1044")      // Boxpost. Inom utdelningsområde 114
        || postnummer.startsWith("1045")      // Boxpost, postala enheter, storkund. Inom utdelningsområde 115
        || postnummer.startsWith("1046")      // Boxpost. Inom utdelningsområde 116
        || postnummer.startsWith("1047")      // Används ej
        || postnummer.startsWith("1048")      // Används ej
        || postnummer.startsWith("1049")      // Används ej
        || postnummer.startsWith("105")       // Storkundspostnummer
        || postnummer.startsWith("106")       // Storkundspostnummer
        || postnummer.startsWith("107")       // Boxpost, postala enheter, storkund
        || postnummer.startsWith("108")       // Avsett för svarspost/frisvar
        || postnummer.startsWith("109")       // Tävlingspost
        || postnummer.startsWith("110")       // Svarspost/Frisvar
        ) {
      return false;
    } else if (postnummer.startsWith("111")   // Utdelningspost tätort
        || postnummer.startsWith("112")       // Utdelningspost tätort
        || postnummer.startsWith("113")       // Utdelningspost tätort
        || postnummer.startsWith("114")       // Utdelningspost tätort
        || postnummer.startsWith("115")       // Utdelningspost tätort
        || postnummer.startsWith("116")       // Utdelningspost tätort
        || postnummer.startsWith("117")       // Utdelningspost tätort
        || postnummer.startsWith("118")       // Utdelningspost tätort
        || postnummer.startsWith("119")       // Utdelningspost tätort
        ) {
      return true;
    } else if (postnummer.startsWith("120")) {
      // Utdelningspost tätort
      // Postnummerserie 120 -- Används enligt principen för tresifferidentifierad
      // ort trots att siffran i position 3 är ”0”
      return isSpecialThreeNumberUtdelningspost(postnummer);
    }

    // Malmö
    if (postnummer.startsWith("2000")         // Postala enheter
        || postnummer.startsWith("2001")      // Box, svarspost, postala enheter. Inom utdomr 211
        || postnummer.startsWith("2002")      // Box, svarspost, postala enheter. Inom utdomr 212
        || postnummer.startsWith("2003")      // Box, svarspost, postala enheter. Inom utdomr 213
        || postnummer.startsWith("2004")      // Box, svarspost, postala enheter. Inom utdomr 214
        || postnummer.startsWith("2005")      // Används ej
        || postnummer.startsWith("2006")      // Box, svarspost, postala enheter. Inom utdomr 216
        || postnummer.startsWith("2007")      // Box, svarspost, postala enheter. Inom utdomr 217
        || postnummer.startsWith("2008")      // Används ej
        || postnummer.startsWith("2009")      // Används ej
        || postnummer.startsWith("201")       // Box, postala enheter
        || postnummer.startsWith("202")       // Box, postala enheter
        || postnummer.startsWith("203")       // Box, postala enheter
        || postnummer.startsWith("204")       // Används ej
        || postnummer.startsWith("205")       // Storkundspostnummer
        || postnummer.startsWith("206")       // Används ej
        || postnummer.startsWith("207")       // Special. 207 01 och 207 99 används för hantering av returpaket
        || postnummer.startsWith("208")       // Svarspost/Frisvar
        || postnummer.startsWith("209")       // Tävlingspost
        ) {
      return false;
    } else if (postnummer.startsWith("211")   // Utdelningspost tätort
        || postnummer.startsWith("212")       // Utdelningspost tätort
        || postnummer.startsWith("213")       // Utdelningspost tätort
        || postnummer.startsWith("214")       // Utdelningspost tätort
        || postnummer.startsWith("215")       // Utdelningspost tätort (även box) // todo notice this!!
        || postnummer.startsWith("216")       // Utdelningspost tätort
        || postnummer.startsWith("217")       // Utdelningspost tätort
        || postnummer.startsWith("218")       // Utdelningspost tätort
        ) {
      return true;
    } else if (postnummer.startsWith("219")   // Används ej
        ) {
      return false;
    }


    // Göteborg
    if (postnummer.startsWith("4000")         // Box, postal enhet, svarspost. Används ej
        || postnummer.startsWith("4001")      // Box, postal enhet, svarspost. Inom utdomr 411
        || postnummer.startsWith("4002")      // Box. Inom utdomr 412
        || postnummer.startsWith("4003")      // Box, svarspost/frisvar. Inom utdomr 413
        || postnummer.startsWith("4004")      // Box, svarspost/frisvar. Inom utdomr 414
        || postnummer.startsWith("4005")      // Box, svarspost/frisvar. Inom utdomr 415
        || postnummer.startsWith("4006")      // Box. Inom utdomr 416
        || postnummer.startsWith("4007")      // Box. Inom utdomr 417
        || postnummer.startsWith("4008")      // Box. Inom utdomr 418. Används ej
        || postnummer.startsWith("4009")      // Box, svarspost/frisvar. Inom utdomr 436
        || postnummer.startsWith("401")       // Box, svarspost, postala enheter, storkund
        || postnummer.startsWith("4021")      // Används ej
        || postnummer.startsWith("4022")      // Box. Inom utdomr 412
        || postnummer.startsWith("4023")      // Box. Inom utdomr 413
        || postnummer.startsWith("4024")      // Box. Inom utdomr 414
        || postnummer.startsWith("4025")      // Box, postala enheter, svarspost. Inom utdomr 415
        || postnummer.startsWith("4026")      // Box. Inom utdomr 416
        || postnummer.startsWith("4027")      // Box. Inom utdomr 417
        || postnummer.startsWith("4028")      // Används ej
        || postnummer.startsWith("4029")      // Används ej
        || postnummer.startsWith("4031")      // Box
        || postnummer.startsWith("4032")      // Box
        || postnummer.startsWith("4033")      // Storkund
        || postnummer.startsWith("4034")      // Storkund
        || postnummer.startsWith("4042")      // Box
        || postnummer.startsWith("4043")      // Box
        || postnummer.startsWith("4048")      // Storkund
        || postnummer.startsWith("404")       // Box, postala enheter, svarspost/frisvar // todo notice this!! 4048 is excluded by above
        || postnummer.startsWith("4050")      // Storkundpostnummer
        || postnummer.startsWith("406")       // Storkundpostnummer. Används ej
        || postnummer.startsWith("407")       // Tävlingspost. 40700
        || postnummer.startsWith("408")       // Svarspost
        || postnummer.startsWith("409")       // Tävlingspost

        ) {
      return false;
    } else if (postnummer.startsWith("411")   // Utdelningspost tätort
        || postnummer.startsWith("412")       // Utdelningspost tätort
        || postnummer.startsWith("413")       // Utdelningspost tätort
        || postnummer.startsWith("414")       // Utdelningspost tätort
        || postnummer.startsWith("415")       // Utdelningspost tätort
        || postnummer.startsWith("416")       // Utdelningspost tätort
        || postnummer.startsWith("417")       // Utdelningspost tätort
        || postnummer.startsWith("418")       // Utdelningspost tätort
        ) {
      return true;
    } else if (postnummer.startsWith("419")   // Används ej
        ) {
      return false;
    }

    String postalTown = postalTownByTwoDigitPostalCodePrefix.get(postnummer.substring(0,2));
    if (postalTown != null) {
      return isSpecialTwoNumberUtdelningspost(postnummer);
    }

    // todo är alla andra tvånummer? jag tror inte det.


    return null;
  }

  /**
   * Postnummer i postorter som identifieras med 2 prefix-siffor.
   * De har minst 20000 invånare.
   *
   * @param postnummer
   * @return
   */
  public Boolean isSpecialTwoNumberUtdelningspost(String postnummer) {
    if ("0".equals(postnummer.substring(2, 3))      // Box, postala enheter
        || "10".equals(postnummer.substring(2, 4))  // Box, postala enheter
        || "11".equals(postnummer.substring(2, 4))  // Box, postala enheter
        || "18".equals(postnummer.substring(2, 4))  // Storkund
        || "19".equals(postnummer.substring(2, 4))  // Storkund
        || "8".equals(postnummer.substring(2, 3))   // Svarspost/Frisvar
        || "9".equals(postnummer.substring(2, 3))   // Tävlingspost
        ) {
      return false;
    } else if ("2".equals(postnummer.substring(2, 3))  // Utdelningspost i tätort
        || "3".equals(postnummer.substring(2, 3))      // Utdelningspost i tätort
        || "4".equals(postnummer.substring(2, 3))      // Utdelningspost i tätort
        || "5".equals(postnummer.substring(2, 3))      // Utdelningspost på landsbygd
        ) {
      return true;
    } else {
      return null;
    }

  }

  /**
   * Postnummer i postorter som identifieras med 2 prefix-siffor
   *
   * @param postnummer
   * @return
   */
  public Boolean isSpecialThreeNumberUtdelningspost(String postnummer) {
    return null;
  }


}
