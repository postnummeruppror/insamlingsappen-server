package nu.postnummeruppror.insamlingsappen;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import nu.postnummeruppror.insamlingsappen.domain.Account;
import nu.postnummeruppror.insamlingsappen.domain.Coordinate;
import nu.postnummeruppror.insamlingsappen.domain.LocationSample;
import nu.postnummeruppror.insamlingsappen.transactions.DeprecateLocationSample;
import nu.postnummeruppror.insamlingsappen.transactions.IdentityFactory;
import nu.postnummeruppror.insamlingsappen.transactions.version_0_0_6.CreateLocationSample;
import se.kodapan.osm.domain.Node;
import se.kodapan.osm.domain.Way;
import se.kodapan.osm.domain.root.PojoRoot;
import se.kodapan.osm.parser.xml.instantiated.InstantiatedOsmXmlParser;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author kalle
 * @since 2017-01-30 18:58
 */
public class Helsingborgsimport {

  public static void main(String[] args) throws Exception {

    Insamlingsappen.getInstance().open();
    try {


      // 1. deprecate old data
      // 1.1 within the hull of the imported data
      // 1.2 without coordinate but with addr:city set to a postort that only exists within the hull
      // todo 1.3 without coordinate but with addr:postcode set to a postal code that only exists within the hull

      GeometryFactory geometryFactory = new GeometryFactory();


      // This was valid 2017-12-06. Might not be valid when you read it or run it.

      Set<String> postorterOnlyInHelsingborgKommun = new HashSet<>();
      postorterOnlyInHelsingborgKommun.add("GANTOFTA");
      postorterOnlyInHelsingborgKommun.add("ÖDÅKRA");
      postorterOnlyInHelsingborgKommun.add("MÖRARP");
      postorterOnlyInHelsingborgKommun.add("FLENINGE");
      postorterOnlyInHelsingborgKommun.add("PÅARP");
      postorterOnlyInHelsingborgKommun.add("KATTARP");
      postorterOnlyInHelsingborgKommun.add("HASSLARP");
      postorterOnlyInHelsingborgKommun.add("ALLERUM");
      postorterOnlyInHelsingborgKommun.add("ÖDÅKRA-VÄLA");
      postorterOnlyInHelsingborgKommun.add("RÅÅ");
      postorterOnlyInHelsingborgKommun.add("RYDEBÄCK");
      postorterOnlyInHelsingborgKommun.add("VALLÅKRA");
      postorterOnlyInHelsingborgKommun.add("DOMSTEN");
      postorterOnlyInHelsingborgKommun.add("HELSINGBORG");
      postorterOnlyInHelsingborgKommun.add("RAMLÖSA");



      Set<LocationSample> deprecationSamples = new HashSet<>();

      MultiPolygon hullPolygon = new Sweden(geometryFactory).getMultiPolygon("name", "Helsingborgs kommun");

      for (LocationSample sample : Insamlingsappen.getInstance().getPrevayler().prevalentSystem().getLocationSamples().values()) {

        if (!"true".equalsIgnoreCase(sample.getTag("deprecated"))) {

          if (sample.getCoordinate() != null
              && sample.getCoordinate().getLatitude() != null
              && sample.getCoordinate().getLongitude() != null) {

            if (hullPolygon.contains(geometryFactory.createPoint(
                new com.vividsolutions.jts.geom.Coordinate(
                    sample.getCoordinate().getLongitude(),
                    sample.getCoordinate().getLatitude()
                )))) {
              deprecationSamples.add(sample);
            }
          } else {
            String addrCity = sample.getTag("addr:city");
            if (addrCity != null
                && postorterOnlyInHelsingborgKommun.contains(addrCity.toUpperCase().trim())) {
              deprecationSamples.add(sample);
            }
//          String postnummer = sample.getTag("addr:postcode");
//          if (postnummer != null
//              && postnummerOnlyInHelsingborgKommun.contains(postnummer.trim())) {
//            deprecationSamples.add(sample);
//          }

          }
        }
      }

      Map<String, List<LocationSample>> samplesByEmail = new HashMap<>();
      Map<String, Set<Account>> accountsByEmail = new HashMap<>();
      for (LocationSample sample : deprecationSamples) {
        if (sample.getAccount().getEmailAddress() != null) {
          String normalizedEmailAddress = sample.getAccount().getEmailAddress().toLowerCase();
          Set<Account> accounts = accountsByEmail.get(normalizedEmailAddress);
          if (accounts == null) {
            accounts = new HashSet<>();
            accountsByEmail.put(normalizedEmailAddress, accounts);
          }
          accounts.add(sample.getAccount());
          List<LocationSample> emailSamples = samplesByEmail.get(normalizedEmailAddress);
          if (emailSamples == null) {
            emailSamples = new ArrayList<>();
            samplesByEmail.put(normalizedEmailAddress, emailSamples);
          }
          emailSamples.add(sample);
        }
      }

      for (LocationSample sample : deprecationSamples) {
        Insamlingsappen.getInstance().getPrevayler().execute(
            new DeprecateLocationSample(
                sample.getIdentity(),
                "Overridden by import of official data from Helsingborg kommun"
            )
        );
      }


      // 2. import helsingborgdata


      PojoRoot dataRoot = new PojoRoot();
      {
        InstantiatedOsmXmlParser parser = InstantiatedOsmXmlParser.newInstance();
        parser.setRoot(dataRoot);
        parser.parse(Helsingborgsimport.class.getResourceAsStream("/helsingborg adresspunkter.osm.xml"));
      }

      String accountIdentity = "Helsingborgimport " + new SimpleDateFormat("yyyy-MM-dd HH:mm");

      for (Node node : dataRoot.getNodes().values()) {
        CreateLocationSample createLocationSample = new CreateLocationSample();

        createLocationSample.setLocationSampleIdentity(Insamlingsappen.getInstance().getPrevayler().execute(new IdentityFactory()));
        createLocationSample.setAccountIdentity(accountIdentity);

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
        String street = node.getTag("addr:street");
        if (street != null) {
          createLocationSample.getTags().put("addr:street", street.trim());
        }
        String housenumber = node.getTag("addr:housenumber");
        if (housenumber != null) {
          createLocationSample.getTags().put("addr:housenumber", housenumber.trim());
        }
        if (!createLocationSample.getTags().isEmpty()) {
          Insamlingsappen.getInstance().getPrevayler().execute(createLocationSample);
        } else {
          System.currentTimeMillis();
        }
      }

      // 3. produce emails to holders of accounts and let them know their reports have been deprecated
      for (Map.Entry<String, List<LocationSample>> entry : samplesByEmail.entrySet()) {
        System.out.println(entry.getKey() + "\t" + entry.getValue().size());
      }

      System.currentTimeMillis();
    } finally {
      Insamlingsappen.getInstance().close();
    }
  }
}