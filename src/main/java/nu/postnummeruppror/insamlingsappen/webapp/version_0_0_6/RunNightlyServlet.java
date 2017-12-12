package nu.postnummeruppror.insamlingsappen.webapp.version_0_0_6;

import nu.postnummeruppror.insamlingsappen.Insamlingsappen;
import nu.postnummeruppror.insamlingsappen.Nightly;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author kalle
 * @since 2017-12-12 01:51
 */
public class RunNightlyServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    if (!req.getParameter("secretKey").equals(System.getProperty("secretKey"))) {
      throw new RuntimeException("Invalid secret key");
    }

    try {
      Nightly.getInstance().getRunnable().execute();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }


  }
}
