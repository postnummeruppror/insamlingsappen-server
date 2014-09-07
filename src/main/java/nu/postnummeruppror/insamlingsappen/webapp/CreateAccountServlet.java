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
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    CreateAccount createAccount = new CreateAccount();
    createAccount.setAccountIdentity(UUID.randomUUID().toString());
    createAccount.setEmailAddress(request.getParameter("emailAddress"));

    Account account;
    try {
      account = Insamlingsappen.getInstance().getPrevayler().execute(createAccount);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    StringBuilder responseJson = new StringBuilder(1024);

    responseJson.append("{ \"success\": true, \"identity\": \"").append(String.valueOf(account.getIdentity())).append("\" }");

    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.getOutputStream().write(responseJson.toString().getBytes("UTF-8"));

  }
}
