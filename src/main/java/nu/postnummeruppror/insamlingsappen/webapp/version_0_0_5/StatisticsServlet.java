package nu.postnummeruppror.insamlingsappen.webapp.version_0_0_5;

import nu.postnummeruppror.insamlingsappen.Insamlingsappen;
import nu.postnummeruppror.insamlingsappen.domain.Account;
import nu.postnummeruppror.insamlingsappen.domain.LocationSample;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author kalle
 * @since 2014-09-24 21:39
 */
public class StatisticsServlet extends HttpServlet {

  private static final Logger log = LoggerFactory.getLogger(StatisticsServlet.class);

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    try {
      JSONObject requestJSON = new JSONObject(new JSONTokener(IOUtils.toString(request.getInputStream(), "UTF-8")));

      log.debug("Incoming request: " + requestJSON.toString());

      // 'accounts', 'location samples', 'postal towns', 'postal codes'
      String dimension = requestJSON.getString("dimension");

      // 'new' or 'total'
      String type = requestJSON.getString("type");

      long timestampFrom = requestJSON.getLong("timestampFrom");
      long timestampTo = requestJSON.getLong("timestampTo");
      long interval = requestJSON.getLong("interval");


      if ("accounts".equalsIgnoreCase(dimension)) {
        Buckets<Account> buckets = new Buckets<>(timestampFrom, timestampTo, interval);

        if ("new".equalsIgnoreCase(type)) {

          for (Account account : Insamlingsappen.getInstance().getPrevayler().prevalentSystem().getAccounts().values()) {
            buckets.getBucket(account.getTimestampCreated()).increaseValue(1d);
          }


        } else if ("total".equals(type)) {

          for (Account account : Insamlingsappen.getInstance().getPrevayler().prevalentSystem().getAccounts().values()) {
            for (Bucket bucket : buckets) {
              if (bucket.getTimestampFrom() >= account.getTimestampCreated()) {
                bucket.increaseValue(1d);
              }
            }
          }

        }

        buckets.writeJSON(response.getWriter());


      } else if ("location samples".equalsIgnoreCase(dimension)) {

        Buckets<LocationSample> buckets = new Buckets<>(timestampFrom, timestampTo, interval);

        if ("new".equalsIgnoreCase(type)) {

          for (LocationSample locationSample : Insamlingsappen.getInstance().getPrevayler().prevalentSystem().getLocationSamples().values()) {
            buckets.getBucket(locationSample.getTimestamp()).increaseValue(1d);
          }


        } else if ("total".equals(type)) {

          for (LocationSample locationSample : Insamlingsappen.getInstance().getPrevayler().prevalentSystem().getLocationSamples().values()) {
            for (Bucket bucket : buckets) {
              if (bucket.getTimestampFrom() >= locationSample.getTimestamp()) {
                bucket.increaseValue(1d);
              }
            }
          }

        }

        buckets.writeJSON(response.getWriter());


      } else if ("postal towns".equalsIgnoreCase(dimension)) {

        Buckets<String> buckets = new Buckets<>(timestampFrom, timestampTo, interval);

        if ("new".equalsIgnoreCase(type)) {

          for (LocationSample locationSample : Insamlingsappen.getInstance().getPrevayler().prevalentSystem().getLocationSamples().values()) {

            if (locationSample.getPostalAddress() != null
                && locationSample.getPostalAddress().getPostalTown() != null) {

              String postalTown = locationSample.getPostalAddress().getPostalTown().trim().toUpperCase();
              if (!postalTown.isEmpty()) {
                Bucket bucket = buckets.getBucket(locationSample.getTimestamp());
                if (bucket.getObjects().add(locationSample.getPostalAddress().getPostalTown())) {
                  bucket.increaseValue(1d);
                }
              }
            }
          }


        } else if ("total".equals(type)) {

          for (LocationSample locationSample : Insamlingsappen.getInstance().getPrevayler().prevalentSystem().getLocationSamples().values()) {
            for (Bucket bucket : buckets) {
              if (bucket.getTimestampFrom() >= locationSample.getTimestamp()) {
                bucket.increaseValue(1d);
              }
            }
          }

        }

        buckets.writeJSON(response.getWriter());


      } else if ("postal codes".equalsIgnoreCase(dimension)) {

      } else {
        throw new RuntimeException("Unsupported dimension: " + dimension);
      }


    } catch (Exception e) {
      throw new RuntimeException(e);
    }


  }

  public static class Buckets<T> extends ArrayList<Bucket<T>> {


    public Buckets(long timestampFrom, long timestampTo, long interval) {

      for (long timestamp = timestampFrom; timestamp <= timestampTo; timestamp += interval) {
        Bucket<T> bucket = new Bucket<>();
        bucket.timestampFrom = timestamp;
        bucket.timestampTo = timestamp + interval;
        add(bucket);
      }

    }

    public void writeJSON(Writer writer) throws IOException {

      for (Iterator<Bucket<T>> iterator = iterator(); iterator.hasNext(); ) {
        Bucket<T> bucket = iterator.next();
        writer.write("{");

        writer.write("timestampFrom:");
        writer.write(String.valueOf(bucket.getTimestampFrom()));
        writer.write(",timestampTo:");
        writer.write(String.valueOf(bucket.getTimestampTo()));
        writer.write(",value:");
        writer.write(String.valueOf(bucket.getValue()));
        writer.write("}");
      }

    }


    public Bucket getBucket(long timestamp) {
      // todo binary search!
      for (Bucket bucket : this) {
        if (bucket.matchTimestamp(timestamp)) {
          return bucket;
        }
      }

      throw new RuntimeException();
    }

  }

  public static class Bucket<T> {

    private long timestampFrom;
    private long timestampTo;

    private double value;

    private Set<T> objects = new HashSet<>();

    public boolean matchTimestamp(long timestamp) {
      return timestampFrom >= timestamp
          && timestampTo <= timestamp;
    }

    public double increaseValue(double amount) {
      value += amount;
      return value;
    }

    public long getTimestampFrom() {
      return timestampFrom;
    }

    public void setTimestampFrom(long timestampFrom) {
      this.timestampFrom = timestampFrom;
    }

    public long getTimestampTo() {
      return timestampTo;
    }

    public void setTimestampTo(long timestampTo) {
      this.timestampTo = timestampTo;
    }

    public double getValue() {
      return value;
    }

    public void setValue(double value) {
      this.value = value;
    }

    public Set<T> getObjects() {
      return objects;
    }

    public void setObjects(Set<T> objects) {
      this.objects = objects;
    }
  }

}
