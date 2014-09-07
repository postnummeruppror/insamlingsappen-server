package nu.postnummeruppror.insamlingsappen.transactions;

import nu.postnummeruppror.insamlingsappen.domain.Account;
import nu.postnummeruppror.insamlingsappen.domain.Root;
import org.prevayler.Transaction;
import org.prevayler.TransactionWithQuery;

import java.io.Serializable;
import java.util.Date;

/**
 * @author kalle
 * @since 2014-09-06 20:23
 */
public class CreateAccount implements TransactionWithQuery<Root, Account>, Serializable {

  private static final long serialVersionUID = 1l;

  public CreateAccount() {
  }

  private String accountIdentity;
  private String emailAddress;

  public CreateAccount(String accountIdentity, String emailAddress) {
    this.accountIdentity = accountIdentity;
    this.emailAddress = emailAddress;
  }

  @Override
  public Account executeAndQuery(Root root, Date executionTime) throws Exception {
    Account account = new Account();
    account.setIdentity(accountIdentity);
    account.setEmailAddress(emailAddress);
    root.getAccounts().put(account.getIdentity(), account);
    return account;
  }

  public String getAccountIdentity() {
    return accountIdentity;
  }

  public void setAccountIdentity(String accountIdentity) {
    this.accountIdentity = accountIdentity;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }
}
