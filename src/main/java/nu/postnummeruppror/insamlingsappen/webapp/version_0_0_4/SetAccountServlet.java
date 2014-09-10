package nu.postnummeruppror.insamlingsappen.webapp.version_0_0_4;

import nu.postnummeruppror.insamlingsappen.Insamlingsappen;
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

/**
 * @author kalle
 * @since 2014-09-10 18:43
 */
public class SetAccountServlet extends HttpServlet {

  private static final Logger log = LoggerFactory.getLogger(SetAccountServlet.class);

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    StringBuilder documentation = new StringBuilder(1024);
    documentation.append("This service accept UTF-8 encoded HTTP post.\n");
    documentation.append("\n");
    documentation.append("JSON request:\n");
    documentation.append("{\n");
    documentation.append("\n");
    documentation.append("  \"identity\": Required. String value. Client defined unique account identity, preferably an UUID.\n");
    documentation.append("  \"acceptingCcZero\": Optional. Boolean value. If the account accepts that all LocationSamples reports are distributed as CC0.\n");
    documentation.append("  \"firstName\": Optional. String value.\n");
    documentation.append("  \"lastName\": Optional. String value.\n");
    documentation.append("  \"emailAddress\": Optional. String value.\n");
    documentation.append("\n");
    documentation.append("  \"query\": {\n");
    documentation.append("   }\n");
    documentation.append("\n");
    documentation.append("}\n");

    documentation.append("\n");
    documentation.append("\n");
    documentation.append("JSON response:\n");
    documentation.append("{\n");
    documentation.append("\n");
    documentation.append("  \"success\": Boolean value.\n");
    documentation.append("\n");
    documentation.append("}\n");

    response.setContentType("text/plain");
    response.setCharacterEncoding("UTF-8");
    response.getOutputStream().write(documentation.toString().getBytes("UTF-8"));


  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    try {

      JSONObject requestJSON = new JSONObject(new JSONTokener(IOUtils.toString(request.getInputStream(), "UTF-8")));

      SetAccount setAccount = new SetAccount();

      // required
      setAccount.setAccountIdentity(requestJSON.getString("identity"));

      if (requestJSON.has("acceptingCcZero")) {
        setAccount.setAcceptingCcZero(requestJSON.getBoolean("acceptingCcZero"));
      }

      if (requestJSON.has("firstName")) {
        setAccount.setFirstName(requestJSON.getString("firstName"));
      }

      if (requestJSON.has("lastName")) {
        setAccount.setLastName(requestJSON.getString("lastName"));
      }

      if (requestJSON.has("emailAddress")) {
        setAccount.setEmailAddress(requestJSON.getString("emailAddress"));
      }

      Insamlingsappen.getInstance().getPrevayler().execute(setAccount);


      JSONObject responseJSON = new JSONObject();
      responseJSON.put("success", true);

      response.setCharacterEncoding("UTF-8");
      response.setContentType("application/json");
      response.getOutputStream().write(responseJSON.toString().getBytes("UTF-8"));


    } catch (Exception e) {

      throw new RuntimeException(e);

    }

  }
}
