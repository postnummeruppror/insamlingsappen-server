package nu.postnummeruppror.insamlingsappen.webapp.version_0_0_6;

import nu.postnummeruppror.insamlingsappen.Insamlingsappen;
import nu.postnummeruppror.insamlingsappen.domain.LocationSample;
import nu.postnummeruppror.insamlingsappen.transactions.DeleteLocationSample;
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

/**
 * @author kalle
 * @since 2014-09-26 17:42
 */
public class DeleteLocationSampleServlet extends HttpServlet {

  private static final Logger log = LoggerFactory.getLogger(DeleteLocationSampleServlet.class);

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    response.setHeader("Access-Control-Allow-Origin", "*");

  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    response.setHeader("Access-Control-Allow-Origin", "*");

    try {

      JSONObject requestJSON = new JSONObject(new JSONTokener(IOUtils.toString(request.getInputStream(), "UTF-8")));

      log.debug("Incoming request: " + requestJSON.toString());


      LocationSample locationSample = Insamlingsappen.getInstance().getPrevayler().prevalentSystem().getLocationSamples().get(requestJSON.getLong("locationSampleIdentity"));

      Boolean superuser = (Boolean)request.getSession().getAttribute("superuser");
      if (superuser == null || !superuser) {
        if (!locationSample.getAccount().getIdentity().equals(requestJSON.getString("accountIdentity"))) {
          throw new RuntimeException("accountIdentity does not match");
        }
      }

      Insamlingsappen.getInstance().getPrevayler().execute(new DeleteLocationSample(locationSample));

    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }
}
