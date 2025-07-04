package mg.razherana.library.controllers.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import mg.razherana.library.models.users.Role;
import mg.razherana.library.models.users.User;
import mg.razherana.library.services.RoleService;
import mg.razherana.library.services.UserService;

@Controller
@RequestMapping("/admin/users")
public class UserAdminController {

  @Autowired
  private UserService userService;

  @Autowired
  private RoleService roleService;

  @GetMapping
  public String listUsers(Model model, HttpSession session) {
    // Check if user is authenticated
    if (!userService.isAuthenticated(session)) {
      return "redirect:/auth/login";
    }

    List<User> users = userService.findAll();
    model.addAttribute("users", users);
    return "admin/users/list";
  }

  @GetMapping("/create")
  public String showCreateForm(Model model, HttpSession session) {
    // Check if user is authenticated
    if (!userService.isAuthenticated(session)) {
      return "redirect:/auth/login";
    }

    List<Role> roles = roleService.findAll();
    model.addAttribute("roles", roles);
    return "admin/users/create";
  }

  @PostMapping("/create")
  public String createUser(
      @RequestParam String username,
      @RequestParam String password,
      @RequestParam(required = false) List<Long> roleIds,
      RedirectAttributes redirectAttributes,
      HttpSession session) {

    // Check if user is authenticated
    if (!userService.isAuthenticated(session)) {
      return "redirect:/auth/login";
    }

    try {
      userService.save(username, password, roleIds);
      redirectAttributes.addFlashAttribute("success", "User created successfully");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "redirect:/admin/users/create";
    }

    return "redirect:/admin/users";
  }

  @GetMapping("/edit/{id}")
  public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
    // Check if user is authenticated
    if (!userService.isAuthenticated(session)) {
      return "redirect:/auth/login";
    }

    try {
      User user = userService.findById(id);
      List<Role> allRoles = roleService.findAll();

      model.addAttribute("user", user);
      model.addAttribute("allRoles", allRoles);
      return "admin/users/edit";
    } catch (Exception e) {
      return "redirect:/admin/users";
    }
  }

  @PostMapping("/update")
  public String updateUser(
      @RequestParam Long id,
      @RequestParam String username,
      @RequestParam(required = false) String password,
      @RequestParam(required = false) List<Long> roleIds,
      RedirectAttributes redirectAttributes,
      HttpSession session) {

    // Check if user is authenticated
    if (!userService.isAuthenticated(session)) {
      return "redirect:/auth/login";
    }

    try {
      userService.update(id, username, password, roleIds);
      redirectAttributes.addFlashAttribute("success", "User updated successfully");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
    }

    return "redirect:/admin/users";
  }

  @PostMapping("/delete")
  public String deleteUser(
      @RequestParam Long id,
      RedirectAttributes redirectAttributes,
      HttpSession session) {

    // Check if user is authenticated
    if (!userService.isAuthenticated(session)) {
      return "redirect:/auth/login";
    }

    try {
      userService.delete(id);
      redirectAttributes.addFlashAttribute("success", "User deleted successfully");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
    }

    return "redirect:/admin/users";
  }
}
