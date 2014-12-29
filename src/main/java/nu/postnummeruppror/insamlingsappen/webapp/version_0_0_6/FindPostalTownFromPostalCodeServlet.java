package nu.postnummeruppror.insamlingsappen.webapp.version_0_0_6;

import nu.postnummeruppror.insamlingsappen.Insamlingsappen;
import nu.postnummeruppror.insamlingsappen.domain.LocationSample;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author kalle
 * @since 2014-12-29 10:04
 */
public class FindPostalTownFromPostalCodeServlet extends HttpServlet {

  private static final Logger log = LoggerFactory.getLogger(CreateLocationSampleServlet.class);

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    response.setHeader("Access-Control-Allow-Origin", "*");


    try {

      JSONObject requestJSON = new JSONObject();
      requestJSON.put("postalCode", request.getParameter("postalCode"));
      doProcess(response, requestJSON);

    } catch (Exception e) {
      throw new RuntimeException(e);
    }


  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    response.setHeader("Access-Control-Allow-Origin", "*");

    try {

      JSONObject requestJSON = new JSONObject(new JSONTokener(IOUtils.toString(request.getInputStream(), "UTF-8")));

      doProcess(response, requestJSON);


    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

  private void doProcess(HttpServletResponse response, JSONObject requestJSON) throws JSONException, IOException {
    log.debug("Incoming request: " + requestJSON.toString());

    String postalCode = requestJSON.getString("postalCode").replaceAll("\\s+", "");

    Map<String, AtomicInteger> scoreMap = new HashMap<>();

    for (LocationSample sample : Insamlingsappen.getInstance().getPrevayler().prevalentSystem().getLocationSamples().values()) {
      if (postalCode.equals(sample.getTag("addr:postcode"))) {
        String postalTown = sample.getTag("addr:city");
        if (postalTown != null) {
          AtomicInteger score = scoreMap.get(postalTown);
          if (score == null) {
            score = new AtomicInteger();
            scoreMap.put(postalTown, score);
          }
          score.incrementAndGet();
        }
      }
    }

    List<Map.Entry<String, AtomicInteger>> scores = new ArrayList<>(scoreMap.entrySet());
    Collections.sort(scores, new Comparator<Map.Entry<String, AtomicInteger>>() {
      @Override
      public int compare(Map.Entry<String, AtomicInteger> o1, Map.Entry<String, AtomicInteger> o2) {
        return Integer.compare(o2.getValue().get(), o1.getValue().get());
      }
    });

    JSONObject responseJSON = new JSONObject();

    if (scores.size() > 0) {

      responseJSON.put("success", true);
      JSONArray postalTowns = new JSONArray(new ArrayList(scores.size()));
      responseJSON.put("results", postalTowns);
      for (Map.Entry<String, AtomicInteger> score : scores) {
        JSONObject jsonScore = new JSONObject(new LinkedHashMap(2));
        jsonScore.put("score", score.getValue().get());
        jsonScore.put("postalTown", score.getKey());
        postalTowns.put(jsonScore);
      }


    } else {

      responseJSON.put("success", false);

    }


    response.setCharacterEncoding("UTF-8");
    response.setContentType("application/json");
    response.getOutputStream().write(responseJSON.toString().getBytes("UTF-8"));
  }
}
