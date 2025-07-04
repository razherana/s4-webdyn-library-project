package mg.razherana.library.interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.razherana.library.services.UserService;

public class AuthInterceptor implements HandlerInterceptor {

  @Autowired
  private UserService userService;

  @Override
  public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
      @NonNull Object handler) throws Exception {
    // Skip authentication for login and logout endpoints
    String requestURI = request.getRequestURI();
    if (requestURI.contains("/auth/login") || requestURI.contains("/auth/logout")) {
      return true;
    }

    // Check if user is authenticated
    if (!userService.isAuthenticated(request.getSession())) {
      response.sendRedirect(request.getContextPath() + "/auth/login");
      return false;
    }

    return true;
  }
}
