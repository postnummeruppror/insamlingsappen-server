package nu.postnummeruppror.insamlingsappen.webapp.version_0_0_6;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.precision.GeometryPrecisionReducer;
import nu.postnummeruppror.insamlingsappen.Insamlingsappen;
import nu.postnummeruppror.insamlingsappen.domain.LocationSample;
import nu.postnummeruppror.insamlingsappen.index.LocationSampleIndexFields;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kodapan.lucene.query.CoordinateCircleEnvelopeQueryFactory;
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


  private MultiPolygon swedenMultipolygon;
  private GeometryFactory geometryFactory;

  @Override
  public void init() throws ServletException {

    try {

      geometryFactory = new GeometryFactory(new PrecisionModel(PrecisionModel.maximumPreciseValue));

      PojoRoot sweden = new PojoRoot();
      InstantiatedOsmXmlParser parser = InstantiatedOsmXmlParser.newInstance();
      parser.setRoot(sweden);
      parser.parse(getClass().getResourceAsStream("/sverige-natural-earth-cc0.osm.xml"));

      List<LinearRing> swedenLinearRings = new ArrayList<>();

      Relation rootRelation = sweden.getRelation(-524915);


      List<Node> nodes = new ArrayList<>();
      Node firstNode = null;
      for (RelationMembership membership : rootRelation.getMembers()) {

        if (!"outer".equalsIgnoreCase(membership.getRole())) {
          continue;
        }

        if (firstNode == null) {
          firstNode = membership.getObject().accept(new OsmObjectVisitor<Node>() {
            @Override
            public Node visit(Node node) {
              return node;
            }

            @Override
            public Node visit(Way way) {
              return way.getNodes().get(0);
            }

            @Override
            public Node visit(Relation relation) {
              return relation.accept(this);
            }
          });
        }

        nodes.addAll(membership.getObject().accept(new OsmObjectVisitor<List<Node>>() {
          @Override
          public List<Node> visit(Node node) {
            ArrayList<Node> nodes = new ArrayList<>(1);
            nodes.add(node);
            return nodes;
          }

          @Override
          public List<Node> visit(Way way) {
            return way.getNodes();
          }

          @Override
          public List<Node> visit(Relation relation) {
            List<Node> nodes = new ArrayList<>();
            for (RelationMembership membership : relation.getMembers()) {
              nodes.addAll(membership.getObject().accept(this));
            }
            return nodes;
          }
        }));

        if (nodes.get(nodes.size() - 1).equals(firstNode)) {
          Coordinate[] coordinates = new Coordinate[nodes.size() + 1];
          for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            coordinates[i] = new Coordinate(node.getX(), node.getY());
          }
          coordinates[coordinates.length - 1] = coordinates[0];
          swedenLinearRings.add(new LinearRing(new CoordinateArraySequence(coordinates), geometryFactory));
          firstNode = null;
          nodes.clear();
        }
      }

      Polygon[] polygons = new Polygon[swedenLinearRings.size()];
      for (int i = 0; i < swedenLinearRings.size(); i++) {
        polygons[i] = new Polygon(swedenLinearRings.get(i), null, geometryFactory);
      }
      swedenMultipolygon = new MultiPolygon(polygons, geometryFactory);

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    try {

      contructVoronoi(response, request.getParameter("tag"));

    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    try {

      JSONObject requestJSON = new JSONObject(new JSONTokener(IOUtils.toString(request.getInputStream(), "UTF-8")));

      log.debug("Incoming request: " + requestJSON.toString());

      contructVoronoi(response, requestJSON.getString("tag"));


    } catch (Exception e) {
      throw new RuntimeException(e);
    }


  }

  private void contructVoronoi(HttpServletResponse response, String tag) throws Exception {

    long timestampFrom = Long.MIN_VALUE;
    long timestampTo = Long.MAX_VALUE;
    long maximumAccuracy = 999;

    Map<String, Set<LocationSample>> gatheredPerNormalizedPostalTown = new HashMap<>(2000);

    for (LocationSample locationSample : Insamlingsappen.getInstance().getPrevayler().prevalentSystem().getLocationSamples().values()) {
      if (locationSample.getCoordinate() != null
          && locationSample.getCoordinate().getLatitude() != null
          && locationSample.getCoordinate().getLongitude() != null
          && locationSample.getTag(tag) != null
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

        Set<LocationSample> perNormalizedTagValue = gatheredPerNormalizedPostalTown.get(tagValue);
        if (perNormalizedTagValue == null) {
          perNormalizedTagValue = new HashSet<>(4096);
          gatheredPerNormalizedPostalTown.put(tagValue, perNormalizedTagValue);
        }
        perNormalizedTagValue.add(locationSample);

      }
    }

    AdjacentClassVoronoiClusterer<String> voronoiClusterer = new AdjacentClassVoronoiClusterer<>(new GeometryFactory());
    voronoiClusterer.setNumberOfThreads(1);

    for (Map.Entry<String, Set<LocationSample>> entry : gatheredPerNormalizedPostalTown.entrySet()) {

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
        Geometry geometry = foo.reduce(polygon).intersection(foo.reduce(swedenMultipolygon));
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
    geoJSONVoronoiFactory.getRoot().writeJSON(response.getWriter());
  }
}
