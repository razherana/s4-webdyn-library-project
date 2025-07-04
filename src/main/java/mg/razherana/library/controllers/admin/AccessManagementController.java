package mg.razherana.library.controllers.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import mg.razherana.library.models.users.Access;
import mg.razherana.library.repositories.AccessRepository;

@Controller
@RequestMapping("/admin/access")
public class AccessManagementController {

  @Autowired
  private AccessRepository accessRepository;

  @GetMapping
  public String listAccess(Model model) {
    List<Access> accessList = accessRepository.findAll();
    model.addAttribute("accesses", accessList);
    return "admin/access/list";
  }

  @GetMapping("/create")
  public String showAddAccessForm(Model model) {
    model.addAttribute("access", new Access());
    return "admin/access/create";
  }

  @GetMapping("/edit/{id}")
  public String showEditAccessForm(@PathVariable Long id, Model model) {
    Access access = accessRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Invalid access ID: " + id));

    model.addAttribute("access", access);
    return "admin/access/edit";
  }

  @PostMapping("/create")
  public String saveAccess(Access access, RedirectAttributes redirectAttributes) {
    try {
      accessRepository.save(access);
      redirectAttributes.addFlashAttribute("success", "Access saved successfully");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", "Error saving access: " + e.getMessage());
    }

    return "redirect:/admin/access";
  }

  @PostMapping("/delete/{id}")
  public String deleteAccess(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    try {
      accessRepository.deleteById(id);
      redirectAttributes.addFlashAttribute("success", "Access deleted successfully");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", "Error deleting access: " + e.getMessage());
    }

    return "redirect:/admin/access";
  }
}
