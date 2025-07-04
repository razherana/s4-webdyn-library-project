package mg.razherana.library.interceptors;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mg.razherana.library.models.users.Access;
import mg.razherana.library.models.users.Role;
import mg.razherana.library.models.users.User;
import mg.razherana.library.services.UserService;

@Component
public class LoadUserRolesAccessInterceptor implements HandlerInterceptor {

  @Autowired
  private UserService userService;

  public static final String USER_SESSION_ATTR = "currentUser";

  @Override
  public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
      @NonNull Object handler) {
    HttpSession session = request.getSession();

    // Check if the user is authenticated
    if (session.getAttribute(UserService.USER_SESSION_ATTR) == null)
      return true;

    User user = userService.getCurrentUser(session);

    if (user == null)
      return true;

    // Process user access information
    boolean isAdmin = false;
    Set<String> accessUris = new HashSet<>();

    if (user.getRoles() != null) {
      for (Role role : user.getRoles()) {
        // Check if user has admin role
        if ("admin".equals(role.getName())) {
          isAdmin = true;
        }

        // Collect all URIs from access rights
        if (role.getAccesses() != null) {
          for (Access access : role.getAccesses()) {
            if (access.getUri() != null && !access.getUri().isEmpty()) {
              accessUris.add(access.getUri());
            }
          }
        }
      }
    }

    // Store admin status and access URIs in session for sidebar use
    request.setAttribute("isAdmin", isAdmin);
    request.setAttribute("userAccessUris", accessUris);

    // Also store the original objects for any other components that need them
    request.setAttribute("currentUserRoles", user.getRoles());
    request.setAttribute("currentUserAccesses",
        user.getRoles().stream()
            .flatMap(role -> role.getAccesses().stream())
            .toList() // Collect all accesses from the user's roles
    );

    return true; // All requests are allowed to proceed
  }
}
