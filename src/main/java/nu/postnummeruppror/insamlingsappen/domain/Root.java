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

  private Map<Long, Comment> comments = new HashMap<>();

  private Intern<String> applicationIntern = new Intern<>();
  private Intern<String> applicationVersionIntern = new Intern<>();
  private Intern<String> providerIntern = new Intern<>();
  private Intern<String> streetNameIntern = new Intern<>();
  private Intern<String> houseNumberIntern = new Intern<>();
  private Intern<String> houseNameIntern = new Intern<>();
  private Intern<String> postalCodeIntern = new Intern<>();
  private Intern<String> postalTownIntern = new Intern<>();

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

  public Intern<String> getPostalCodeIntern() {
    return postalCodeIntern;
  }

  public void setPostalCodeIntern(Intern<String> postalCodeIntern) {
    this.postalCodeIntern = postalCodeIntern;
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

  public Intern<String> getPostalTownIntern() {
    return postalTownIntern;
  }

  public void setPostalTownIntern(Intern<String> postalTownIntern) {
    this.postalTownIntern = postalTownIntern;
  }

  public Intern<String> getApplicationIntern() {
    return applicationIntern;
  }

  public void setApplicationIntern(Intern<String> applicationIntern) {
    this.applicationIntern = applicationIntern;
  }

  public Intern<String> getApplicationVersionIntern() {
    return applicationVersionIntern;
  }

  public void setApplicationVersionIntern(Intern<String> applicationVersionIntern) {
    this.applicationVersionIntern = applicationVersionIntern;
  }

  public Intern<String> getHouseNameIntern() {
    return houseNameIntern;
  }

  public void setHouseNameIntern(Intern<String> houseNameIntern) {
    this.houseNameIntern = houseNameIntern;
  }

  public Map<Long, Comment> getComments() {
    return comments;
  }

  public void setComments(Map<Long, Comment> comments) {
    this.comments = comments;
  }
}
