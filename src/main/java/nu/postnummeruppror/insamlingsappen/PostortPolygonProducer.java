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
public class PostortPolygonProducer {

  private GeometryFactory geometryFactory;

  private MultiPolygon borderMultiPolygon;

  public PostortPolygonProducer(GeometryFactory geometryFactory, MultiPolygon borderMultiPolygon) {
    this.geometryFactory = geometryFactory;
    this.borderMultiPolygon = borderMultiPolygon;
  }

  private long timestampFrom = Long.MIN_VALUE;
  private long timestampTo = Long.MAX_VALUE;

  private double maximumAccuracy = 1000;

  public Map<String, List<Polygon>> execute() throws Exception {

    Map<String, Set<LocationSample>> gatheredPerNormalizedPostalTown = new HashMap<>(2000);

    for (LocationSample locationSample : Insamlingsappen.getInstance().getPrevayler().prevalentSystem().getLocationSamples().values()) {
      if (locationSample.getCoordinate() != null
          && locationSample.getCoordinate().getLatitude() != null
          && locationSample.getCoordinate().getLongitude() != null
          && !"true".equalsIgnoreCase(locationSample.getTag("deprecated"))
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

    AdjacentClassVoronoiClusterer<String> voronoiClusterer = new AdjacentClassVoronoiClusterer<>(geometryFactory);
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

}
