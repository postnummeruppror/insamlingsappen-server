package nu.postnummeruppror.insamlingsappen;

import nu.postnummeruppror.insamlingsappen.domain.Account;
import nu.postnummeruppror.insamlingsappen.domain.LocationSample;
import nu.postnummeruppror.insamlingsappen.domain.Root;
import org.json.JSONArray;
import org.json.JSONObject;
import org.prevayler.Query;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author kalle
 * @since 2017-12-05
 */
public class ProduceTimeLineStatistics {

  public static void main (String[] args) throws Exception {
    Insamlingsappen.getInstance().open();
    try {
      Writer out = new OutputStreamWriter(new FileOutputStream(new File("src/main/webapp/nightly/stats.json")), "UTF8");
      JSONArray stats = new ProduceTimeLineStatistics().execute();
      stats.write(out);
      out.close();
    } finally {
      Insamlingsappen.getInstance().close();
    }
  }

  private static class Item {
    private String date;
    private int totalSampleCount = 0;
    private int totalPostalCodeCount = 0;
    private int totalPostalTownCount = 0;
    private List<LocationSample> newSamples = new ArrayList<>();
    private List<Account> newAccounts = new ArrayList<>();
    private int totalAccountsCount =0;
    private Set<String> newPostalCodes = new HashSet<>();
    private Set<String> newPostalTowns = new HashSet<>();
  }

  public JSONArray execute() throws Exception {

    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    Map<String, Item> items = new HashMap<>();

    Set<String> postalCodes = new HashSet<>();
    Set<String> postalTowns = new HashSet<>();

    // add accounts

    List<Account> accounts = Insamlingsappen.getInstance().getPrevayler().execute(new Query<Root, List<Account>>() {
      @Override
      public List<Account> query(Root root, Date date) throws Exception {
        return new ArrayList<>(root.getAccounts().values());
      }
    });
    for (Account account : accounts) {

      String date = dateFormat.format(new Date(account.getTimestampCreated()));

      Item item = items.get(date);

      if (item == null) {
        item = new Item();
        item.date = date;
        items.put(date, item);
      }

      item.newAccounts.add(account);

    }


    // add samples

    List<LocationSample> locationSamples = Insamlingsappen.getInstance().getPrevayler().execute(new Query<Root, List<LocationSample>>() {
      @Override
      public List<LocationSample> query(Root root, Date date) throws Exception {
        return new ArrayList<LocationSample>(root.getLocationSamples().values());
      }
    });


    for (LocationSample locationSample : locationSamples) {
      String date = dateFormat.format(new Date(locationSample.getTimestamp()));

      Item item = items.get(date);

      if (item == null) {
        item = new Item();
        item.date = date;
        items.put(date, item);
      }

      item.newSamples.add(locationSample);

      String postalCode = locationSample.getTag("addr:postcode");
      if (postalCode != null) {
        postalCode = postalCode.replaceAll("\\D+", "");
        if (postalCode.length() == 5 && postalCodes.add(postalCode)) {
          item.newPostalCodes.add(postalCode);
        }
      }

      String postalTown = locationSample.getTag("addr:city");
      if (postalTown != null) {
        postalTown = postalTown.toUpperCase();
        postalTown = postalTown.replaceAll("\\s+", "");
        postalTown = postalTown.replaceAll("\\p{Punct}+", "");
        if (!postalTown.isEmpty() && postalTowns.add(postalTown)) {
          item.newPostalTowns.add(locationSample.getTag("addr:city"));
        }
      }

    }


    // set total counts in all items

    List<Item> itemsByDate = new ArrayList<>(items.values());
    Collections.sort(itemsByDate, new Comparator<Item>() {
      @Override
      public int compare(Item o1, Item o2) {
        return o1.date.compareTo(o2.date);
      }
    });

    int totalSampleCount = 0;
    int totalPostalCodeCount = 0;
    int totalPostalTownCount = 0;
    int totalAcccountsCount = 0;
    for (Item item : itemsByDate) {
      totalSampleCount += item.newSamples.size();
      item.totalSampleCount = totalSampleCount;

      totalPostalCodeCount += item.newPostalCodes.size();
      item.totalPostalCodeCount = totalPostalCodeCount;

      totalPostalTownCount += item.newPostalTowns.size();
      item.totalPostalTownCount = totalPostalTownCount;

      totalAcccountsCount += item.newAccounts.size();
      item.totalAccountsCount = totalAcccountsCount;

    }

    JSONArray response = new JSONArray();
    for (Item item : itemsByDate) {

      JSONArray newPostalCodes = new JSONArray();
      for (String postalCode : item.newPostalCodes) {
        newPostalCodes.put(postalCode);
      }

      JSONArray newPostalTowns = new JSONArray();
      for (String postalTown : item.newPostalTowns) {
        newPostalTowns.put(postalTown);
      }

      JSONObject jsonItem = new JSONObject(new LinkedHashMap<>());
      jsonItem.put("date", item.date);
      jsonItem.put("newSamplesCount", item.newSamples.size());
      jsonItem.put("totalSampleCount", item.totalSampleCount);
      jsonItem.put("newPostalCodes", newPostalCodes);
      jsonItem.put("totalPostalCodeCount", item.totalPostalCodeCount);
      jsonItem.put("newPostalTowns", newPostalTowns);
      jsonItem.put("totalPostalTownCount", item.totalPostalTownCount);
      jsonItem.put("newAccountsCount", item.newAccounts.size());
      jsonItem.put("totalAccountsCount", item.totalAccountsCount);
      response.put(jsonItem);
    }

    return response;

  }
  
}
