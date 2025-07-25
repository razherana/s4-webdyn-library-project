package mg.razherana.library.interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.razherana.library.models.users.User;
import mg.razherana.library.services.UserService;

@Component
public class AccessInterceptor implements HandlerInterceptor {
  @Autowired
  private UserService userService;

  @Override
  public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
      @NonNull Object handler)
      throws Exception {
    // Skip for static and login/logout endpoints
    String requestURI = request.getRequestURI();
    if (requestURI.contains("/static/") || requestURI.contains("/auth/login") ||
        requestURI.contains("/auth/logout")) {
      return true;
    }

    // Check if user is authenticated
    if (request.getSession().getAttribute(UserService.USER_SESSION_ATTR) == null) {
      response.sendRedirect(request.getContextPath() + "/auth/login");
      return false;
    }

    // Check roles and accesses of the roles
    long userId = (long) request.getSession().getAttribute(
        UserService.USER_SESSION_ATTR);

    requestURI = requestURI.replaceAll("\\?.*", "");

    // Remove trailing slash
    if (requestURI.endsWith("/")) {
      requestURI = requestURI.substring(0, requestURI.length() - 1);
    }

    // Check if user is people and redirect to user home
    
    User currentUser = userService.getCurrentUser(userId);
    System.out.println("Ato letsy, " + currentUser + " " + currentUser.getPeople());

    if (currentUser.isPeople() && (requestURI.isEmpty() || requestURI.equals("/") || requestURI.equals("/home"))) {
      response.sendRedirect(request.getContextPath() + "/user/home");
      return false;
    }

    if (userService.hasAccess(userId, requestURI, request.getMethod())) {
      return true;
    } else {
      response.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have access to this resource");
      return false;
    }
  }
}
