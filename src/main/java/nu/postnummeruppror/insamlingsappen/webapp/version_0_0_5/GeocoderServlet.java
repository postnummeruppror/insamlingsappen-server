package nu.postnummeruppror.insamlingsappen.webapp.version_0_0_5;

import nu.postnummeruppror.insamlingsappen.Insamlingsappen;
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

/**
 * @author kalle
 * @since 2014-09-25 19:17
 */
public class GeocoderServlet extends HttpServlet {

  private static final Logger log = LoggerFactory.getLogger(GeocoderServlet.class);

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {



  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    try {

      JSONObject requestJSON = new JSONObject(new JSONTokener(IOUtils.toString(request.getInputStream(), "UTF-8")));

      log.debug("Incoming request: " + requestJSON.toString());

      String postalTownQuery = requestJSON.getString("postalTown").trim().toUpperCase();

      for (LocationSample locationSample : Insamlingsappen.getInstance().getPrevayler().prevalentSystem().getLocationSamples().values()) {
        if (locationSample.getCoordinate() != null
            && locationSample.getPostalAddress() != null
            && locationSample.getPostalAddress().getPostalTown() != null) {

          if (postalTownQuery.equals(locationSample.getPostalAddress().getPostalTown().toUpperCase())) {



          }

        }
      }


    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }
}
