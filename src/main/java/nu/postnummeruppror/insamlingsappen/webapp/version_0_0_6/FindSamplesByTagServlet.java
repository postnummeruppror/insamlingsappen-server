package nu.postnummeruppror.insamlingsappen.webapp.version_0_0_6;

import nu.postnummeruppror.insamlingsappen.Insamlingsappen;
import nu.postnummeruppror.insamlingsappen.domain.LocationSample;
import nu.postnummeruppror.insamlingsappen.queries.FindSamplesByTag;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author kalle
 * @since 2017-12-12
 */
public class FindSamplesByTagServlet extends HttpServlet {

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
    documentation.append("  \"mustTags\": { JSON object. Required tags. All must match. Case sensitive regexp\n");
    documentation.append("    \"addr:city\": \"Place\"\n");
    documentation.append("    \"addr:postcode\": \"12345\"\n");
    documentation.append("  },\n");
    documentation.append("  \"mustNotTags\": { JSON object. Tags that mut not match. All must match. Case sensitive regexp\n");
    documentation.append("    \"deprecated\": \"true\"\n");
    documentation.append("    \"key\": \"nor this\"\n");
    documentation.append("  }\n");
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

      FindSamplesByTag findSamplesByTag = new FindSamplesByTag();
      if (requestJSON.has("mustTags")) {
        JSONObject mustTags = requestJSON.getJSONObject("mustTags");
        for (Iterator iterator = mustTags.keys(); iterator.hasNext(); ) {
          String key = (String) iterator.next();
          String value = mustTags.getString(key);
          findSamplesByTag.getMustTags().put(key, Pattern.compile(value));
        }
      }

      if (requestJSON.has("mustNotTags")) {
        JSONObject mustNotTags = requestJSON.getJSONObject("mustNotTags");
        for (Iterator iterator = mustNotTags.keys(); iterator.hasNext(); ) {
          String key = (String) iterator.next();
          String value = mustNotTags.getString(key);
          findSamplesByTag.getMustNotTags().put(key, Pattern.compile(value));
        }
      }


      Collection<LocationSample> locationSamples = Insamlingsappen.getInstance().getPrevayler().execute(findSamplesByTag);

      JSONObject responseJSON = new JSONObject();

      responseJSON.put("success", true);
      responseJSON.put("reference", requestJSON.opt("reference"));

      JSONArray locationSamplesJSON = new JSONArray();
      responseJSON.put("locationSamples", locationSamplesJSON);

      for (LocationSample locationSample : locationSamples) {
        JSONObject locationSampleJSON = new JSONObject();
        locationSamplesJSON.put(locationSampleJSON);

        locationSampleJSON.put("identity", locationSample.getIdentity());


        JSONObject tagsJSON = new JSONObject();
        locationSampleJSON.put("tags", tagsJSON);

        for (Map.Entry<String, String> tag : locationSample.getTags().entrySet()) {
          tagsJSON.put(tag.getKey(), tag.getValue());
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
