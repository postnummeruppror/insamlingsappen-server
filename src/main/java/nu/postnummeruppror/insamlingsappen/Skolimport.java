package nu.postnummeruppror.insamlingsappen;

import nu.postnummeruppror.insamlingsappen.domain.Coordinate;
import nu.postnummeruppror.insamlingsappen.transactions.IdentityFactory;
import nu.postnummeruppror.insamlingsappen.transactions.version_0_0_6.CreateLocationSample;
import se.kodapan.osm.domain.Node;
import se.kodapan.osm.domain.root.PojoRoot;
import se.kodapan.osm.parser.xml.instantiated.InstantiatedOsmXmlParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author kalle
 * @since 2014-09-27 12:19
 */
public class Skolimport {

  private static Pattern streetAddressPattern = Pattern.compile("\\s*(.+)(\\s*([0-9]+)\\s*([A-Za-z]?))?\\s*$");

  public static void main(String[] args) throws Exception {

    Insamlingsappen.getInstance().open();
    try {


      PojoRoot pojoRoot = new PojoRoot();
      InstantiatedOsmXmlParser parser = InstantiatedOsmXmlParser.newInstance();
      parser.setRoot(pojoRoot);

      parser.parse(Skolimport.class.getResourceAsStream("/alla-skolor-cc0.osm.xml"));

      for (Node node : pojoRoot.getNodes().values()) {
        CreateLocationSample createLocationSample = new CreateLocationSample();

        createLocationSample.setLocationSampleIdentity(Insamlingsappen.getInstance().getPrevayler().execute(new IdentityFactory()));
        createLocationSample.setAccountIdentity("Skolimport version 0.0.1");

        createLocationSample.setApplication("Skolimport");
        createLocationSample.setApplicationVersion("0.0.1");

        createLocationSample.setCoordinate(new Coordinate());
        createLocationSample.getCoordinate().setLatitude(node.getLatitude());
        createLocationSample.getCoordinate().setLongitude(node.getLongitude());
        createLocationSample.getCoordinate().setAccuracy(1d);
        createLocationSample.getCoordinate().setProvider("shapefile");

        createLocationSample.getTags().put("amenity", "school");

        String verkform = node.getTag("VERKFORM");
        if (verkform != null) {
          String level = null;
          if ("Förskoleklass".equals(verkform)) {
            createLocationSample.getTags().put("isced:level", "0");
          } else if ("Gymnasieskola".equals(verkform)) {
            createLocationSample.getTags().put("isced:level", "3");
          }
          createLocationSample.getTags().put("ref:se:skola:verkform", verkform);
        }


        String skolenkod = node.getTag("SKOLENKOD");
        if (skolenkod != null) {
          int id = Double.valueOf(skolenkod.trim()).intValue();
          createLocationSample.getTags().put("ref:se:skola:skolenhetskod", String.valueOf(id));
        }


        String hmannamn = node.getTag("HMANNAMN");
        if (hmannamn != null) {
          createLocationSample.getTags().put("operator", hmannamn.trim());
        }


        String streetAddress = node.getTag("BADRESS");
        if (streetAddress != null) {

          Matcher matcher = streetAddressPattern.matcher(streetAddress);
          if (matcher.matches()) {
            createLocationSample.getTags().put("addr:street", matcher.group(1).trim());
            if (matcher.group(2) != null) {
              createLocationSample.getTags().put("addr:housenumber", matcher.group(2).trim());
            }
            if (matcher.group(3) != null) {
              createLocationSample.getTags().put("addr:housename", matcher.group(3).trim());
            }
          }

        }

        String name = node.getTag("SNAMN");
        if (name != null) {
          createLocationSample.getTags().put("name", name.trim());
        }

        String postalTown = node.getTag("BPOSTORT");
        if (postalTown != null) {
          createLocationSample.getTags().put("addr:city", postalTown.trim());
        }

        String postalCode = node.getTag("BPOSTNR");
        if (postalCode != null) {
          createLocationSample.getTags().put("addr:postcode", String.valueOf(Double.valueOf(postalCode.trim()).intValue()));
        }

        String lan = node.getTag("LAN");
        if (lan != null) {
          createLocationSample.getTags().put("ref:se:län", lan.trim());
        }

        String kommun = node.getTag("SKOM");
        if (kommun != null) {
          createLocationSample.getTags().put("ref:se:kommun", kommun.trim());
        }


        Insamlingsappen.getInstance().getPrevayler().execute(createLocationSample);
      }

    } finally {
      Insamlingsappen.getInstance().close();
    }

  }

}
