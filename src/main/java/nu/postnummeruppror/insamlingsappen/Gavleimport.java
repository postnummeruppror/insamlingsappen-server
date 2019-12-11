package nu.postnummeruppror.insamlingsappen;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import nu.postnummeruppror.insamlingsappen.domain.Account;
import nu.postnummeruppror.insamlingsappen.domain.Coordinate;
import nu.postnummeruppror.insamlingsappen.domain.LocationSample;
import nu.postnummeruppror.insamlingsappen.transactions.DeprecateLocationSample;
import nu.postnummeruppror.insamlingsappen.transactions.IdentityFactory;
import nu.postnummeruppror.insamlingsappen.transactions.version_0_0_6.CreateLocationSample;
import se.kodapan.osm.domain.Node;
import se.kodapan.osm.domain.root.PojoRoot;
import se.kodapan.osm.parser.xml.instantiated.InstantiatedOsmXmlParser;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Based on the Helsingborgsimport class
 * @author kalle
 * @since 2017-01-30 18:58
 */
public class Gavleimport {

  public static void main(String[] args) throws Exception {

    Insamlingsappen.getInstance().open();
    try {


      // 1. deprecate old data
      // 1.1 within the hull of the imported data
      // 1.2 without coordinate but with addr:city set to a postort that only exists within the hull
      // todo 1.3 without coordinate but with addr:postcode set to a postal code that only exists within the hull

      GeometryFactory geometryFactory = new GeometryFactory();


      // This was valid 2019-12-11. Might not be valid when you read it or run it.

      Set<LocationSample> deprecationSamples = new HashSet<>();

      MultiPolygon hullPolygon = new Sweden(geometryFactory).getMultiPolygon("name", "Gävle kommun");

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
                "Overridden by import of official data from Gävle kommun"
            )
        );
      }


      // 2. import data


      PojoRoot dataRoot = new PojoRoot();
      {
        InstantiatedOsmXmlParser parser = InstantiatedOsmXmlParser.newInstance();
        parser.setRoot(dataRoot);
        parser.parse(Gavleimport.class.getResourceAsStream("/gavle adresspunkter.osm.xml"));
      }

      String accountIdentity = "Gävleimport " + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());

      for (Node node : dataRoot.getNodes().values()) {
        CreateLocationSample createLocationSample = new CreateLocationSample();

        createLocationSample.setLocationSampleIdentity(Insamlingsappen.getInstance().getPrevayler().execute(new IdentityFactory()));
        createLocationSample.setAccountIdentity(accountIdentity);

        createLocationSample.setApplication("Gävleimport");
        createLocationSample.setApplicationVersion("0.0.1");

        createLocationSample.setCoordinate(new Coordinate());
        // we dont use the node coordinate, it has been converted from SWEREF and is somewhat wrong on CM level.
        createLocationSample.getCoordinate().setLatitude(Double.valueOf(node.getTag("WGS84_LAT")));
        createLocationSample.getCoordinate().setLongitude(Double.valueOf(node.getTag("WGS84_LONG")));
        createLocationSample.getCoordinate().setAccuracy(1d);
        createLocationSample.getCoordinate().setProvider("Supplied from Gävle kommun");
        String postcode = node.getTag("POSTNR");
        if (postcode != null) {
          postcode = postcode.replaceAll("\\s", "");
          if (postcode.matches("[0-9]{5}")) {
            createLocationSample.getTags().put("addr:postcode", postcode);
          }
        }
        String city = node.getTag("POSTORT");
        if (city != null) {
          createLocationSample.getTags().put("addr:city", city);
        }
        String street = node.getTag("GATUADRESS");
        if (street != null) {
          createLocationSample.getTags().put("addr:full", street.trim());
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