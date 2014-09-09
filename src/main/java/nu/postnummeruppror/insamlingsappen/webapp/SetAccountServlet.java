package nu.postnummeruppror.insamlingsappen.webapp;

import nu.postnummeruppror.insamlingsappen.Insamlingsappen;
import nu.postnummeruppror.insamlingsappen.domain.Account;
import nu.postnummeruppror.insamlingsappen.transactions.SetAccount;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.json.JSONTokener;
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
public class SetAccountServlet extends NoHammeringHttpServlet {

  private static final Logger log = LoggerFactory.getLogger(SetAccountServlet.class);

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    if (noHammering(request, response)) {

      SetAccount setAccount = new SetAccount();
      setAccount.setAccountIdentity(request.getParameter("identity"));
      setAccount.setEmailAddress(request.getParameter("emailAddress"));
      setAccount.setAcceptingCcZero(Boolean.valueOf(request.getParameter("acceptingCcZero")));

      sendResponse(setAccount, request, response);

    }

  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    if (noHammering(request, response)) {

      try {

        String jsonString = IOUtils.toString(request.getInputStream(), "UTF-8");
        JSONObject requestJson = new JSONObject(new JSONTokener(jsonString));

        SetAccount setAccount = new SetAccount();
        setAccount.setAccountIdentity(requestJson.getString("identity"));
        setAccount.setEmailAddress(requestJson.getString("emailAddress"));
        setAccount.setAcceptingCcZero(requestJson.getBoolean("acceptingCcZero"));

        sendResponse(setAccount, request, response);

      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

  }

  private void sendResponse(SetAccount setAccount, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
