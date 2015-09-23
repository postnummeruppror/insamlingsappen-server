package nu.postnummeruppror.insamlingsappen;

import nu.postnummeruppror.insamlingsappen.domain.LocationSample;
import nu.postnummeruppror.insamlingsappen.queries.GetUniquePostalCodes;
import nu.postnummeruppror.insamlingsappen.queries.GetUniquePostalTowns;
import org.apache.commons.lang.StringEscapeUtils;

import java.io.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

/**
 * @author kalle
 * @since 20/09/15 20:44
 */
public class Nightly {


  private NightlyRunnable runnable;

  public void start() {
    Thread thread = new Thread(runnable = new NightlyRunnable());
    thread.setDaemon(true);
    thread.setName("Nightly jobs thread");
    thread.start();
  }

  public void stop() {
    runnable.stop();
  }

  private static class NightlyRunnable implements Runnable {

    File nightlyPath = new File("src/main/webapp/nightly/");

    private boolean stopping = false;

    private void stop() {
      stopping = true;
    }

    @Override
    public void run() {

      long previousExecution = Long.MIN_VALUE;

      DateFormat sdf = new SimpleDateFormat("HH");

      while (!stopping) {

        if (!nightlyPath.exists()) {
          nightlyPath.mkdirs();
          try {
            execute();
          } catch (Exception e) {
            e.printStackTrace();
          }
          continue;
        }

        Long hour = Long.valueOf(sdf.format(new Date(System.currentTimeMillis())));
        if (hour == 0) {
          if (previousExecution < System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)) {
            previousExecution = System.currentTimeMillis();
            try {
              execute();
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        }


        try {
          Thread.sleep(TimeUnit.MINUTES.toMillis(1));
        } catch (Exception e) {
          throw new RuntimeException(e);
        }

      }
    }

    private void execute() throws Exception {


      {
        Writer out = new OutputStreamWriter(new FileOutputStream(new File(nightlyPath, "postorter.utf8.txt")), "UTF8");
        out.write("# Postorter i postnummeruppror.nu ");
        out.write(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(System.currentTimeMillis())));
        out.write("\n");
        List<String> postalTowns = new ArrayList<>(Insamlingsappen.getInstance().getPrevayler().execute(new GetUniquePostalTowns()));
        Collections.sort(postalTowns);
        for (String postalTown : postalTowns) {
          out.write(postalTown);
          out.write("\n");
        }
        out.close();
      }

      {
        Writer out = new OutputStreamWriter(new FileOutputStream(new File(nightlyPath, "postnummer_postort.utf8.txt")), "UTF8");
        out.write("# Postnummer och postort i postnummeruppror.nu ");
        out.write(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(System.currentTimeMillis())));
        out.write("\n");
        out.write("# Notera att postorten inte är 100% säker, detta är en sammanställning av postnummer med en i databasen slumpmässigt förekommande postort för det givna postnummret.\n");
        List<Map.Entry<String, String>> postalCodes = new ArrayList<>(Insamlingsappen.getInstance().getPrevayler().execute(new GetUniquePostalCodes()));
        Collections.sort(postalCodes, new Comparator<Map.Entry<String, String>>() {
          @Override
          public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
            int ret = o2.getKey().compareTo(o2.getKey());
            if (ret == 0) {
              ret = o1.getValue().compareTo(o1.getValue());
            }
            return ret;
          }
        });
        for (Map.Entry<String, String> postalCode : postalCodes) {
          out.write(postalCode.getKey());
          out.write("\t");
          out.write(postalCode.getValue());
          out.write("\n");
        }
        out.close();
      }


      {

        Writer xml = new OutputStreamWriter(new FileOutputStream(new File(nightlyPath, "samples-with-coordinates.osm.xml")), "UTF-8");
        try {

          xml.write("<?xml version='1.0' encoding='");
          xml.write("UTF8");
          xml.write("'?>\n");

          xml.write("<osm version='");
          xml.write("0.6");
          xml.write("' upload='");
          xml.write("true");
          xml.write("'");
          xml.write(" generator='");
          xml.write("postnummeruppror.nu nightly");
          xml.write("'>\n");

          int id = 0;
          DecimalFormat df = new DecimalFormat("#.##################################");

          for (LocationSample locationSample : new ArrayList<>(Insamlingsappen.getInstance().getPrevayler().prevalentSystem().getLocationSamples().values())) {

            if (locationSample.getCoordinate() == null
                || locationSample.getCoordinate().getLatitude() == null
                || locationSample.getCoordinate().getLongitude() == null) {
              continue;
            }

            xml.write("\t<node ");
            xml.write(" id='");
            xml.write(String.valueOf(--id));
            xml.write("'");

            xml.write(" lat='");
            xml.write(df.format(locationSample.getCoordinate().getLatitude()));
            xml.write("'");

            xml.write(" lon='");
            xml.write(df.format(locationSample.getCoordinate().getLongitude()));
            xml.write("'");

            xml.write(" >\n");

            xml.write("\t\t<tag k='");
            xml.write("source");
            xml.write("' v='");
            xml.write("postnummeruppror.nu");
            xml.write("' />\n");

            xml.write("\t\t<tag k='");
            xml.write("postnummeruppror.nu:location_sample:id");
            xml.write("' v='");
            xml.write(String.valueOf(locationSample.getIdentity()));
            xml.write("' />\n");

            for (Map.Entry<String, String> tag : locationSample.getTags().entrySet()) {
              xml.write("\t\t<tag k='");
              xml.write(StringEscapeUtils.escapeXml(tag.getKey()));
              xml.write("' v='");
              xml.write(StringEscapeUtils.escapeXml(tag.getValue()));
              xml.write("' />\n");
            }

            if (locationSample.getCoordinate().getProvider() != null) {
              xml.write("\t\t<tag k='");
              xml.write("position:provider");
              xml.write("' v='");
              xml.write(StringEscapeUtils.escapeXml(locationSample.getCoordinate().getProvider()));
              xml.write("' />\n");
            }

            if (locationSample.getCoordinate().getAccuracy() != null) {
              xml.write("\t\t<tag k='");
              xml.write("position:accuracy");
              xml.write("' v='");
              xml.write(StringEscapeUtils.escapeXml(df.format(locationSample.getCoordinate().getAccuracy())));
              xml.write("' />\n");
            }

            if (locationSample.getCoordinate().getAltitude() != null) {
              xml.write("\t\t<tag k='");
              xml.write("position:altitude");
              xml.write("' v='");
              xml.write(StringEscapeUtils.escapeXml(df.format(locationSample.getCoordinate().getAltitude())));
              xml.write("' />\n");
            }


            xml.write("\t</node>\n");

          }

          xml.write("</osm>\n");


        } finally {
          xml.flush();
        }


      }

    }
  }


}
