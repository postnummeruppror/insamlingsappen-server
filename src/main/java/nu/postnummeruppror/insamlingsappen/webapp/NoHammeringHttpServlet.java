package nu.postnummeruppror.insamlingsappen.webapp;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author kalle
 * @since 2014-09-08 21:37
 */
public class NoHammeringHttpServlet extends HttpServlet {

  /**
   * Avoiding hammering.
   *
   * http://www.webopedia.com/TERM/H/hammering.html
   *
   * @param request
   * @param response
   * @return true if client is not hammering the server
   */
  public boolean noHammering(HttpServletRequest request, HttpServletResponse response) throws IOException {

    boolean hammering = false;

    // todo detect hammering
    // todo ip requests per second

    if (hammering) {

      response.setStatus(500);
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      response.getOutputStream().write("{ \"success\": false, \"error\": \"You are hammering the service!\" }".getBytes("UTF-8"));

    }

    return hammering;
  }


}
