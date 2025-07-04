package mg.razherana.library.controllers.admin;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import mg.razherana.library.models.users.Access;
import mg.razherana.library.models.users.Role;
import mg.razherana.library.repositories.AccessRepository;
import mg.razherana.library.repositories.RoleRepository;

@Controller
@RequestMapping("/admin/roles")
public class RoleManagementController {

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private AccessRepository accessRepository;

  @GetMapping
  public String listRoles(Model model) {
    // Get all roles except the admin role
    List<Role> roles = roleRepository.findAll().stream()
        .filter(role -> !"admin".equals(role.getName()))
        .collect(Collectors.toList());

    model.addAttribute("roles", roles);
    return "admin/roles/list";
  }

  @GetMapping("/add")
  public String showAddRoleForm(Model model) {
    List<Access> allAccess = accessRepository.findAll();
    model.addAttribute("allAccess", allAccess);
    model.addAttribute("role", new Role());
    return "admin/roles/edit";
  }

  @GetMapping("/edit/{id}")
  public String showEditRoleForm(@PathVariable Long id, Model model) {

    System.out.println(id);

    Role role = roleRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Invalid role ID: " + id));

    // Don't allow editing the admin role
    if ("admin".equals(role.getName())) {
      return "redirect:/admin/roles";
    }

    List<Access> allAccess = accessRepository.findAll();

    model.addAttribute("role", role);
    model.addAttribute("allAccesses", allAccess);

    return "admin/roles/edit";
  }

  @PostMapping("/save")
  public String saveRole(Role role,
      @RequestParam(required = false) List<Long> accessIds,
      RedirectAttributes redirectAttributes) {
    try {
      // Don't allow saving with the name "admin"
      if ("admin".equalsIgnoreCase(role.getName())) {
        redirectAttributes.addFlashAttribute("error", "Cannot create or modify the admin role");
        return "redirect:/admin/roles";
      }

      // Handle access assignment
      if (accessIds != null && !accessIds.isEmpty()) {
        List<Access> selectedAccess = accessRepository.findAllById(accessIds);
        role.setAccesses(selectedAccess.stream().collect(Collectors.toSet()));
      }

      roleRepository.save(role);
      redirectAttributes.addFlashAttribute("success", "Role saved successfully");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", "Error saving role: " + e.getMessage());
    }

    return "redirect:/admin/roles";
  }

  @PostMapping("/delete/{id}")
  public String deleteRole(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    try {
      Role role = roleRepository.findById(id)
          .orElseThrow(() -> new IllegalArgumentException("Invalid role ID: " + id));

      // Don't allow deleting the admin role
      if ("admin".equals(role.getName())) {
        redirectAttributes.addFlashAttribute("error", "Cannot delete the admin role");
        return "redirect:/admin/roles";
      }

      roleRepository.deleteById(id);
      redirectAttributes.addFlashAttribute("success", "Role deleted successfully");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", "Error deleting role: " + e.getMessage());
    }

    return "redirect:/admin/roles";
  }

  @PostMapping("/update")
  public String updateRole(
      @RequestParam Long id,
      @RequestParam String name,
      @RequestParam String description,
      @RequestParam(required = false) List<Long> accessIds,
      RedirectAttributes redirectAttributes) {
    try {
      // Get the existing role
      Role role = roleRepository.findById(id)
          .orElseThrow(() -> new IllegalArgumentException("Invalid role ID: " + id));

      // Don't allow modifying the admin role
      if ("admin".equals(role.getName())) {
        redirectAttributes.addFlashAttribute("error", "Cannot modify the admin role");
        return "redirect:/admin/roles";
      }

      // Update the role properties
      role.setName(name);
      role.setDescription(description);

      // Handle access assignment
      if (accessIds != null && !accessIds.isEmpty()) {
        Set<Access> selectedAccesses = accessRepository.findAllById(accessIds).stream().collect(Collectors.toSet());
        role.setAccesses(selectedAccesses);
      } else {
        role.setAccesses(null); // Clear accesses if none selected
      }

      // Save the updated role
      roleRepository.save(role);
      redirectAttributes.addFlashAttribute("success", "Role updated successfully");

    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", "Error updating role: " + e.getMessage());
    }

    return "redirect:/admin/roles";
  }
}
