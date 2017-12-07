package nu.postnummeruppror.insamlingsappen;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.precision.GeometryPrecisionReducer;
import nu.postnummeruppror.insamlingsappen.domain.LocationSample;
import nu.postnummeruppror.insamlingsappen.index.LocationSampleIndexFields;
import se.kodapan.lucene.query.CoordinateCircleEnvelopeQueryFactory;
import se.kodapan.osm.jts.voronoi.AdjacentClassVoronoiClusterer;

import java.util.*;

/**
 * @author kalle
 * @since 2017-12-07
 */
public class PostnummerPolygonProducer {

  private long timestampFrom = Long.MIN_VALUE;
  private long timestampTo = Long.MAX_VALUE;

  private double maximumAccuracy = 1000;

  private int postalCodeLength = 5;

  private GeometryFactory geometryFactory;

  private MultiPolygon borderMultiPolygon;

  public PostnummerPolygonProducer(GeometryFactory geometryFactory, MultiPolygon borderMultiPolygon) {
    this.geometryFactory = geometryFactory;
    this.borderMultiPolygon = borderMultiPolygon;
  }

  public Map<String, List<Polygon>> execute() throws Exception {

    Map<String, Set<LocationSample>> gatheredPerPostalCode = new HashMap<>(2000);

    for (LocationSample locationSample : Insamlingsappen.getInstance().getPrevayler().prevalentSystem().getLocationSamples().values()) {
      if (locationSample.getCoordinate() != null
          && locationSample.getCoordinate().getLatitude() != null
          && locationSample.getCoordinate().getLongitude() != null
          && locationSample.getTag("addr:postcode") != null
          && locationSample.getCoordinate().getAccuracy() != null
          && locationSample.getTimestamp() >= timestampFrom
          && locationSample.getTimestamp() <= timestampTo) {

        String postalCode = locationSample.getTag("addr:postcode").replaceAll("\\s+", "");
        if (!postalCode.matches("[0-9]{5}")) {
          continue;
        }

        postalCode = postalCode.substring(0, postalCodeLength);

        Set<LocationSample> perPostalCode = gatheredPerPostalCode.get(postalCode);
        if (perPostalCode == null) {
          perPostalCode = new HashSet<>(4096);
          gatheredPerPostalCode.put(postalCode, perPostalCode);
        }
        perPostalCode.add(locationSample);

      }
    }

    AdjacentClassVoronoiClusterer<String> voronoiClusterer = new AdjacentClassVoronoiClusterer<>(geometryFactory);
    voronoiClusterer.setNumberOfThreads(1);

    for (Map.Entry<String, Set<LocationSample>> entry : gatheredPerPostalCode.entrySet()) {

      String postalCode = entry.getKey();
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


        voronoiClusterer.addCoordinate(postalCode, locationSample.getCoordinate().getLongitude(), locationSample.getCoordinate().getLatitude());


      }

    }


    GeometryPrecisionReducer precisionReducer = new GeometryPrecisionReducer(new PrecisionModel(PrecisionModel.maximumPreciseValue));


    Map<String, List<Polygon>> voronoiClusters = voronoiClusterer.build();

    for (Map.Entry<String, List<Polygon>> entry : voronoiClusters.entrySet()) {

      List<Polygon> polygons = new ArrayList<>();

      for (Polygon polygon : entry.getValue()) {
        Geometry geometry = precisionReducer.reduce(polygon).intersection(precisionReducer.reduce(borderMultiPolygon));
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

  public int getPostalCodeLength() {
    return postalCodeLength;
  }

  public void setPostalCodeLength(int postalCodeLength) {
    this.postalCodeLength = postalCodeLength;
  }
}
