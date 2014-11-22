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
 * This should be limited to n requests per minute in server!
 * <p/>
 * nginx like this:
 * <p/>
 * <p/>
 * <pre>{@code
 *   limit_req_zone $binary_remote_addr zone=account_exists:10m rate=1r/m;
 *   server {
 *     listen 80;
 *     server_name insamling.postnummeruppror.nu insamling.postnummeruppror.nu.kodapan.se;
 *     access_log  /var/log/nginx/insamling.postnummeruppror.nu-access.log;
 *     location /  {
 *       proxy_pass http://postnummeruppror.virt:8081;
 *       proxy_set_header Host $host;
 *     }
 *     location ~ /api/[^/]+/account/exists {
 *       limit_req zone=account_exists burst=5;
 *       proxy_pass http://postnummeruppror.virt:8081;
 *       proxy_set_header Host $host;
 *     }
 *   }
 * }</pre>
 *
 * @author kalle
 * @since 2014-09-13 16:38
 */
public class AccountExistsServlet extends HttpServlet {

  private static final Logger log = LoggerFactory.getLogger(AccountExistsServlet.class);

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    StringBuilder documentation = new StringBuilder(1024);
    documentation.append("This service accept UTF-8 encoded HTTP post.\n");
    documentation.append("\n");
    documentation.append("JSON request:\n");
    documentation.append("{\n");
    documentation.append("\n");
    documentation.append("  \"identity\": Required. String value. Client defined unique account identity, preferably an UUID.\n");
    documentation.append("\n");
    documentation.append("}\n");

    documentation.append("\n");
    documentation.append("\n");
    documentation.append("JSON response:\n");
    documentation.append("{\n");
    documentation.append("\n");
    documentation.append("  \"exists\": Boolean value.\n");
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

      log.debug("Incoming request: " + requestJSON.toString());

      JSONObject responseJSON = new JSONObject();
      responseJSON.put("success", true);

      responseJSON.put("exists", Insamlingsappen.getInstance().getPrevayler().prevalentSystem().getAccounts().containsKey(requestJSON.getString("identity")));

      response.setCharacterEncoding("UTF-8");
      response.setContentType("application/json");
      response.getOutputStream().write(responseJSON.toString().getBytes("UTF-8"));


    } catch (Exception e) {

      throw new RuntimeException(e);

    }

  }


}
