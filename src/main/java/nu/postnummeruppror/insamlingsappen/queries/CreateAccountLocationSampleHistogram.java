package nu.postnummeruppror.insamlingsappen.queries;

import nu.postnummeruppror.insamlingsappen.Insamlingsappen;
import nu.postnummeruppror.insamlingsappen.domain.Account;
import nu.postnummeruppror.insamlingsappen.domain.Root;
import org.json.JSONArray;
import org.json.JSONObject;
import org.prevayler.Query;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author kalle
 * @since 2014-09-30 13:11
 */
public class CreateAccountLocationSampleHistogram implements Query<Root, JSONArray> {

  public static void main(String[] args) throws Exception {

    Insamlingsappen.getInstance().open();
    try {

      JSONArray histogram = Insamlingsappen.getInstance().getPrevayler().execute(new CreateAccountLocationSampleHistogram());
      for (int i=0; i<histogram.length(); i++) {
        JSONObject bucket = histogram.getJSONObject(i);
        System.out.print(bucket.getInt("numberOfLocationSamples"));
        System.out.print("\t");
        System.out.print(bucket.getInt("numberOfAccounts"));
        System.out.print("\n");
      }

    } finally {
      Insamlingsappen.getInstance().close();
    }

  }

  @Override
  public JSONArray query(Root root, Date executionTime) throws Exception {

    Map<String, AtomicInteger> counterPerEmailAddress = new HashMap<>();
    for (Account account : root.getAccounts().values()) {

      String key;

      if (account.getEmailAddress() == null
          || account.getEmailAddress().trim().isEmpty()) {
        key = account.getIdentity();
      } else {
        key = account.getEmailAddress().trim().toLowerCase();
      }

      AtomicInteger counter = counterPerEmailAddress.get(key);
      if (counter == null) {
        counter = new AtomicInteger();
        counterPerEmailAddress.put(key, counter);
      }
      if (account.getLocationSamples() != null) {
        counter.addAndGet(account.getLocationSamples().size());
      }
    }



    Map<Integer, AtomicInteger> counters = new HashMap<>();

    for (AtomicInteger accountLocationSamples : counterPerEmailAddress.values()) {
      int count;

      AtomicInteger counter = counters.get(accountLocationSamples.get());
      if (counter == null) {
        counter = new AtomicInteger(1);
        counters.put(accountLocationSamples.get(), counter);
      } else {
        counter.incrementAndGet();
      }
    }

    List<Map.Entry<Integer, AtomicInteger>> sorted = new ArrayList<>(counters.entrySet());
    Collections.sort(sorted, new Comparator<Map.Entry<Integer, AtomicInteger>>() {
      @Override
      public int compare(Map.Entry<Integer, AtomicInteger> o1, Map.Entry<Integer, AtomicInteger> o2) {
        return o1.getKey().compareTo(o2.getKey());
      }
    });

    JSONArray histogramJSON = new JSONArray(new ArrayList<>(sorted.size()));

    for (Map.Entry<Integer, AtomicInteger> entry : sorted) {
      JSONObject bucketJSON = new JSONObject();
      bucketJSON.put("numberOfLocationSamples", entry.getKey());
      bucketJSON.put("numberOfAccounts", entry.getValue().get());
      histogramJSON.put(bucketJSON);
    }

    return histogramJSON;

  }
}
