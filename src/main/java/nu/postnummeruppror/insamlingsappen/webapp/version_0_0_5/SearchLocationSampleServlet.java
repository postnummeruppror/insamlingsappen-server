package nu.postnummeruppror.insamlingsappen.webapp.version_0_0_5;

import nu.postnummeruppror.insamlingsappen.Insamlingsappen;
import nu.postnummeruppror.insamlingsappen.domain.LocationSample;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kodapan.lucene.query.JSONQueryUnmarshaller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author kalle
 * @since 2014-09-10 18:11
 */
public class SearchLocationSampleServlet extends HttpServlet {

  private static final Logger log = LoggerFactory.getLogger(SearchLocationSampleServlet.class);

  private Map<String, ResponseFormatSerializer> responseFormatSerializers = new HashMap<>();

  private abstract class ResponseFormatSerializer {
    public abstract String getContentType();

    public abstract String getCharacterEncoding();

    public abstract void writeOutput(OutputStream output, List<Map.Entry<LocationSample, Float>> searchResults,
                                     Object reference, int startIndex, int limit, boolean score) throws Exception;

  }

  @Override
  public void init() throws ServletException {
    super.init();

    responseFormatSerializers.put("json", new JSONResponseFormatSerializer());
    responseFormatSerializers.put("osm.xml", new OSMXMLResponseFormatSerializer());
  }

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
    documentation.append("  \"score\": Optional. Boolean value. If search results is to be scored.\n");
    documentation.append("  \"startIndex\": Optional. Integer value. Zero-base index of first search result in response.\n");
    documentation.append("  \"limit\": Optional. Integer value. Maximum number of search results.\n");
    documentation.append("  \"format\": Optional. String value. Response format. 'json' or'osm-xml'.\n");
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
    documentation.append("  \"totalNumberOfMatches\": Integer value. Number of hits yielded by query. This might be greater than searchResults.length in case of limit was supplied in request.\n");
    documentation.append("  \"searchResults\": [ {\n");
    documentation.append("      \"index\": Integer value. Position in search results, ordered by score.\n");
    documentation.append("      \"score\": Float value. Similarity between query and result\n");
    documentation.append("\n");
    documentation.append("      \"identity\": LocationSample identity\n");
    documentation.append("\n");
    documentation.append("      \"postalAddress\": { \n");
    documentation.append("        \"postalCode\": String value. E.g. '12345'\n");
    documentation.append("        \"postalTown\": String value. E.g. 'Stockholm'\n");
    documentation.append("        \"streetName\": String value. E.g. 'Drottningatan'\n");
    documentation.append("        \"houseNumber\": String value. E.g '12'\n");
    documentation.append("        \"houseName\": String value. E.g. 'A'\n");
    documentation.append("      }\n");
    documentation.append("\n");
    documentation.append("      \"name\": String value. Generic POI location name. e.g. 'A och B livsmedelsbutik'\n");
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

