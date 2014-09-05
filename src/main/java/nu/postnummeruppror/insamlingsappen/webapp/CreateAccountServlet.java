package nu.postnummeruppror.insamlingsappen.webapp;

import nu.postnummeruppror.insamlingsappen.Insamlingsappen;
import nu.postnummeruppror.insamlingsappen.domain.Account;

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

    if (!request.getParameterMap().containsKey("password")) {
      throw new IllegalArgumentException("Missing parameter 'password'");
    }

    Account account = new Account();
    account.setIdentity(UUID.randomUUID().toString());
    account.setTimestampCreated(System.currentTimeMillis());
    Insamlingsappen.getInstance().getDomainStore().getAccounts().put(account);
    response.getWriter().append("{ \"identity\": \"").append(String.valueOf(account.getIdentity())).append("\" }");

  }
}
