package nu.postnummeruppror.insamlingsappen.webapp;

import nu.postnummeruppror.insamlingsappen.Insamlingsappen;
import nu.postnummeruppror.insamlingsappen.domain.LocationSample;
import nu.postnummeruppror.insamlingsappen.index.LocationSampleCoordinateCircleEnvelopeQueryFactory;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author kalle
 * @since 2014-09-07 03:48
 */
public class SearchLocationSampleServlet extends NoHammeringHttpServlet {

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    if (noHammering(request, response)) {

      try {

        String jsonString = IOUtils.toString(request.getInputStream(), "UTF-8");
        JSONObject requestJson = new JSONObject(new JSONTokener(jsonString));

        JSONObject json = search(
            requestJson.getDouble("south"),
            requestJson.getDouble("west"),
            requestJson.getDouble("north"),
            requestJson.getDouble("east"),
            requestJson.getInt("maximumHits"),
            requestJson.getString("reference")
        );

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getOutputStream().write(json.toString().getBytes("UTF-8"));

      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    if (noHammering(request, response)) {
      try {

        JSONObject json = search(
            Double.valueOf(request.getParameter("south")),
            Double.valueOf(request.getParameter("west")),
            Double.valueOf(request.getParameter("north")),
            Double.valueOf(request.getParameter("east")),
            Integer.valueOf(request.getParameter("maximumHits")),
            request.getParameter("reference")
        );

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getOutputStream().write(json.toString().getBytes("UTF-8"));

      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

  }


  public JSONObject search(double south, double west, double north, double east, int maximumHits, String reference) throws Exception {

    Map<LocationSample, Float> hits = Insamlingsappen.getInstance().getLocationSampleIndex().search(new LocationSampleCoordinateCircleEnvelopeQueryFactory()
        .setSouth(south)
        .setWest(west)
        .setNorth(north)
        .setEast(east)
        .build());

    JSONObject json = new JSONObject();

    json.put("success", true);
    json.put("reference", reference);
    json.put("numberOfHits", hits.size());

    JSONArray hitsJson = new JSONArray();
    json.put("hits", hitsJson);

    for (LocationSample hit : hits.keySet()) {
      JSONObject hitJson = new JSONObject();
      hitJson.put("identity", hit.getIdentity());

      hitJson.put("postalCode", hit.getPostalCode().getPostalCode());
      hitJson.put("postalTown", hit.getPostalTown());
      hitJson.put("houseNumber", hit.getHouseNumber());
      hitJson.put("streetName", hit.getStreetName());

      hitJson.put("latitude", hit.getLatitude());
      hitJson.put("longitude", hit.getLongitude());
      hitJson.put("accuracy", hit.getAccuracy());
      hitJson.put("altitude", hit.getAltitude());

      hitsJson.put(hitJson);

      if (hitsJson.length() >= maximumHits) {
        break;
      }
    }

    return json;

  }

}
