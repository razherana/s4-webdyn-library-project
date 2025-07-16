package mg.razherana.library.controllers.loans;

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

import mg.razherana.library.models.loans.MembershipType;
import mg.razherana.library.services.loans.MembershipTypeService;

@Controller
@RequestMapping("/membership-types")
public class MembershipTypeController {

  @Autowired
  private MembershipTypeService membershipTypeService;

  @GetMapping
  public String listMembershipTypes(Model model) {
    List<MembershipType> membershipTypes = membershipTypeService.findAll();
    model.addAttribute("membershipTypes", membershipTypes);
    return "membership-types/list";
  }

  @GetMapping("/add")
  public String showAddForm() {
    return "membership-types/create";
  }

  @PostMapping("/add")
  public String addMembershipType(
      @RequestParam String name,
      @RequestParam int maxBooksAllowedHome,
      @RequestParam int maxBooksAllowedLibrary,
      @RequestParam(required = false, defaultValue = "0") int maxTimeHoursHome,
      @RequestParam(required = false, defaultValue = "0") int maxTimeHoursLibrary,
      @RequestParam(required = false, defaultValue = "2") int maxExtensionsAllowed,
      @RequestParam(required = false, defaultValue = "2") int punishmentTime,
      RedirectAttributes redirectAttributes) {

    MembershipType membershipType = new MembershipType();
    membershipType.setName(name);
    membershipType.setMaxBooksAllowedHome(maxBooksAllowedHome);
    membershipType.setMaxBooksAllowedLibrary(maxBooksAllowedLibrary);
    membershipType.setMaxTimeHoursHome(maxTimeHoursHome);
    membershipType.setMaxTimeHoursLibrary(maxTimeHoursLibrary);
    membershipType.setMaxExtensionsAllowed(maxExtensionsAllowed);
    membershipType.setPunishmentTime(punishmentTime);

    membershipTypeService.save(membershipType);
    redirectAttributes.addFlashAttribute("success", "Membership type added successfully!");
    return "redirect:/membership-types";
  }

  @GetMapping("/edit/{id}")
  public String showEditForm(@PathVariable Long id, Model model) {
    MembershipType membershipType = membershipTypeService.findById(id);
    if (membershipType == null) {
      return "redirect:/membership-types";
    }
    model.addAttribute("membershipType", membershipType);
    return "membership-types/edit";
  }

  @PostMapping("/update")
  public String updateMembershipType(
      @RequestParam Long id,
      @RequestParam String name,
      @RequestParam int maxBooksAllowedHome,
      @RequestParam int maxBooksAllowedLibrary,
      @RequestParam(required = false, defaultValue = "0") int maxTimeHoursHome,
      @RequestParam(required = false, defaultValue = "0") int maxTimeHoursLibrary,
      @RequestParam(required = false, defaultValue = "2") int maxExtensionsAllowed,
      @RequestParam(required = false, defaultValue = "2") int punishmentTime,
      RedirectAttributes redirectAttributes) {

    try {
      MembershipType type = membershipTypeService.findById(id);
      if (type == null) {
        redirectAttributes.addFlashAttribute("error", "Membership type not found");
        return "redirect:/membership-types";
      }

      type.setName(name);
      type.setMaxBooksAllowedHome(maxBooksAllowedHome);
      type.setMaxBooksAllowedLibrary(maxBooksAllowedLibrary);
      type.setMaxTimeHoursHome(maxTimeHoursHome);
      type.setMaxTimeHoursLibrary(maxTimeHoursLibrary);
      type.setMaxExtensionsAllowed(maxExtensionsAllowed);
      type.setPunishmentTime(punishmentTime);

      membershipTypeService.save(type);
      redirectAttributes.addFlashAttribute("success", "Membership type updated successfully!");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", "Error updating membership type: " + e.getMessage());
    }

    return "redirect:/membership-types";
  }

  @PostMapping("/delete")
  public String deleteMembershipType(@RequestParam Long id, RedirectAttributes redirectAttributes) {
    membershipTypeService.delete(id);
    redirectAttributes.addFlashAttribute("success", "Membership type deleted successfully!");
    return "redirect:/membership-types";
  }
}
