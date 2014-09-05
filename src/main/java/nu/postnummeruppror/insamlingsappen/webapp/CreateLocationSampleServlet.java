package nu.postnummeruppror.insamlingsappen.webapp;

import nu.postnummeruppror.insamlingsappen.Insamlingsappen;
import nu.postnummeruppror.insamlingsappen.domain.LocationSample;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author kalle
 * @since 2014-09-06 01:21
 */
public class CreateLocationSampleServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    if (!request.getParameterMap().containsKey("accountIdentity")) {
      throw new IllegalArgumentException("Missing parameter 'accountIdentity'");
    }

    if (!request.getParameterMap().containsKey("latitude")) {
      throw new IllegalArgumentException("Missing parameter 'latitude'");
    }

    if (!request.getParameterMap().containsKey("longitude")) {
      throw new IllegalArgumentException("Missing parameter 'longitude'");
    }

    if (!request.getParameterMap().containsKey("accuracy")) {
      throw new IllegalArgumentException("Missing parameter 'accuracy'");
    }

    if (!request.getParameterMap().containsKey("postalCode")) {
      throw new IllegalArgumentException("Missing parameter 'postalCode'");
    }

    if (!request.getParameterMap().containsKey("device")) {
      throw new IllegalArgumentException("Missing parameter 'device'");
    }

    LocationSample locationSample = new LocationSample();

    locationSample.setTimestamp(System.currentTimeMillis());
    locationSample.setAccountIdentity(request.getParameter("accountIdentity"));
    locationSample.setDevice(request.getParameter("device"));
    locationSample.setPostalCode(request.getParameter("postalCode"));
    locationSample.setLatitude(Double.valueOf(request.getParameter("latitude")));
    locationSample.setLongitude(Double.valueOf(request.getParameter("longitude")));
    locationSample.setAccuracy(Double.valueOf(request.getParameter("accuracy")));
    locationSample.setStreetName(request.getParameter("streetName"));
    locationSample.setHouseNumber(request.getParameter("houseNumber"));

    Insamlingsappen.getInstance().getDomainStore().getLocationSamples().put(locationSample);


  }
}
