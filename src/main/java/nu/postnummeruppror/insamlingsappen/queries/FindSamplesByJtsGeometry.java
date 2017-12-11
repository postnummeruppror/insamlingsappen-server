package nu.postnummeruppror.insamlingsappen.queries;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import nu.postnummeruppror.insamlingsappen.domain.LocationSample;
import nu.postnummeruppror.insamlingsappen.domain.Root;
import org.prevayler.Query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Really only useful while not that many location samples in store
 *
 * @author kalle
 * @since 2017-12-09 15:00
 */
public class FindSamplesByJtsGeometry implements Query<Root, Collection<LocationSample>> {

  private GeometryFactory geometryFactory;
  private Geometry geometry;

  public FindSamplesByJtsGeometry(GeometryFactory geometryFactory, Geometry geometry) {
    this.geometryFactory = geometryFactory;
    this.geometry = geometry;
  }

  @Override
  public Collection<LocationSample> query(Root root, Date date) throws Exception {
    List<LocationSample> response = new ArrayList<>();
    for (LocationSample locationSample : root.getLocationSamples().values()) {
      if (locationSample.getCoordinate() != null
          && locationSample.getCoordinate().getLongitude() != null
          && locationSample.getCoordinate().getLatitude() != null) {
        if (geometry.contains(geometryFactory.createPoint(
            new Coordinate(
                locationSample.getCoordinate().getLongitude(),
                locationSample.getCoordinate().getLatitude()
            )
        ))) {
          response.add(locationSample);
        }
      }
    }
    return response;
  }
}
