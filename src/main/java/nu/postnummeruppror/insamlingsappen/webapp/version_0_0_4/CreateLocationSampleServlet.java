package nu.postnummeruppror.insamlingsappen.webapp.version_0_0_4;

import nu.postnummeruppror.insamlingsappen.Insamlingsappen;
import nu.postnummeruppror.insamlingsappen.domain.LocationSample;
import nu.postnummeruppror.insamlingsappen.transactions.CreateLocationSample;
import nu.postnummeruppror.insamlingsappen.transactions.IdentityFactory;
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
 * @since 2014-09-10 18:50
 */
public class CreateLocationSampleServlet extends HttpServlet {

  private static final Logger log = LoggerFactory.getLogger(CreateLocationSampleServlet.class);

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    StringBuilder documentation = new StringBuilder(1024);
    documentation.append("This service accept UTF-8 encoded HTTP post.\n");
    documentation.append("The server will create a new LocationSample based on the JSON request.\n");
    documentation.append("\n");
    documentation.append("JSON request:\n");
    documentation.append("{\n");
    documentation.append("\n");
    documentation.append("  \"accountIdentity\": Required. String value. User defined unique account identity, preferably an UUID.\n");
    documentation.append("  \"application\": Required. String value. Application used to produce this data, e.g. 'insamlingsappen-android', 'webapp', etc.\n");
    documentation.append("  \"applicationVersion\": Required. String value. Version of application that produced this data, e.g. '0.0.3'.\n");
    documentation.append("\n");
    documentation.append("  \"postalCode\": Optional. String value. E.g. '12345'\n");
    documentation.append("  \"postalTown\": Optional. String value. E.g. 'Stockholm'\n");
    documentation.append("  \"streetName\": Optional. String value. E.g. 'Drottningatan'\n");
    documentation.append("  \"houseNumber\": Optional. String value. E.g '12'\n");
    documentation.append("  \"houseName\": Optional. String value. E.g. 'A'\n");
    documentation.append("\n");
    documentation.append("  \"provider\": Required. String value. Source of location, e.g. 'gps', 'network', 'human', etc.\n");
    documentation.append("  \"accuracy\": Required. Double value. Maximum error in meters.\n");
    documentation.append("  \"latitude\": Required. Double value. EPSG:3857\n");
    documentation.append("  \"longitude\": Required. Double value. EPSG:3857\n");
    documentation.append("  \"altitude\": Optional. Double value. Meters altitude above sea.\n");
    documentation.append("\n");
    documentation.append("}\n");

    documentation.append("\n");
    documentation.append("\n");
    documentation.append("JSON response:\n");
    documentation.append("{\n");
    documentation.append("\n");
    documentation.append("  \"success\": Boolean value.\n");
    documentation.append("\n");
    documentation.append("  \"identity\": Long value. Identity of the newly created LocationSample.\n");
    documentation.append("\n");

    documentation.append("}\n");

    response.setContentType("text/plain");
    response.setCharacterEncoding("UTF-8");
    response.getOutputStream().write(documentation.toString().getBytes("UTF-8"));


  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    try {

      JSONObject requestJSON = new JSONObject(new JSONTokener(IOUtils.toString(request.getInputStream(), "UTF-8")));

      log.debug("Incoming request: " + requestJSON.toString());

      CreateLocationSample createLocationSample = new CreateLocationSample();

      createLocationSample.setLocationSampleIdentity(Insamlingsappen.getInstance().getPrevayler().execute(new IdentityFactory()));

      createLocationSample.setAccountIdentity(requestJSON.getString("accountIdentity"));

      createLocationSample.setApplication(requestJSON.getString("application"));
      createLocationSample.setApplicationVersion(requestJSON.getString("applicationVersion"));

      if (requestJSON.has("postalCode")) {
        createLocationSample.setPostalCode(requestJSON.getString("postalCode"));
      }

      if (requestJSON.has("postalTown")) {
        createLocationSample.setPostalTown(requestJSON.getString("postalTown"));
      }

      if (requestJSON.has("streetName")) {
        createLocationSample.setStreetName(requestJSON.getString("streetName"));
      }

      if (requestJSON.has("houseNumber")) {
        createLocationSample.setHouseNumber(requestJSON.getString("houseNumber"));
      }

      if (requestJSON.has("houseName")) {
        createLocationSample.setHouseName(requestJSON.getString("houseName"));
      }

      createLocationSample.setProvider(requestJSON.getString("provider"));
      createLocationSample.setLatitude(requestJSON.getDouble("latitude"));
      createLocationSample.setLongitude(requestJSON.getDouble("longitude"));
      if (requestJSON.has("accuracy")) {
        createLocationSample.setAccuracy(requestJSON.getDouble("accuracy"));
      }
      if (requestJSON.has("altitude")) {
        createLocationSample.setAltitude(requestJSON.getDouble("altitude"));
      }

      LocationSample locationSample = Insamlingsappen.getInstance().getPrevayler().execute(createLocationSample);

      log.info("Created location sample " + requestJSON.toString());

      JSONObject responseJSON = new JSONObject();

      responseJSON.put("success", true);
      responseJSON.put("identity", locationSample.getIdentity());

      response.setCharacterEncoding("UTF-8");
      response.setContentType("application/json");
      response.getOutputStream().write(responseJSON.toString().getBytes("UTF-8"));

      Insamlingsappen.getInstance().getLocationSampleIndex().update(locationSample);

    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }
}
