package nu.postnummeruppror.insamlingsappen.webapp.version_0_0_6;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import nu.postnummeruppror.insamlingsappen.Insamlingsappen;
import nu.postnummeruppror.insamlingsappen.domain.LocationSample;
import nu.postnummeruppror.insamlingsappen.queries.FindSamplesByJtsGeometry;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kodapan.geojson.GeoJSONParser;
import se.kodapan.geojson.Polygon;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * @author kalle
 * @since 2017-12-11 02:00
 */
public class ListLocationSamplesByGeoJsonPolygonServlet extends HttpServlet {

  private Logger log = LoggerFactory.getLogger(getClass());

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    response.setHeader("Access-Control-Allow-Origin", "*");

    StringBuilder documentation = new StringBuilder(1024);
    documentation.append("This service accept UTF-8 encoded HTTP post.\n");
    documentation.append("The server will return an UTF-8 encoded JSON object with search results based on the JSON request.\n");
    documentation.append("\n");
    documentation.append("JSON request:\n");
    documentation.append("{\n");
    documentation.append("\n");
    documentation.append("  \"reference\": Optional. Any value. Will be sent back to client in response.\n");
    documentation.append("  \"polygon\": GeoJSON polygon.\n");
    documentation.append("\n");
    documentation.append("}\n");

    documentation.append("\n");
    documentation.append("\n");
    documentation.append("JSON response:\n");
    documentation.append("{\n");
    documentation.append("\n");
    documentation.append("  \"success\": Boolean value.\n");
    documentation.append("\n");
    documentation.append("  \"reference\": Same as request value.\n");
    documentation.append("\n");
    documentation.append("  \"totalNumberOfMatches\": Integer value. Number of hits yielded by query. This might be greater than searchResults.length in case of limit was supplied in request.\n");
    documentation.append("  \"locationSamples\": [ {\n");
    documentation.append("      \"identity\": LocationSample identity\n");
    documentation.append("\n");
    documentation.append("      \"tags\": [ OSM style tags, all string values \n");
    documentation.append("        \"addr:postcode\": String value. E.g. '12345'\n");
    documentation.append("        \"addr:city\": String value. E.g. 'Stockholm'\n");
    documentation.append("        \"addr:street\": String value. E.g. 'Drottningatan'\n");
    documentation.append("        \"addr:housenumber\": String value. E.g '12'\n");
    documentation.append("        \"addr:housename\": String value. E.g. 'A'\n");
    documentation.append("      ]\n");
    documentation.append("\n");
    documentation.append("      \"coordinate\": {\n");
    documentation.append("        \"provider\": String value. Source of location, e.g. 'gps', 'network', 'human', etc.\n");
    documentation.append("        \"accuracy\": Double value. Maximum error in meters.\n");
    documentation.append("        \"latitude\": Double value. EPSG:3857\n");
    documentation.append("        \"longitude\": Double value. EPSG:3857\n");
    documentation.append("        \"altitude\": Double value. Meters altitude above sea.\"\n");
    documentation.append("      }\n");
    documentation.append("\n");
    documentation.append("  } ]\n");
    documentation.append("\n");
    documentation.append("}\n");

    response.setContentType("text/plain");
    response.setCharacterEncoding("UTF-8");
    response.getOutputStream().write(documentation.toString().getBytes("UTF-8"));


  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setHeader("Access-Control-Allow-Origin", "*");

    try {
      JSONObject requestJSON = new JSONObject(new JSONTokener(IOUtils.toString(request.getInputStream(), "UTF-8")));
      log.debug("Incoming request: " + requestJSON.toString());

      Polygon geoJsonPolygon = GeoJSONParser.parsePolygon(requestJSON.getJSONObject("polygon"));

      GeometryFactory geometryFactory = new GeometryFactory();

      Coordinate[] shellCoordinates = new Coordinate[geoJsonPolygon.getHull().size()];
      for (int i = 0; i < shellCoordinates.length; i++) {
        shellCoordinates[i] = new Coordinate(
            geoJsonPolygon.getHull().get(i).getLongitude(),
            geoJsonPolygon.getHull().get(i).getLatitude()
        );
      }
      LinearRing shell = geometryFactory.createLinearRing(shellCoordinates);

      LinearRing[] holes = null;
      if (geoJsonPolygon.getHoles() != null && !geoJsonPolygon.getHoles().isEmpty()) {
        holes = new LinearRing[geoJsonPolygon.getHoles().size()];
        for (int holesIndex = 0; holesIndex < holes.length; holesIndex++) {
          Coordinate[] holeCoordindates = new Coordinate[geoJsonPolygon.getHoles().get(holesIndex).size()];
          for (int i = 0; i < geoJsonPolygon.getHoles().get(holesIndex).size(); i++) {
            holeCoordindates[i] = new Coordinate(
                geoJsonPolygon.getHoles().get(holesIndex).get(i).getLongitude(),
                geoJsonPolygon.getHoles().get(holesIndex).get(i).getLatitude()
            );
          }
          holes[holesIndex] = geometryFactory.createLinearRing(holeCoordindates);
        }
      }

      com.vividsolutions.jts.geom.Polygon jtsPolygon = geometryFactory.createPolygon(shell, holes);

      Collection<LocationSample> locationSamples = Insamlingsappen.getInstance().getPrevayler().execute(new FindSamplesByJtsGeometry(geometryFactory, jtsPolygon));

      JSONObject responseJSON = new JSONObject();

      responseJSON.put("success", true);
      responseJSON.put("reference", requestJSON.opt("reference"));

      JSONArray locationSamplesJSON = new JSONArray();
      responseJSON.put("locationSamples", locationSamplesJSON);

      for (LocationSample locationSample : locationSamples) {
        JSONObject locationSampleJSON = new JSONObject();
        locationSamplesJSON.put(locationSampleJSON);

        locationSampleJSON.put("identity", locationSample.getIdentity());


        JSONArray tagsJSON = new JSONArray();
        locationSampleJSON.put("tags", tagsJSON);

        for (Map.Entry<String, String> tag : locationSample.getTags().entrySet()) {
          JSONObject tagJSON = new JSONObject();
          tagJSON.put(tag.getKey(), tag.getValue());
          tagsJSON.put(tagJSON);
        }

        if (locationSample.getCoordinate() != null) {
          JSONObject coordinateJSON = new JSONObject();
          locationSampleJSON.put("coordinate", coordinateJSON);

          coordinateJSON.put("latitude", locationSample.getCoordinate().getLatitude());
          coordinateJSON.put("longitude", locationSample.getCoordinate().getLongitude());
          coordinateJSON.put("accuracy", locationSample.getCoordinate().getAccuracy());
          coordinateJSON.put("altitude", locationSample.getCoordinate().getAltitude());
        }

      }

      response.getOutputStream().write(responseJSON.toString().getBytes("UTF-8"));

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
