package nu.postnummeruppror.insamlingsappen.webapp.version_0_0_4;

import nu.postnummeruppror.insamlingsappen.Insamlingsappen;
import nu.postnummeruppror.insamlingsappen.queries.CountUniquePostalTowns;
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
 * @since 2014-09-09 14:32
 */
public class ServerStatisticsServlet extends HttpServlet {

  private static final Logger log = LoggerFactory.getLogger(ServerStatisticsServlet.class);

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    response.setHeader("Access-Control-Allow-Origin", "*");

    StringBuilder documentation = new StringBuilder(1024);
    documentation.append("This service accept UTF-8 encoded HTTP post.\n");
    documentation.append("\n");
    documentation.append("Request data is ignored.\n");
    documentation.append("\n");
    documentation.append("\n");
    documentation.append("JSON response:\n");
    documentation.append("{\n");
    documentation.append("\n");
    documentation.append("  \"success\": Boolean value.\n");
    documentation.append("\n");
    documentation.append("  \"numberOfLocationSamples\": Integer value. Number of location samples on server.\n");
    documentation.append("  \"numberOfAccounts\": Integer value. Number of accounts on server.\n");
    documentation.append("  \"numberOfPostalCodes\": Integer value. Number of unique postal codes on server.\n");
    documentation.append("  \"numberOfPostalTowns\": Integer value. Number of unique postal towns on server.\n");
    documentation.append("\n");

    documentation.append("}\n");

    response.setContentType("text/plain");
    response.setCharacterEncoding("UTF-8");
    response.getOutputStream().write(documentation.toString().getBytes("UTF-8"));


  }


  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    response.setHeader("Access-Control-Allow-Origin", "*");

    try {

      JSONObject responseJSON = new JSONObject();

      responseJSON.put("numberOfLocationSamples", Insamlingsappen.getInstance().getPrevayler().prevalentSystem().getLocationSamples().size());
      responseJSON.put("numberOfAccounts", Insamlingsappen.getInstance().getPrevayler().prevalentSystem().getAccounts().size());
      responseJSON.put("numberOfPostalCodes", Insamlingsappen.getInstance().getPrevayler().prevalentSystem().getTagsIntern().getValues().get("addr:postcode").getMap().size());
      responseJSON.put("numberOfPostalTowns", Insamlingsappen.getInstance().getPrevayler().execute(new CountUniquePostalTowns()));

      responseJSON.put("success", true);

      response.setCharacterEncoding("UTF-8");
      response.setContentType("application/json");
      response.getOutputStream().write(responseJSON.toString().getBytes("UTF-8"));


    } catch (Exception e) {

      throw new RuntimeException(e);

    }

  }


}
