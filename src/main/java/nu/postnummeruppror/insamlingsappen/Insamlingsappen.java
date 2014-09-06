package nu.postnummeruppror.insamlingsappen;

import nu.postnummeruppror.insamlingsappen.domain.DomainStore;

import java.io.File;

/**
 * @author kalle
 * @since 2014-09-06 00:42
 */
public class Insamlingsappen {

  /** För att teststarta tjänsten */
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

  private DomainStore domainStore;

  public void open() throws Exception {
    domainStore = new DomainStore();
    domainStore.setPath(new File("data/domain_store"));
    domainStore.open();
  }

  public void close() throws Exception {
    domainStore.close();
  }

  public DomainStore getDomainStore() {
    return domainStore;
  }

}
