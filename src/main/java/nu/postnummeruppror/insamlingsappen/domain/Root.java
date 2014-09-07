package nu.postnummeruppror.insamlingsappen.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author kalle
 * @since 2014-09-06 20:16
 */
public class Root implements Serializable {

  private static final long serialVersionUID = 1l;

  private AtomicLong identityFactory = new AtomicLong();

  private Map<String, Account> accounts = new HashMap<>();
  private Map<Long, LocationSample> locationSamples = new HashMap<>();
  private Map<String, PostalCode> postalCodes = new HashMap<>();

  private Intern<String> providerIntern = new Intern<>();
  private Intern<String> streetNameIntern = new Intern<>();
  private Intern<String> houseNumberIntern = new Intern<>();

  public Map<String, Account> getAccounts() {
    return accounts;
  }

  public void setAccounts(Map<String, Account> accounts) {
    this.accounts = accounts;
  }

  public AtomicLong getIdentityFactory() {
    return identityFactory;
  }

  public void setIdentityFactory(AtomicLong identityFactory) {
    this.identityFactory = identityFactory;
  }

  public Map<Long, LocationSample> getLocationSamples() {
    return locationSamples;
  }

  public void setLocationSamples(Map<Long, LocationSample> locationSamples) {
    this.locationSamples = locationSamples;
  }

  public Map<String, PostalCode> getPostalCodes() {
    return postalCodes;
  }

  public void setPostalCodes(Map<String, PostalCode> postalCodes) {
    this.postalCodes = postalCodes;
  }

  public Intern<String> getProviderIntern() {
    return providerIntern;
  }

  public void setProviderIntern(Intern<String> providerIntern) {
    this.providerIntern = providerIntern;
  }


  public Intern<String> getStreetNameIntern() {
    return streetNameIntern;
  }

  public void setStreetNameIntern(Intern<String> streetNameIntern) {
    this.streetNameIntern = streetNameIntern;
  }

  public Intern<String> getHouseNumberIntern() {
    return houseNumberIntern;
  }

  public void setHouseNumberIntern(Intern<String> houseNumberIntern) {
    this.houseNumberIntern = houseNumberIntern;
  }
}
