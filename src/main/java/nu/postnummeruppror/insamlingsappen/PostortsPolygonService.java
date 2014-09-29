package nu.postnummeruppror.insamlingsappen;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.precision.GeometryPrecisionReducer;
import nu.postnummeruppror.insamlingsappen.domain.LocationSample;
import nu.postnummeruppror.insamlingsappen.index.LocationSampleIndexFields;
import org.apache.commons.io.IOUtils;
import se.kodapan.lucene.query.CoordinateCircleEnvelopeQueryFactory;
import se.kodapan.osm.domain.*;
import se.kodapan.osm.domain.root.PojoRoot;
import se.kodapan.osm.jts.voronoi.AdjacentClassVoronoiClusterer;
import se.kodapan.osm.jts.voronoi.GeoJSONVoronoiFactory;
import se.kodapan.osm.parser.xml.instantiated.InstantiatedOsmXmlParser;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author kalle
 * @since 2014-09-20 22:32
 */
public class PostortsPolygonService {

  public static void main(String[] args) throws Exception {

    Insamlingsappen.getInstance().open();
    try {

      DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

      new PostortsPolygonService().animate(df.parse("2014-09-14 23:59").getTime(), System.currentTimeMillis() + 1000 * 60 * 60 * 24, 1000 * 60 * 60 * 24);

    } finally {
      Insamlingsappen.getInstance().close();
    }


  }

  private PojoRoot sweden;
  private List<LinearRing> swedenLinearRings;
  private MultiPolygon swedenMultipolygon;


  private GeometryFactory geometryFactory = new GeometryFactory();

  public PostortsPolygonService() throws Exception {

    sweden = new PojoRoot();
    InstantiatedOsmXmlParser parser = InstantiatedOsmXmlParser.newInstance();
    parser.setRoot(sweden);
    parser.parse(getClass().getResourceAsStream("/sverige-odbl.osm.xml"));

    swedenLinearRings = new ArrayList<>();

    Relation rootRelation = sweden.getRelation(52822);



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

  }


  public void animate(long timestampFrom, long timestampTo, long interval) throws Exception {

    String template = IOUtils.toString(new FileInputStream("src/main/webapp/map/postorter/animation-template.html"), "UTF8");

    setTimestampFrom(Long.MIN_VALUE);

    DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH:mm");

    for (long timestamp = timestampFrom; timestamp <= timestampTo; timestamp += interval) {

      setTimestampTo(timestamp);

      Map<String, List<Polygon>> voronoiClusters = factory();

      StringWriter geojson = new StringWriter(49152);


      GeoJSONVoronoiFactory<String> geojsonFactory = new GeoJSONVoronoiFactory<>();
      geojsonFactory.factory(voronoiClusters);
      geojsonFactory.getRoot().writeJSON(geojson);

      String html = template.replaceFirst("\\$\\{geojson\\}", geojson.toString());

      Writer writer = new OutputStreamWriter(new FileOutputStream("src/main/webapp/map/postorter/" + df.format(new Date(timestamp)) + ".html"), "UTF8");
      writer.write(html);
      writer.close();
    }


  }


  private long timestampFrom = Long.MIN_VALUE;
  private long timestampTo = Long.MAX_VALUE;

  private double maximumAccuracy = 1000;

  public Map<String, List<Polygon>> factory() throws Exception {

    Map<String, Set<LocationSample>> gatheredPerNormalizedPostalTown = new HashMap<>(2000);

    for (LocationSample locationSample : Insamlingsappen.getInstance().getPrevayler().prevalentSystem().getLocationSamples().values()) {
      if (locationSample.getCoordinate() != null
          && locationSample.getCoordinate().getLatitude() != null
          && locationSample.getCoordinate().getLongitude() != null
          && locationSample.getTag("addr:city") != null
          && locationSample.getCoordinate().getAccuracy() != null
          && locationSample.getTimestamp() >= timestampFrom
          && locationSample.getTimestamp() <= timestampTo) {

        String postalTown = locationSample.getTag("addr:city").trim().toUpperCase();
        if (postalTown.isEmpty()) {
          continue;
        }

        Set<LocationSample> perNormalizedPostalTown = gatheredPerNormalizedPostalTown.get(postalTown);
        if (perNormalizedPostalTown == null) {
          perNormalizedPostalTown = new HashSet<>(4096);
          gatheredPerNormalizedPostalTown.put(postalTown, perNormalizedPostalTown);
        }
        perNormalizedPostalTown.add(locationSample);

      }
    }

    AdjacentClassVoronoiClusterer<String> voronoiClusterer = new AdjacentClassVoronoiClusterer<>(new GeometryFactory());
    voronoiClusterer.setNumberOfThreads(1);

    for (Map.Entry<String, Set<LocationSample>> entry : gatheredPerNormalizedPostalTown.entrySet()) {

      String postalTown = entry.getKey();
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


        voronoiClusterer.addCoordinate(postalTown, locationSample.getCoordinate().getLongitude(), locationSample.getCoordinate().getLatitude());


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

    return voronoiClusters;
  }

  public long getTimestampFrom() {
    return timestampFrom;
  }

  public void setTimestampFrom(long timestampFrom) {
    this.timestampFrom = timestampFrom;
  }

  public long getTimestampTo() {
    return timestampTo;
  }

  public void setTimestampTo(long timestampTo) {
    this.timestampTo = timestampTo;
  }

  public double getMaximumAccuracy() {
    return maximumAccuracy;
  }

  public void setMaximumAccuracy(double maximumAccuracy) {
    this.maximumAccuracy = maximumAccuracy;
  }
}
