package nu.postnummeruppror.insamlingsappen.webapp;

import nu.postnummeruppror.insamlingsappen.Insamlingsappen;
import nu.postnummeruppror.insamlingsappen.domain.Account;
import nu.postnummeruppror.insamlingsappen.transactions.SetAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * @author kalle
 * @since 2014-09-06 01:00
 */
public class SetAccountServlet extends HttpServlet {

  private static final Logger log = LoggerFactory.getLogger(SetAccountServlet.class);

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    SetAccount setAccount = new SetAccount();
    setAccount.setAccountIdentity(UUID.randomUUID().toString());
    setAccount.setEmailAddress(request.getParameter("emailAddress"));

    Account account;
    try {
      account = Insamlingsappen.getInstance().getPrevayler().execute(setAccount);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.getOutputStream().write("{ \"success\": true }".getBytes("UTF-8"));
    response.getOutputStream().close();

    log.info("Updated account " + account);


  }
}