    try {

      JSONObject requestJSON = new JSONObject(new JSONTokener(IOUtils.toString(request.getInputStream(), "UTF-8")));

      log.debug("Incoming request: " + requestJSON.toString());

      boolean score = requestJSON.has("score") && requestJSON.getBoolean("score");

      Map<LocationSample, Float> searchResults = Insamlingsappen.getInstance().getLocationSampleIndex().search(
          new JSONQueryUnmarshaller().parseJsonQuery(requestJSON.getJSONObject("query")), score);

      List<Map.Entry<LocationSample, Float>> orderedSearchResults = new ArrayList<>(searchResults.entrySet());


      if (score) {
        Collections.sort(orderedSearchResults, new Comparator<Map.Entry<LocationSample, Float>>() {
          @Override
          public int compare(Map.Entry<LocationSample, Float> o1, Map.Entry<LocationSample, Float> o2) {
            return o1.getValue().compareTo(o2.getValue());
          }
        });
      } else {
        Collections.sort(orderedSearchResults, new Comparator<Map.Entry<LocationSample, Float>>() {
          @Override
          public int compare(Map.Entry<LocationSample, Float> o1, Map.Entry<LocationSample, Float> o2) {
            return o2.getKey().getIdentity().compareTo(o1.getKey().getIdentity());
          }
        });
      }

      Object reference = requestJSON.has("reference") ? requestJSON.get("reference") : null;
      int limit = requestJSON.has("limit") ? requestJSON.getInt("limit") : Integer.MAX_VALUE;
      int startIndex = requestJSON.has("startIndex") ? requestJSON.getInt("startIndex") : 0;

      String format;
      if (requestJSON.has("format")) {
        format = requestJSON.getString("format");
      } else {
        format = "json";
      }

      ResponseFormatSerializer responseFormatSerializer = responseFormatSerializers.get(format);

      response.setCharacterEncoding(responseFormatSerializer.getCharacterEncoding());
      response.setContentType(responseFormatSerializer.getContentType());

      responseFormatSerializer.writeOutput(response.getOutputStream(), orderedSearchResults, reference, startIndex, limit, score);

    } catch (Exception e) {

      throw new RuntimeException(e);

    }


  }

  private class JSONResponseFormatSerializer extends ResponseFormatSerializer {
    @Override
    public String getContentType() {
      return "application/json";
    }

    @Override
    public String getCharacterEncoding() {
      return "UTF-8";
    }

    @Override
    public void writeOutput(OutputStream output, List<Map.Entry<LocationSample, Float>> searchResults, Object reference, int startIndex, int limit, boolean score) throws Exception {

      JSONObject responseJSON = new JSONObject();

      responseJSON.put("success", true);
      responseJSON.put("reference", reference);
      responseJSON.put("totalNumberOfMatches", searchResults.size());

      JSONArray searchResultsJSON = new JSONArray();
      responseJSON.put("searchResults", searchResultsJSON);

      for (int index = startIndex; index < startIndex + limit && index < searchResults.size(); index++) {
        JSONObject searchResultJSON = new JSONObject();

        Map.Entry<LocationSample, Float> searchResult = searchResults.get(index);

        LocationSample locationSample = searchResult.getKey();

        searchResultJSON.put("index", index);
        if (score) {
          searchResultJSON.put("score", searchResult.getValue());
        }

        searchResultJSON.put("identity", locationSample.getIdentity());


        JSONObject postalAddressJSON = new JSONObject();
        searchResultJSON.put("postalAddress", postalAddressJSON);

        postalAddressJSON.put("postalCode", locationSample.getTag("addr:postcode"));
        postalAddressJSON.put("postalTown", locationSample.getTag("addr:city"));
        postalAddressJSON.put("streetName", locationSample.getTag("addr:street"));
        postalAddressJSON.put("houseNumber", locationSample.getTag("addr:housenumber"));
        postalAddressJSON.put("houseName", locationSample.getTag("addr:housename"));


        searchResultJSON.put("name", locationSample.getTag("name"));

        if (locationSample.getCoordinate() != null) {

          JSONObject coordinateJSON = new JSONObject();
          searchResultJSON.put("coordinate", coordinateJSON);

          coordinateJSON.put("latitude", locationSample.getCoordinate().getLatitude());
          coordinateJSON.put("longitude", locationSample.getCoordinate().getLongitude());
          coordinateJSON.put("accuracy", locationSample.getCoordinate().getAccuracy());
          coordinateJSON.put("altitude", locationSample.getCoordinate().getAltitude());

        }

        searchResultsJSON.put(searchResultJSON);

      }

      output.write(responseJSON.toString().getBytes("UTF-8"));

    }
  }

  private class OSMXMLResponseFormatSerializer extends ResponseFormatSerializer {

    @Override
    public String getContentType() {
      return "text/xml;charset=" + getCharacterEncoding();
    }

    @Override
    public String getCharacterEncoding() {
      return "UTF-8";
    }

    @Override
    public void writeOutput(OutputStream output, List<Map.Entry<LocationSample, Float>> searchResults, Object reference, int startIndex, int limit, boolean score) throws Exception {

      OutputStreamWriter xml = new OutputStreamWriter(output, "UTF-8");
      try {


        xml.write("<?xml version='1.0' encoding='");
        xml.write(getCharacterEncoding());
        xml.write("'?>\n");

        xml.write("<osm version='");
        xml.write("0.6");
        xml.write("' upload='");
        xml.write("true");
        xml.write("'");
        xml.write(" generator='");
        xml.write(getClass().getName());
        xml.write("'>\n");

        int id = 0;
        DecimalFormat df = new DecimalFormat("#.##################################");

        for (int index = startIndex; index < startIndex + limit && index < searchResults.size(); index++) {

          LocationSample locationSample = searchResults.get(index).getKey();

          xml.write("\t<node ");
          xml.write(" id='");
          xml.write(String.valueOf(--id));
          xml.write("'");

          xml.write(" lat='");
          xml.write(df.format(locationSample.getCoordinate().getLatitude()));
          xml.write("'");

          xml.write(" lon='");
          xml.write(df.format(locationSample.getCoordinate().getLongitude()));
          xml.write("'");

          xml.write(" >\n");

          xml.write("\t\t<tag k='");
          xml.write("source");
          xml.write("' v='");
          xml.write("postnummeruppror.nu");
          xml.write("' />\n");

          xml.write("\t\t<tag k='");
          xml.write("postnummeruppror.nu:location_sample:id");
          xml.write("' v='");
          xml.write(String.valueOf(locationSample.getIdentity()));
          xml.write("' />\n");

          for (Map.Entry<String, String> tag : locationSample.getTags().entrySet())  {
            xml.write("\t\t<tag k='");
            xml.write(StringEscapeUtils.escapeXml(tag.getKey()));
            xml.write("' v='");
            xml.write(StringEscapeUtils.escapeXml(tag.getValue()));
            xml.write("' />\n");
          }

          if (locationSample.getCoordinate().getProvider() != null) {
            xml.write("\t\t<tag k='");
            xml.write("position:provider");
            xml.write("' v='");
            xml.write(StringEscapeUtils.escapeXml(locationSample.getCoordinate().getProvider()));
            xml.write("' />\n");
          }

          if (locationSample.getCoordinate().getAccuracy() != null) {
            xml.write("\t\t<tag k='");
            xml.write("position:accuracy");
            xml.write("' v='");
            xml.write(StringEscapeUtils.escapeXml(df.format(locationSample.getCoordinate().getAccuracy())));
            xml.write("' />\n");
          }

          if (locationSample.getCoordinate().getAltitude() != null) {
            xml.write("\t\t<tag k='");
            xml.write("position:altitude");
            xml.write("' v='");
            xml.write(StringEscapeUtils.escapeXml(df.format(locationSample.getCoordinate().getAltitude())));
            xml.write("' />\n");
          }


          xml.write("\t</node>\n");

        }

        xml.write("</osm>\n");


      } finally {
        xml.flush();
      }

    }
  }

}
