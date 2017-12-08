package nu.postnummeruppror.insamlingsappen;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import org.apache.commons.io.IOUtils;
import se.kodapan.osm.domain.*;
import se.kodapan.osm.domain.root.PojoRoot;
import se.kodapan.osm.jts.voronoi.GeoJSONVoronoiFactory;
import se.kodapan.osm.parser.xml.instantiated.InstantiatedOsmXmlParser;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author kalle
 * @since 2014-09-20 22:32
 */
public class PostnummerPolygonService {

  public static void main(String[] args) throws Exception {

    Insamlingsappen.getInstance().open();
    try {

      DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

      PostnummerPolygonService service = new PostnummerPolygonService();

//      service.animate(df.parse("2014-09-14 23:59").getTime(), System.currentTimeMillis() + 1000 * 60 * 60 * 24, 1000 * 60 * 60 * 24);
      service.animate(df.parse("2017-01-30 23:59").getTime(), System.currentTimeMillis() + 1000 * 60 * 60 * 24, TimeUnit.DAYS.toMillis(30));

    } finally {
      Insamlingsappen.getInstance().close();
    }


  }

  private GeometryFactory geometryFactory = new GeometryFactory();
  private Sweden sweden;

  public PostnummerPolygonService() throws Exception {
    sweden = new Sweden(geometryFactory);
  }

  public void animate(long timestampFrom, long timestampTo, long interval) throws Exception {

    String template = IOUtils.toString(new FileInputStream("src/main/webapp/map/postnummer/animation-template.html"), "UTF8");

    setTimestampFrom(Long.MIN_VALUE);

    DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH:mm");

    for (int postalCodeLength = 1; postalCodeLength <= 5; postalCodeLength++) {

      setPostalCodeLength(postalCodeLength);

      for (long timestamp = timestampFrom; timestamp <= timestampTo; timestamp += interval) {

        setTimestampTo(timestamp);

        Map<String, List<Polygon>> voronoiClusters = factory();

        StringWriter geojson = new StringWriter(49152);
        GeoJSONVoronoiFactory<String> geojsonFactory = new GeoJSONVoronoiFactory<>();
        geojsonFactory.factory(voronoiClusters);
        geojsonFactory.getRoot().writeJSON(geojson);

        String html = template.replaceFirst("\\$\\{geojson\\}", geojson.toString());

        Writer writer = new OutputStreamWriter(new FileOutputStream("src/main/webapp/map/postnummer/" + this.postalCodeLength + "/" + df.format(new Date(timestamp)) + ".html"), "UTF8");
        writer.write(html);
        writer.close();
      }
    }


  }

  private long timestampFrom = Long.MIN_VALUE;
  private long timestampTo = Long.MAX_VALUE;

  private double maximumAccuracy = 1000;

  private int postalCodeLength = 5;

  public Map<String, List<Polygon>> factory() throws Exception {
    PostnummerPolygonProducer producer = new PostnummerPolygonProducer(geometryFactory, sweden.getSwedenMultiPolygon());
    producer.setTimestampFrom(timestampFrom);
    producer.setTimestampTo(timestampTo);
    producer.setMaximumAccuracy(maximumAccuracy);
    producer.setPostalCodeLength(postalCodeLength);
    return producer.execute();
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
