package nu.postnummeruppror.insamlingsappen.webapp;

import nu.postnummeruppror.insamlingsappen.Insamlingsappen;
import nu.postnummeruppror.insamlingsappen.domain.LocationSample;
import nu.postnummeruppror.insamlingsappen.index.LocationSampleCoordinateCircleEnvelopeQueryFactory;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author kalle
 * @since 2014-09-08 14:21
 */
public class LocationStatisticsServlet extends NoHammeringHttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    if (noHammering(request, response)) {

      try {

        JSONObject json = createResponse(
            Double.valueOf(request.getParameter("latitude")),
            Double.valueOf(request.getParameter("longitude"))
        );

        sendResponse(json, request, response);

      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }


  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    if (noHammering(request, response)) {

      try {

        String jsonString = IOUtils.toString(request.getInputStream(), "UTF-8");
        JSONObject requestJson = new JSONObject(new JSONTokener(jsonString));

        JSONObject json = createResponse(
            requestJson.getDouble("latitude"),
            requestJson.getDouble("longitude")
        );

        sendResponse(json, request, response);

      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

  }

  private JSONObject createResponse(double latitude, double longitude) throws Exception {


    JSONObject json = new JSONObject();

    Map<LocationSample, Float> hits;

    hits = Insamlingsappen.getInstance().getLocationSampleIndex().search(new LocationSampleCoordinateCircleEnvelopeQueryFactory()
        .setCentroidLatitude(latitude)
        .setCentroidLongitude(longitude)
        .setRadiusKilometers(0.1d)
        .build());

    json.put("locationSamplesWithinOneHundredMetersRadius", hits.size());

    hits = Insamlingsappen.getInstance().getLocationSampleIndex().search(new LocationSampleCoordinateCircleEnvelopeQueryFactory()
        .setCentroidLatitude(latitude)
        .setCentroidLongitude(longitude)
        .setRadiusKilometers(0.5d)
        .build());

    json.put("locationSamplesWithinFiveHundredMetersRadius", hits.size());

    json.put("success", true);

    return json;

  }


  private void sendResponse(JSONObject json, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.getOutputStream().write(json.toString().getBytes("UTF-8"));
    response.getOutputStream().close();


  }


}
