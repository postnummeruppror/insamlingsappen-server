package nu.postnummeruppror.insamlingsappen.webapp.version_0_0_6;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.precision.GeometryPrecisionReducer;
import nu.postnummeruppror.insamlingsappen.Insamlingsappen;
import nu.postnummeruppror.insamlingsappen.Sweden;
import nu.postnummeruppror.insamlingsappen.domain.LocationSample;
import nu.postnummeruppror.insamlingsappen.index.LocationSampleIndexFields;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kodapan.lucene.query.CoordinateCircleEnvelopeQueryFactory;
import se.kodapan.lucene.query.JSONQueryUnmarshaller;
import se.kodapan.osm.domain.*;
import se.kodapan.osm.domain.root.PojoRoot;
import se.kodapan.osm.jts.voronoi.AdjacentClassVoronoiClusterer;
import se.kodapan.osm.jts.voronoi.GeoJSONVoronoiFactory;
import se.kodapan.osm.parser.xml.instantiated.InstantiatedOsmXmlParser;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @author kalle
 * @since 2014-09-25 17:24
 */
public class TagVoronoiServlet extends HttpServlet {

  private static final Logger log = LoggerFactory.getLogger(TagVoronoiServlet.class);


  private Sweden sweden;
  private GeometryFactory geometryFactory;

  @Override
  public void init() throws ServletException {

    try {

      geometryFactory = new GeometryFactory();
      sweden = new Sweden(geometryFactory);

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }


  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    response.setHeader("Access-Control-Allow-Origin", "*");

    try {

      JSONObject requestJSON = new JSONObject(new JSONTokener(IOUtils.toString(request.getInputStream(), "UTF-8")));

      log.debug("Incoming request: " + requestJSON.toString());

      String tag = requestJSON.getString("tag");

      Set<LocationSample> locationSamples = Insamlingsappen.getInstance().getLocationSampleIndex().search(
          new JSONQueryUnmarshaller().parseJsonQuery(requestJSON.getJSONObject("query")), false).keySet();

      long timestampFrom = Long.MIN_VALUE;
      long timestampTo = Long.MAX_VALUE;
      long maximumAccuracy = 999;

      Map<String, Set<LocationSample>> samplesByClassValue = new HashMap<>(2000);

      for (LocationSample locationSample : locationSamples) {
        if (locationSample.getCoordinate() != null
            && locationSample.getCoordinate().getLatitude() != null
            && locationSample.getCoordinate().getLongitude() != null
            && locationSample.getTag(tag) != null
            && !"true".equalsIgnoreCase(locationSample.getTag("deprecated"))
            && locationSample.getCoordinate().getAccuracy() != null
            && locationSample.getTimestamp() >= timestampFrom
            && locationSample.getTimestamp() <= timestampTo) {

          String tagValue = locationSample.getTag(tag);
          tagValue = tagValue.replaceAll("-", " ");
          tagValue = tagValue.replaceAll("\\s+", " ");
          tagValue = tagValue.toUpperCase();
          tagValue = tagValue.trim();

          if (tagValue.isEmpty()) {
            continue;
          }

          Set<LocationSample> perNormalizedTagValue = samplesByClassValue.get(tagValue);
          if (perNormalizedTagValue == null) {
            perNormalizedTagValue = new HashSet<>(4096);
            samplesByClassValue.put(tagValue, perNormalizedTagValue);
          }
          perNormalizedTagValue.add(locationSample);

        }
      }

      AdjacentClassVoronoiClusterer<String> voronoiClusterer = new AdjacentClassVoronoiClusterer<>(new GeometryFactory());
      voronoiClusterer.setNumberOfThreads(1);

      for (Map.Entry<String, Set<LocationSample>> entry : samplesByClassValue.entrySet()) {

        String tagValue = entry.getKey();
        for (LocationSample locationSample : entry.getValue()) {

          if (locationSample.getCoordinate().getAccuracy() > maximumAccuracy) {

            // allow if this is the only sample in the accuracy area

            Map<LocationSample, Float> searchResults = Insamlingsappen.getInstance().getLocationSampleIndex().search(
                new CoordinateCircleEnvelopeQueryFactory()
                    .setCentroidLatitude(locationSample.getCoordinate().getLatitude())
                    .setCentroidLongitude(locationSample.getCoordinate().getLongitude())
                    .setRadiusKilometers(locationSample.getCoordinate().getAccuracy() / 1000d)
                    .setLatitudeField(LocationSampleIndexFields.latitude)
                    .setLongitudeField(LocationSampleIndexFields.longitude)
                    .build()
            );

            if (searchResults.size() > 1) {
              continue;
            } else {
              System.currentTimeMillis();
            }

          }


          voronoiClusterer.addCoordinate(tagValue, locationSample.getCoordinate().getLongitude(), locationSample.getCoordinate().getLatitude());


        }

      }

      GeometryPrecisionReducer foo = new GeometryPrecisionReducer(new PrecisionModel(PrecisionModel.maximumPreciseValue));


      Map<String, List<Polygon>> voronoiClusters = voronoiClusterer.build();

      for (Map.Entry<String, List<Polygon>> entry : voronoiClusters.entrySet()) {

        List<Polygon> polygons = new ArrayList<>();

        for (Polygon polygon : entry.getValue()) {
          Geometry geometry = foo.reduce(polygon).intersection(foo.reduce(sweden.getSwedenMultiPolygon()));
          if (geometry instanceof Polygon) {
            polygons.add((Polygon) geometry);
          } else if (geometry instanceof GeometryCollection) {
            GeometryCollection geometryCollection = (GeometryCollection) geometry;
            for (int i = 0; i < geometryCollection.getNumGeometries(); i++) {
              polygons.add((Polygon) geometryCollection.getGeometryN(i));
            }
          } else {
            throw new RuntimeException(geometry.getClass().getName());
          }
        }

        entry.getValue().clear();
        entry.getValue().addAll(polygons);

        System.currentTimeMillis();
      }

      GeoJSONVoronoiFactory<String> geoJSONVoronoiFactory = new GeoJSONVoronoiFactory<>();
      geoJSONVoronoiFactory.factory(voronoiClusters);

      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");

      JSONObject responseJSON = new JSONObject();

      if (requestJSON.has("reference")) {
        responseJSON.put("reference", requestJSON.get("reference"));
      }

      responseJSON.put("voronoi", new JSONObject(new JSONTokener(geoJSONVoronoiFactory.getRoot().toJSON())));

      response.getWriter().write(responseJSON.toString());

    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }


}
