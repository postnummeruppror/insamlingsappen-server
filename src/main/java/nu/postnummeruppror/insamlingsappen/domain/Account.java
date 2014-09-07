package nu.postnummeruppror.insamlingsappen.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kalle
 * @since 2014-09-06 00:45
 */
public class Account implements Serializable {

  private static final long serialVersionUID = 1l;

  /** Random UUID */
  private String identity;

  private long timestampCreated;

  private String emailAddress;
  private Long timestampVerifiedEmailAddress;

  private String firstName;
  private String lastName;

  private List<LocationSample> locationSamples = new ArrayList<>();

  @Override
  public String toString() {
    return "Account{" +
        "identity='" + identity + '\'' +
        ", timestampCreated=" + timestampCreated +
        ", emailAddress='" + emailAddress + '\'' +
        ", timestampVerifiedEmailAddress=" + timestampVerifiedEmailAddress +
        ", firstName='" + firstName + '\'' +
        ", lastName='" + lastName + '\'' +
        ", locationSamples.size()=" + locationSamples.size() +
        '}';
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  public String getIdentity() {
    return identity;
  }

  public void setIdentity(String identity) {
    this.identity = identity;
  }

  public long getTimestampCreated() {
    return timestampCreated;
  }

  public void setTimestampCreated(long timestampCreated) {
    this.timestampCreated = timestampCreated;
  }

  public Long getTimestampVerifiedEmailAddress() {
    return timestampVerifiedEmailAddress;
  }

  public void setTimestampVerifiedEmailAddress(Long timestampVerifiedEmailAddress) {
    this.timestampVerifiedEmailAddress = timestampVerifiedEmailAddress;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public List<LocationSample> getLocationSamples() {
    return locationSamples;
  }

  public void setLocationSamples(List<LocationSample> locationSamples) {
    this.locationSamples = locationSamples;
  }


}
