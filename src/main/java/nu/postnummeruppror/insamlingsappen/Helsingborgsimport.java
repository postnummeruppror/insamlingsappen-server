package nu.postnummeruppror.insamlingsappen;

import nu.postnummeruppror.insamlingsappen.domain.Coordinate;
import nu.postnummeruppror.insamlingsappen.transactions.IdentityFactory;
import nu.postnummeruppror.insamlingsappen.transactions.version_0_0_6.CreateLocationSample;
import se.kodapan.osm.domain.Node;
import se.kodapan.osm.domain.root.PojoRoot;
import se.kodapan.osm.parser.xml.instantiated.InstantiatedOsmXmlParser;

/**
 * @author kalle
 * @since 2017-01-30 18:58
 */
public class Helsingborgsimport {

  public static void main(String[] args) throws Exception {

    Insamlingsappen.getInstance().open();
    try {


      PojoRoot pojoRoot = new PojoRoot();
      InstantiatedOsmXmlParser parser = InstantiatedOsmXmlParser.newInstance();
      parser.setRoot(pojoRoot);

      parser.parse(Helsingborgsimport.class.getResourceAsStream("/helsingborg adresspunkter.osm.xml"));

      int counter = 0;

      for (Node node : pojoRoot.getNodes().values()) {
        CreateLocationSample createLocationSample = new CreateLocationSample();

        createLocationSample.setLocationSampleIdentity(Insamlingsappen.getInstance().getPrevayler().execute(new IdentityFactory()));
        createLocationSample.setAccountIdentity("Helsingborgimport version 0.0.1");

        createLocationSample.setApplication("Helsingborgimport");
        createLocationSample.setApplicationVersion("0.0.1");

        createLocationSample.setCoordinate(new Coordinate());
        createLocationSample.getCoordinate().setLatitude(node.getLatitude());
        createLocationSample.getCoordinate().setLongitude(node.getLongitude());
        createLocationSample.getCoordinate().setAccuracy(1d);
        createLocationSample.getCoordinate().setProvider("geojson converted");
        String postcode = node.getTag("addr:postcode");
        if (postcode != null) {
          postcode = postcode.replaceAll("\\s", "");
          if (postcode.matches("[0-9]{5}")) {
            createLocationSample.getTags().put("addr:postcode", postcode);
          }
        }
        String city = node.getTag("addr:city");
        if (city != null) {
          createLocationSample.getTags().put("addr:city", city);
        }
        if (!createLocationSample.getTags().isEmpty()) {
          System.currentTimeMillis();
          counter++;

          Insamlingsappen.getInstance().getPrevayler().execute(createLocationSample);

        } else {
          System.currentTimeMillis();
        }
      }

      System.currentTimeMillis();
    } finally {
       Insamlingsappen.getInstance().close();
    }
  }
}