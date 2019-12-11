package nu.postnummeruppror.insamlingsappen;

import nu.postnummeruppror.insamlingsappen.domain.LocationSample;
import nu.postnummeruppror.insamlingsappen.transactions.DeprecateLocationSample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author kalle
 * @since 2018-03-06
 */
public class HandlePostnummerChangeMarch2018 {

  private static String resourceName = "/postnummer-andringar-2018-03-05.utf8.csv";

  public static void main(String[] args) throws Exception {
    HandlePostnummerChangeMarch2018 handler = new HandlePostnummerChangeMarch2018();
    List<CsvEntry> entries = handler.readCSV();
    System.currentTimeMillis();

    Set<String> deprecatedPostalCodes = new HashSet<>();
    for (CsvEntry entry : entries) {
      deprecatedPostalCodes.add(entry.getOldPostnummer());
      deprecatedPostalCodes.add(entry.getNewPostnummer());
    }

    Insamlingsappen.getInstance().open();
    try {

      for (LocationSample locationSample : Insamlingsappen.getInstance().getPrevayler().prevalentSystem().getLocationSamples().values()) {
        if (!"true".equalsIgnoreCase(locationSample.getTag("deprecated"))
            && locationSample.getTag("addr:postcode") != null
            && deprecatedPostalCodes.contains(locationSample.getTag("addr:postcode"))) {

          if (false) {
            Insamlingsappen.getInstance().getPrevayler().execute(new DeprecateLocationSample(
                locationSample.getIdentity(), "Postnummer not valid since march 2018"
            ));
          }

        }

      }

    } finally {
      Insamlingsappen.getInstance().close();
    }
  }

  public List<CsvEntry> readCSV() throws IOException {
    return readCSV(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(resourceName), StandardCharsets.UTF_8)));
  }

  public List<CsvEntry> readCSV(BufferedReader reader) throws IOException {

    List<CsvEntry> entries = new ArrayList<>(4096);
    String line = reader.readLine(); // skip header
    while ((line = reader.readLine()) != null) {
      line = line.trim();
      String[] values = line.split(";");
      if (values.length != 5) {
        throw new RuntimeException("Expected 5 values, found " + values.length + " in line:\n" + line);
      }
      for (int i=0; i<values.length; i++) {
        values[i] = values[i].trim();
      }
      CsvEntry entry = new CsvEntry();
      entry.setAdress(values[0]);
      entry.setOldPostnummer(values[1]);
      entry.setOldPostort(values[2]);
      entry.setNewPostnummer(values[3]);
      entry.setNewPostort(values[4]);
      entries.add(entry);
    }
    return entries;

  }

  public static class CsvEntry {
    private String adress;
    private String oldPostnummer;
    private String oldPostort;
    private String newPostnummer;
    private String newPostort;

    public String getAdress() {
      return adress;
    }

    public void setAdress(String adress) {
      this.adress = adress;
    }

    public String getOldPostnummer() {
      return oldPostnummer;
    }

    public void setOldPostnummer(String oldPostnummer) {
      this.oldPostnummer = oldPostnummer;
    }

    public String getOldPostort() {
      return oldPostort;
    }

    public void setOldPostort(String oldPostort) {
      this.oldPostort = oldPostort;
    }

    public String getNewPostnummer() {
      return newPostnummer;
    }

    public void setNewPostnummer(String newPostnummer) {
      this.newPostnummer = newPostnummer;
    }

    public String getNewPostort() {
      return newPostort;
    }

    public void setNewPostort(String newPostort) {
      this.newPostort = newPostort;
    }
  }


}
