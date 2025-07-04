package mg.razherana.library.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpSession;
import mg.razherana.library.models.users.Role;
import mg.razherana.library.models.users.User;
import mg.razherana.library.repositories.RoleRepository;
import mg.razherana.library.repositories.UserRepository;

@Service
public class UserService {

  public static final String USER_SESSION_ATTR = "currentUser";

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Transactional
  public boolean hasAccess(long userId, String uri, String methodType) {
    User user = userRepository.findById(userId).orElse(null);

    System.out.println("hasAccess = " + user);

    if (user == null)
      return false;

    if (user.getRoles().stream().anyMatch(e -> e.getName().toLowerCase().equals("admin"))) {
      return true; // Admins have access to everything
    }

    return user.getRoles().stream()
        .flatMap(role -> role.getAccesses().stream())
        .anyMatch(access -> access.getUri().equals(uri) && access.getMethodType().equalsIgnoreCase(methodType));
  }

  public boolean authenticate(String username, String password, HttpSession session) {
    Optional<User> userOpt = userRepository.findByUsername(username);

    if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
      // Store user in session
      session.setAttribute(USER_SESSION_ATTR, userOpt.get().getId());
      return true;
    }

    return false;
  }

  public User getCurrentUser(HttpSession session) {
    return userRepository.findById((long) session.getAttribute(USER_SESSION_ATTR)).orElse(null);
  }

  public void logout(HttpSession session) {
    session.removeAttribute(USER_SESSION_ATTR);
    session.invalidate();
  }

  public boolean isAuthenticated(HttpSession session) {
    return session.getAttribute(USER_SESSION_ATTR) != null;
  }

  // CRUD operations
  public List<User> findAll() {
    return userRepository.findAll();
  }

  public User findById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
  }

  public User save(String username, String password, List<Long> roleIds) {
    // Check if username already exists
    if (userRepository.findByUsername(username).isPresent()) {
      throw new RuntimeException("Username already exists");
    }

    User user = new User();
    user.setUsername(username);
    user.setPassword(password);

    if (roleIds != null && !roleIds.isEmpty()) {
      List<Role> roles = roleRepository.findAllById(roleIds);
      user.setRoles(roles);
    }

    return userRepository.save(user);
  }

  public User update(Long id, String username, String password, List<Long> roleIds) {
    User user = findById(id);

    // Check if username already exists for another user
    Optional<User> existingUser = userRepository.findByUsername(username);
    if (existingUser.isPresent() && existingUser.get().getId() != id) {
      throw new RuntimeException("Username already exists");
    }

    user.setUsername(username);

    // Only update password if provided
    if (password != null && !password.trim().isEmpty()) {
      user.setPassword(password);
    }

    if (roleIds != null) {
      List<Role> roles = roleRepository.findAllById(roleIds);
      user.setRoles(roles);
    }

    return userRepository.save(user);
  }

  public void delete(Long id) {
    userRepository.deleteById(id);
  }
}
