package nu.postnummeruppror.insamlingsappen.transactions;

import nu.postnummeruppror.insamlingsappen.domain.Account;
import nu.postnummeruppror.insamlingsappen.domain.Root;
import org.prevayler.TransactionWithQuery;

import java.io.Serializable;
import java.util.Date;

/**
 * @author kalle
 * @since 2014-09-06 20:23
 */
public class IdentityFactory implements TransactionWithQuery<Root, Long>, Serializable {

  private static final long serialVersionUID = 1l;

  public IdentityFactory() {
  }

  @Override
  public Long executeAndQuery(Root root, Date executionTime) throws Exception {
    return root.getIdentityFactory().incrementAndGet();
  }
}
