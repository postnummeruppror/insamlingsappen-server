package nu.postnummeruppror.insamlingsappen.index;

import org.apache.lucene.search.*;

/**
 * @author kalle
 * @since 2014-09-08 14:32
 */
public class LocationSampleCoordinateEnvelopeQueryFactory {

  private Double south;
  private Double west;
  private Double north;
  private Double east;

  public LocationSampleCoordinateEnvelopeQueryFactory setSouth(Double south) {
    this.south = south;
    return this;
  }

  public LocationSampleCoordinateEnvelopeQueryFactory setWest(Double west) {
    this.west = west;
    return this;
  }

  public LocationSampleCoordinateEnvelopeQueryFactory setNorth(Double north) {
    this.north = north;
    return this;
  }

  public LocationSampleCoordinateEnvelopeQueryFactory setEast(Double east) {
    this.east = east;
    return this;
  }

  public Double getSouth() {
    return south;
  }

  public Double getWest() {
    return west;
  }

  public Double getNorth() {
    return north;
  }

  public Double getEast() {
    return east;
  }

  public Query build() {
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
