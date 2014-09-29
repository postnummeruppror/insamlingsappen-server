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

  private Intern<String> applicationIntern = new Intern<>();
  private Intern<String> applicationVersionIntern = new Intern<>();
  private Intern<String> providerIntern = new Intern<>();

  private TagsIntern tagsIntern = new TagsIntern();

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


  public Intern<String> getProviderIntern() {
    return providerIntern;
  }

  public void setProviderIntern(Intern<String> providerIntern) {
    this.providerIntern = providerIntern;
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


  public TagsIntern getTagsIntern() {
    return tagsIntern;
  }

  public void setTagsIntern(TagsIntern tagsIntern) {
    this.tagsIntern = tagsIntern;
  }
}
