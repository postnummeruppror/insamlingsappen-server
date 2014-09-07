package nu.postnummeruppror.insamlingsappen.webapp;

import nu.postnummeruppror.insamlingsappen.Insamlingsappen;
import nu.postnummeruppror.insamlingsappen.domain.Account;
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
 * @since 2014-09-06 01:21
 */
public class CreateLocationSampleServlet extends HttpServlet {

  private static final Logger log = LoggerFactory.getLogger(CreateLocationSampleServlet.class);

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    try {
      String jsonString = IOUtils.toString(request.getInputStream(), "UTF-8");
      JSONObject json = new JSONObject(new JSONTokener(jsonString));

      Account account = Insamlingsappen.getInstance().getPrevayler().prevalentSystem().getAccounts().get(json.getString("accountIdentity"));

      CreateLocationSample createLocationSample = new CreateLocationSample();

      createLocationSample.setLocationSampleIdentity(Insamlingsappen.getInstance().getPrevayler().execute(new IdentityFactory()));

      createLocationSample.setAccountIdentity(account.getIdentity());

      createLocationSample.setPostalCode(json.getString("postalCode"));

      if (json.has("streetName")) {
        createLocationSample.setStreetName(json.getString("streetName"));
      }

      if (json.has("houseNumber")) {
        createLocationSample.setHouseNumber(json.getString("houseNumber"));
      }

      createLocationSample.setProvider(json.getString("provider"));
      createLocationSample.setAccuracy(json.getDouble("accuracy"));
      createLocationSample.setLatitude(json.getDouble("latitude"));
      createLocationSample.setLongitude(json.getDouble("longitude"));
      createLocationSample.setAltitude(json.getDouble("altitude"));

      LocationSample locationSample = Insamlingsappen.getInstance().getPrevayler().execute(createLocationSample);

      log.info("Created location sample " + locationSample);

    } catch (Exception e) {
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      response.getOutputStream().write("{ \"success\": false }".getBytes("UTF-8"));

      e.printStackTrace();

      return;

    }

    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.getOutputStream().write("{ \"success\": true }".getBytes("UTF-8"));

  }
}
