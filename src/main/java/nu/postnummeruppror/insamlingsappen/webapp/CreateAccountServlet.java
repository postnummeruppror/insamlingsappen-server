package nu.postnummeruppror.insamlingsappen.webapp;

import nu.postnummeruppror.insamlingsappen.Insamlingsappen;
import nu.postnummeruppror.insamlingsappen.domain.Account;
import nu.postnummeruppror.insamlingsappen.transactions.CreateAccount;

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
public class CreateAccountServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    if (!request.getParameterMap().containsKey("emailAddress")) {
      throw new IllegalArgumentException("Missing parameter 'emailAddress'");
    }

    CreateAccount createAccount = new CreateAccount();
    createAccount.setAccountIdentity(UUID.randomUUID().toString());
    createAccount.setEmailAddress(request.getParameter("emailAddress"));

    Account account;
    try {
      account = Insamlingsappen.getInstance().getPrevayler().execute(createAccount);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    response.getWriter().append("{ \"identity\": \"").append(String.valueOf(account.getIdentity())).append("\" }");

  }
}
