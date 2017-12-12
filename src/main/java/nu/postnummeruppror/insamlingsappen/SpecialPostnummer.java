package nu.postnummeruppror.insamlingsappen;

/**
 * @author kalle
 * @since 2017-12-12 12:27
 */
public class SpecialPostnummer {

  public boolean isSpecial(String postnummer) {

    if (postnummer == null) {
      throw new NullPointerException();
    }

    postnummer = postnummer.replaceAll("\\D+", "");
    if (postnummer.length() != 5) {
      throw new IllegalArgumentException("Postal codes are 5 digits");
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
      return true;
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
      return false;
    } else if (postnummer.startsWith("120")) {
      // Utdelningspost tätort
      // Postnummerserie 120 -- Används enligt principen för tresifferidentifierad
      // ort trots att siffran i position 3 är ”0”
      return isSpecialThreeNumber(postnummer);
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
        || postnummer.startsWith("219")       // Används ej
        ) {
      return true;
    } else if (postnummer.startsWith("211")   // Utdelningspost tätort
        || postnummer.startsWith("212")       // Utdelningspost tätort
        || postnummer.startsWith("213")       // Utdelningspost tätort
        || postnummer.startsWith("214")       // Utdelningspost tätort
        || postnummer.startsWith("215")       // Utdelningspost tätort (även box)
        || postnummer.startsWith("216")       // Utdelningspost tätort
        || postnummer.startsWith("217")       // Utdelningspost tätort
        || postnummer.startsWith("218")       // Utdelningspost tätort
        ) {
      return false;
    }

    // Göteborg
    if (postnummer.startsWith("400")) {
      // todo
    }

    return false;
  }

  /**
   * Postnummer i postorter som identifieras med 2 prefix-siffor
   * @param postnummer
   * @return
   */
  public boolean isSpecialThreeNumber(String postnummer) {
    return false;
    // todo
  }

  /**
   * Postnummer i postorter som identifieras med 2 prefix-siffor
   * @param postnummer
   * @return
   */
  public boolean isSpecialTwoNumber(String postnummer) {
    if ("0".equals(postnummer.substring(2, 3))      // Box, postala enheter
        || "10".equals(postnummer.substring(2, 4))  // Box, postala enheter
        || "11".equals(postnummer.substring(2, 4))  // Box, postala enheter
        || "18".equals(postnummer.substring(2, 4))  // Storkund
        || "19".equals(postnummer.substring(2, 4))  // Storkund
        || "8".equals(postnummer.substring(2, 3))   // Svarspost/Frisvar
        || "9".equals(postnummer.substring(2, 3))   // Tävlingspost
        ) {
      return true;
    } else if ("2".equals(postnummer.substring(2, 3))  // Utdelningspost i tätort
        || "3".equals(postnummer.substring(2, 3))      // Utdelningspost i tätort
        || "4".equals(postnummer.substring(2, 3))      // Utdelningspost i tätort
        || "5".equals(postnummer.substring(2, 3))      // Utdelningspost på landsbygd
        ) {
      return false;
    } else {
      return false;
    }
  }


}
