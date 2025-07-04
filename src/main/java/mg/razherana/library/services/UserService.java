package mg.razherana.library.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import mg.razherana.library.models.users.User;
import mg.razherana.library.repositories.UserRepository;

@Service
public class UserService {

  private static final String USER_SESSION_ATTR = "currentUser";

  @Autowired
  private UserRepository userRepository;

  public boolean authenticate(String username, String password, HttpSession session) {
    Optional<User> userOpt = userRepository.findByUsername(username);

    if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
      // Store user in session
      session.setAttribute(USER_SESSION_ATTR, userOpt.get());
      return true;
    }

    return false;
  }

  public User getCurrentUser(HttpSession session) {
    return (User) session.getAttribute(USER_SESSION_ATTR);
  }

  public void logout(HttpSession session) {
    session.removeAttribute(USER_SESSION_ATTR);
    session.invalidate();
  }

  public boolean isAuthenticated(HttpSession session) {
    return session.getAttribute(USER_SESSION_ATTR) != null;
  }
}
