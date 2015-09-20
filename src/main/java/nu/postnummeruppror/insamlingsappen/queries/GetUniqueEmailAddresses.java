package nu.postnummeruppror.insamlingsappen.queries;

import nu.postnummeruppror.insamlingsappen.Insamlingsappen;
import nu.postnummeruppror.insamlingsappen.domain.Account;
import nu.postnummeruppror.insamlingsappen.domain.Root;
import org.prevayler.Query;

import java.util.*;

/**
 * @author kalle
 * @since 2014-09-26 18:45
 */
public class GetUniqueEmailAddresses implements Query<Root, Set<String>> {

  public static void main(String[] args) throws Exception {
    Insamlingsappen.getInstance().open();
    try {
      int count = 0;
      for (Account account : Insamlingsappen.getInstance().getPrevayler().prevalentSystem().getAccounts().values()) {
        if (account.getEmailAddress() == null) {
          count ++;
        }
      }
      System.out.println("Number of accounts: " + Insamlingsappen.getInstance().getPrevayler().prevalentSystem().getAccounts().size());
      System.out.println("Number of accounts without email address: " + count);

      List<String> emailAddresses = new ArrayList<>(Insamlingsappen.getInstance().getPrevayler().execute(new GetUniqueEmailAddresses()));
      Collections.sort(emailAddresses);

      System.out.println("Number of unique email addresses: " + emailAddresses.size());

      for (String emailAddress : emailAddresses) {
        System.out.println(emailAddress);
      }



    } finally {
      Insamlingsappen.getInstance().close();
    }
  }

  @Override
  public Set<String> query(Root root, Date executionTime) throws Exception {
    Set<String> normalizedEmailAddresses = new HashSet<>(root.getAccounts().size());
    for (Account account : root.getAccounts().values()) {
      String normalizedEmailAddress = normalizeEmailAddress(account.getEmailAddress());
      if (normalizedEmailAddress != null) {
        normalizedEmailAddresses.add(normalizedEmailAddress);
      }
    }
    return normalizedEmailAddresses;
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
