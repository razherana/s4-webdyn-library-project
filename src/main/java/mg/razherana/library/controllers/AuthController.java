package mg.razherana.library.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import mg.razherana.library.models.users.User;
import mg.razherana.library.services.UserService;

@Controller
@RequestMapping("/auth")
public class AuthController {

  @Autowired
  private UserService userService;

  @GetMapping("/login")
  public String showLoginPage() {
    return "auth/login";
  }

  @PostMapping("/login")
  public String login(
      @RequestParam String username,
      @RequestParam String password,
      HttpSession session,
      RedirectAttributes redirectAttributes) {

    if (userService.authenticate(username, password, session)) {
      User user = userService.getCurrentUser(session);
      redirectAttributes.addFlashAttribute("success", "Login successful for user: " + user.getUsername());
      return "redirect:/home"; // Redirect to home page after successful login
    }

    redirectAttributes.addFlashAttribute("error", "Invalid username or password");
    return "redirect:/auth/login";
  }

  @GetMapping("/current-user")
  public ResponseEntity<?> getCurrentUser(HttpSession session) {
    if (userService.isAuthenticated(session)) {
      User user = userService.getCurrentUser(session);
      return ResponseEntity.ok().body(user);
    }

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
  }

  @GetMapping("/logout")
  public String logoutGet(HttpSession session, RedirectAttributes redirectAttributes) {
    return logoutPost(session, redirectAttributes);
  }

  @PostMapping("/logout")
  public String logoutPost(HttpSession session, RedirectAttributes redirectAttributes) {
    userService.logout(session);
    redirectAttributes.addFlashAttribute("success", "Logged out successfully");
    return "redirect:/auth/login";
  }
}
