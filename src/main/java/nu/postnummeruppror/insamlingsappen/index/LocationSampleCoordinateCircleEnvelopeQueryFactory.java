package nu.postnummeruppror.insamlingsappen.index;

import se.kodapan.lucene.query.CoordinateCircleEnvelopeQueryFactory;

/**
 * @author kalle
 * @since 2014-09-08 14:32
 */
public class LocationSampleCoordinateCircleEnvelopeQueryFactory extends CoordinateCircleEnvelopeQueryFactory {

  public LocationSampleCoordinateCircleEnvelopeQueryFactory() {
    setLatitudeField(LocationSampleIndexFields.latitude);
    setLongitudeField(LocationSampleIndexFields.longitude);
  }
}
