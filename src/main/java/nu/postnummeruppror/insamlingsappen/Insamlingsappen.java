package nu.postnummeruppror.insamlingsappen;

import nu.postnummeruppror.insamlingsappen.domain.Root;
import nu.postnummeruppror.insamlingsappen.index.LocationSampleIndex;
import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * @author kalle@kodapan.se
 * @since 2014-09-06 00:42
 */
public class Insamlingsappen {

  private Logger log = LoggerFactory.getLogger(getClass());

  /**
   * För att teststarta tjänsten
   */
  public static void main(String[] args) throws Exception {
    Insamlingsappen.getInstance().open();
    try {
      // foo
    } finally {
      Insamlingsappen.getInstance().close();
    }
  }

  private static Insamlingsappen instance = new Insamlingsappen();

  public static Insamlingsappen getInstance() {
    return instance;
  }

  private Insamlingsappen() {
  }

  private Prevayler<Root> prevayler;
  private LocationSampleIndex locationSampleIndex;

  public void open() throws Exception {
    PrevaylerFactory<Root> prevaylerFactory = new PrevaylerFactory<>();
    File prevalenceDirectory = new File("data/prevalence");
    if (!prevalenceDirectory.exists() && !prevalenceDirectory.mkdirs()) {
      throw new IOException("Could not mkdirs " + prevalenceDirectory.getAbsolutePath());
    }
    prevaylerFactory.configurePrevalenceDirectory(new File("data/prevalence").getAbsolutePath());
    prevaylerFactory.configurePrevalentSystem(new Root());
    prevayler = prevaylerFactory.create();

    log.info("Prevayler loaded with {} samples", prevayler.prevalentSystem().getLocationSamples().size());

    locationSampleIndex = new LocationSampleIndex();
    locationSampleIndex.open();
    locationSampleIndex.reconstruct();

  }

  public void close() throws Exception {
    locationSampleIndex.close();
    prevayler.close();
  }

  public Prevayler<Root> getPrevayler() {
    return prevayler;
  }

  public LocationSampleIndex getLocationSampleIndex() {
    return locationSampleIndex;
  }
}
