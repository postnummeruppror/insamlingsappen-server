package nu.postnummeruppror.insamlingsappen;

import nu.postnummeruppror.insamlingsappen.queries.GetUniquePostalTowns;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    private boolean stopping = false;

    private void stop() {
      stopping = true;
    }

    @Override
    public void run() {

      long previousExecution = Long.MIN_VALUE;

      DateFormat sdf = new SimpleDateFormat("HH");

      while (!stopping) {


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

      File nightlyPath = new File("src/main/webapp/nightly/");
      nightlyPath.mkdirs();

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

    }
  }


}
