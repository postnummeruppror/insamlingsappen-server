package nu.postnummeruppror.insamlingsappen.index;

import se.kodapan.lucene.query.CoordinateEnvelopeQueryFactory;

/**
 * @author kalle
 * @since 2014-09-08 14:32
 */
public class LocationSampleCoordinateEnvelopeQueryFactory extends CoordinateEnvelopeQueryFactory {

  public LocationSampleCoordinateEnvelopeQueryFactory() {
    setLatitudeField(LocationSampleIndexFields.latitude);
    setLongitudeField(LocationSampleIndexFields.longitude);
  }
}
