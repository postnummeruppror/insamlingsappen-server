package nu.postnummeruppror.insamlingsappen.webapp.version_0_0_4;

import nu.postnummeruppror.insamlingsappen.Insamlingsappen;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.json.JSONTokener;

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

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    try {

      JSONObject responseJSON = new JSONObject();

      responseJSON.put("numberOfLocationSamples", Insamlingsappen.getInstance().getPrevayler().prevalentSystem().getLocationSamples().size());
      responseJSON.put("numberOfAccounts", Insamlingsappen.getInstance().getPrevayler().prevalentSystem().getAccounts().size());
      responseJSON.put("numberOfPostalCodes", Insamlingsappen.getInstance().getPrevayler().prevalentSystem().getPostalCodes().size());
      responseJSON.put("numberOfPostalTowns", Insamlingsappen.getInstance().getPrevayler().prevalentSystem().getPostalTownIntern().getMap().size());

      responseJSON.put("success", true);

      response.setCharacterEncoding("UTF-8");
      response.setContentType("application/json");
      response.getOutputStream().write(responseJSON.toString().getBytes("UTF-8"));


    } catch (Exception e) {

      throw new RuntimeException(e);

    }

  }


}
