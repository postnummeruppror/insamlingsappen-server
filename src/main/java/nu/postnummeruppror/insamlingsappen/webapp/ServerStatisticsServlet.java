package nu.postnummeruppror.insamlingsappen.webapp;

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
public class ServerStatisticsServlet extends NoHammeringHttpServlet {


  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    if (noHammering(request, response)) {
      try {
        sendResponse(createResponse(), request, response);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }


  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    if (noHammering(request, response)) {
      try {
        sendResponse(createResponse(), request, response);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }


  }

  private JSONObject createResponse() throws Exception {

    JSONObject json = new JSONObject();


    json.put("numberOfLocationSamples", Insamlingsappen.getInstance().getPrevayler().prevalentSystem().getLocationSamples().size());
    json.put("numberOfAccounts", Insamlingsappen.getInstance().getPrevayler().prevalentSystem().getAccounts().size());
    json.put("numberOfPostalCodes", Insamlingsappen.getInstance().getPrevayler().prevalentSystem().getPostalCodes().size());
    json.put("numberOfPostalTowns", Insamlingsappen.getInstance().getPrevayler().prevalentSystem().getPostalTownIntern().getMap().size());

    json.put("success", true);

    return json;

  }


  private void sendResponse(JSONObject json, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.getOutputStream().write(json.toString().getBytes("UTF-8"));
    response.getOutputStream().close();

  }


}
