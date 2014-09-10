package nu.postnummeruppror.insamlingsappen.webapp.version_0_0_4;

import nu.postnummeruppror.insamlingsappen.Insamlingsappen;
import nu.postnummeruppror.insamlingsappen.domain.LocationSample;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kodapan.lucene.query.JsonQueryUnmarshaller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @author kalle
 * @since 2014-09-10 18:11
 */
public class SearchLocationSampleServlet extends HttpServlet {

  private static final Logger log = LoggerFactory.getLogger(SearchLocationSampleServlet.class);

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    StringBuilder documentation = new StringBuilder(1024);
    documentation.append("This service accept UTF-8 encoded HTTP post.\n");
    documentation.append("The server will return an UTF-8 encoded JSON object with search results based on the JSON request.\n");
    documentation.append("\n");
    documentation.append("JSON request:\n");
    documentation.append("{\n");
    documentation.append("\n");
    documentation.append("  \"reference\": Optional. Any value. Will be sent back to client in response.\n");
    documentation.append("  \"startIndex\": Optional. Integer value. Zero-base index of first search result in response.\n");
    documentation.append("  \"maximumHits\": Optional. Integer value. Maximum number of search results.\n");
    documentation.append("\n");
    documentation.append("  \"query\": {\n");
    documentation.append("   }\n");
    documentation.append("\n");
    documentation.append("}\n");

    documentation.append("\n");
    documentation.append("\n");
    documentation.append("JSON response:\n");
    documentation.append("{\n");
    documentation.append("\n");
    documentation.append("  \"success\": Boolean value.\n");
    documentation.append("\n");
    documentation.append("  \"numberOfHits\": Integer value. Number of hits yielded by query. This might be greater than searchResults.length\n");
    documentation.append("  \"searchResults\": [ {\n");
    documentation.append("      \"index\": Integer value. Position in search results, ordered by score.\n");
    documentation.append("      \"score\": Float value. Similarity between query and result\n");
    documentation.append("\n");
    documentation.append("      \"identity\": LocationSample identity\n");
    documentation.append("\n");
    documentation.append("      \"postalCode\": String value. E.g. '12345'\n");
    documentation.append("      \"postalTown\": String value. E.g. 'Stockholm'\n");
    documentation.append("      \"streetName\": String value. E.g. 'Drottningatan'\n");
    documentation.append("      \"houseNumber\": String value. E.g '12'\n");
    documentation.append("      \"houseName\": String value. E.g. 'A'\n");
    documentation.append("\n");
    documentation.append("      \"provider\": String value. Source of location, e.g. 'gps', 'network', 'human', etc.\n");
    documentation.append("      \"accuracy\": Double value. Maximum error in meters.\n");
    documentation.append("      \"latitude\": Double value. EPSG:3857\n");
    documentation.append("      \"longitude\": Double value. EPSG:3857\n");
    documentation.append("      \"altitude\": Double value. Meters altitude above sea.\"\n");
    documentation.append("  } ]\n");
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

      Map<LocationSample, Float> searchResults = Insamlingsappen.getInstance().getLocationSampleIndex().search(
          new JsonQueryUnmarshaller().parseJsonQuery(requestJSON.getJSONObject("query")));

      List<Map.Entry<LocationSample, Float>> orderedSearchResults = new ArrayList<>(searchResults.entrySet());
      Collections.sort(orderedSearchResults, new Comparator<Map.Entry<LocationSample, Float>>() {
        @Override
        public int compare(Map.Entry<LocationSample, Float> o1, Map.Entry<LocationSample, Float> o2) {
          return o1.getValue().compareTo(o2.getValue());
        }
      });

      Object reference = requestJSON.has("reference") ? requestJSON.get("reference") : null;
      int maximumHits = requestJSON.has("maximumHits") ? requestJSON.getInt("maximumHits") : Integer.MAX_VALUE;
      int startIndex = requestJSON.has("startIndex") ? requestJSON.getInt("startIndex") : 0;

      JSONObject responseJSON = new JSONObject();

      responseJSON.put("success", true);
      responseJSON.put("reference", reference);
      responseJSON.put("numberOfHits", searchResults.size());

      JSONArray searchResultsJSON = new JSONArray();
      responseJSON.put("searchResults", searchResultsJSON);

      for (int index = startIndex; index < maximumHits && index < orderedSearchResults.size(); index++) {
        JSONObject searchResultJSON = new JSONObject();

        Map.Entry<LocationSample, Float> searchResult = orderedSearchResults.get(index);

        LocationSample locationSample = searchResult.getKey();

        searchResultJSON.put("index", index);
        searchResultJSON.put("score", searchResult.getValue());

        searchResultJSON.put("identity", locationSample.getIdentity());

        searchResultJSON.put("postalCode", locationSample.getPostalCode().getPostalCode());
        searchResultJSON.put("postalTown", locationSample.getPostalTown());
        searchResultJSON.put("streetName", locationSample.getStreetName());
        searchResultJSON.put("houseNumber", locationSample.getHouseNumber());
        searchResultJSON.put("houseName", locationSample.getHouseName());

        searchResultJSON.put("latitude", locationSample.getLatitude());
        searchResultJSON.put("longitude", locationSample.getLongitude());
        searchResultJSON.put("accuracy", locationSample.getAccuracy());
        searchResultJSON.put("altitude", locationSample.getAltitude());

        searchResultsJSON.put(searchResultJSON);

      }

      response.setCharacterEncoding("UTF-8");
      response.setContentType("application/json");
      response.getOutputStream().write(responseJSON.toString().getBytes("UTF-8"));

    } catch (Exception e) {

      throw new RuntimeException(e);

    }


  }






}
