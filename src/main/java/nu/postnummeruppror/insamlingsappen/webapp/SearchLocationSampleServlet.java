package nu.postnummeruppror.insamlingsappen.webapp;

import nu.postnummeruppror.insamlingsappen.Insamlingsappen;
import nu.postnummeruppror.insamlingsappen.domain.LocationSample;
import nu.postnummeruppror.insamlingsappen.index.LocationSampleIndexFields;
import org.apache.lucene.search.*;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author kalle
 * @since 2014-09-07 03:48
 */
public class SearchLocationSampleServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    try {

      Query coordinateEnvelopeQuery = coordinateEnvelopeQueryFactory(
          Double.valueOf(request.getParameter("south")),
          Double.valueOf(request.getParameter("west")),
          Double.valueOf(request.getParameter("north")),
          Double.valueOf(request.getParameter("east"))
      );

      int maximumHits = Integer.valueOf(request.getParameter("maximumHits"));

      JSONObject json = new JSONObject();

      Map<LocationSample, Float> hits = Insamlingsappen.getInstance().getLocationSampleIndex().search(coordinateEnvelopeQuery);

      json.put("success", true);
      json.put("reference", request.getParameter("reference"));
      json.put("numberOfHits", hits.size());

      JSONArray hitsJson = new JSONArray();
      json.put("hits", hitsJson);

      for (LocationSample hit : hits.keySet()) {
        JSONObject hitJson = new JSONObject();
        hitJson.put("identity", hit.getIdentity());
        hitJson.put("postalCode", hit.getPostalCode().getPostalCode());
        hitJson.put("latitude", hit.getLatitude());
        hitJson.put("longitude", hit.getLongitude());

        hitsJson.put(hitJson);

        if (hitsJson.length() >= maximumHits) {
          break;
        }
      }

      response.setCharacterEncoding("UTF-8");
      response.setContentType("application/json");
      response.getOutputStream().write(json.toString().getBytes("UTF-8"));

    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }


  public Query coordinateEnvelopeQueryFactory(double south, double west, double north, double east) {
    if (south <= -90d
        && west <= -180d
        && north >= 90d
        && east >= 180d) {
      return new MatchAllDocsQuery();
    }

    BooleanQuery query = new BooleanQuery();

    query.add(NumericRangeQuery.newDoubleRange(LocationSampleIndexFields.latitude, south, north, true, true), BooleanClause.Occur.MUST);

    if (west < east) {

      query.add(NumericRangeQuery.newDoubleRange(LocationSampleIndexFields.longitude, west, east, true, true), BooleanClause.Occur.MUST);

    } else {

      BooleanQuery longitudeQuery = new BooleanQuery();

      longitudeQuery.add(NumericRangeQuery.newDoubleRange(LocationSampleIndexFields.longitude, -180d, west, true, true), BooleanClause.Occur.SHOULD);
      longitudeQuery.add(NumericRangeQuery.newDoubleRange(LocationSampleIndexFields.longitude, east, 180d, true, true), BooleanClause.Occur.SHOULD);

      query.add(longitudeQuery, BooleanClause.Occur.MUST);


    }

    return query;

  }
}
