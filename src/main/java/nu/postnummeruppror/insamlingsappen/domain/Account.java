package nu.postnummeruppror.insamlingsappen.domain;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

/**
 * @author kalle
 * @since 2014-09-06 00:45
 */
@Entity(version = 1)
public class Account {

  @PrimaryKey
  private String identity;

  private long timestampCreated;

  private Long timestampVerified;

  @SecondaryKey(relate = Relationship.ONE_TO_ONE)
  private String emailAddress;

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

  public Long getTimestampVerified() {
    return timestampVerified;
  }

  public void setTimestampVerified(Long timestampVerified) {
    this.timestampVerified = timestampVerified;
  }
}
