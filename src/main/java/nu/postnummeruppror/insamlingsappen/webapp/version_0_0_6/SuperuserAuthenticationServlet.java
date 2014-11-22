package nu.postnummeruppror.insamlingsappen.webapp.version_0_0_6;

import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author kalle
 * @since 2014-09-26 17:33
 */
public class SuperuserAuthenticationServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    response.setHeader("Access-Control-Allow-Origin", "*");

    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    String superuserPassword = IOUtils.toString(new InputStreamReader(new FileInputStream("superuser.password"), "UTF-8")).trim();

    if (superuserPassword.equals(request.getParameter("password").trim())) {
      request.getSession().setAttribute("superuser", true);

      response.getWriter().write("{\"success\":true}");
    } else {
      response.getWriter().write("{\"success\":false}");
    }

  }
}
