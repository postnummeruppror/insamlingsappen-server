package nu.postnummeruppror.insamlingsappen.queries;

import nu.postnummeruppror.insamlingsappen.Insamlingsappen;
import nu.postnummeruppror.insamlingsappen.domain.Account;
import nu.postnummeruppror.insamlingsappen.domain.Root;
import org.prevayler.Query;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author kalle
 * @since 2014-09-26 18:45
 */
public class CountUniqueEmailAddresses implements Query<Root, Integer> {

  public static void main(String[] args) throws Exception {
    Insamlingsappen.getInstance().open();
    try {
      int count = 0;
      for (Account account : Insamlingsappen.getInstance().getPrevayler().prevalentSystem().getAccounts().values()) {
        if (account.getEmailAddress() == null) {
          count ++;
        }
      }
      System.out.println(count);
      System.out.println(Insamlingsappen.getInstance().getPrevayler().prevalentSystem().getAccounts().size());
      System.out.println(Insamlingsappen.getInstance().getPrevayler().execute(new CountUniqueEmailAddresses()));
    } finally {
      Insamlingsappen.getInstance().close();
    }
  }

  @Override
  public Integer query(Root root, Date executionTime) throws Exception {
    Set<String> normalizedEmailAddresses = new HashSet<>(root.getAccounts().size());
    for (Account account : root.getAccounts().values()) {
      String normalizedEmailAddress = normalizeEmailAddress(account.getEmailAddress());
      if (normalizedEmailAddress != null) {
        normalizedEmailAddresses.add(normalizedEmailAddress);
      }
    }
    return normalizedEmailAddresses.size();
  }

  private String normalizeEmailAddress(String emailAddress) {
    if (emailAddress == null) {
      return null;
    }
    emailAddress = emailAddress.trim();
    emailAddress = emailAddress.toLowerCase();
    return emailAddress;
  }
}
