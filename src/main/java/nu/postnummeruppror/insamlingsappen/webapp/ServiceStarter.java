package nu.postnummeruppror.insamlingsappen.webapp;

import nu.postnummeruppror.insamlingsappen.Insamlingsappen;
import nu.postnummeruppror.insamlingsappen.Nightly;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author kalle
 * @since 2014-09-06 00:41
 */
public class ServiceStarter implements ServletContextListener {

  @Override
  public void contextInitialized(ServletContextEvent servletContextEvent) {
    try {
      Insamlingsappen.getInstance().open();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    Nightly.getInstance().start();
  }

  @Override
  public void contextDestroyed(ServletContextEvent servletContextEvent) {

    Nightly.getInstance().stop();

    try {
      Insamlingsappen.getInstance().close();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
