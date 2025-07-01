package mg.razherana.library.controllers.loans;

import mg.razherana.library.models.loans.MembershipType;
import mg.razherana.library.services.loans.MembershipTypeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/membership-types")
public class MembershipTypeController {

  @Autowired
  private MembershipTypeService membershipTypeService;

  @GetMapping("")
  public String list(Model model) {
    List<MembershipType> types = membershipTypeService.findAll();
    model.addAttribute("membershipTypes", types);
    model.addAttribute("pageTitle", "Membership Types Management");
    return "membership-types/list";
  }

  @GetMapping("/add")
  public String addForm(Model model) {
    model.addAttribute("pageTitle", "Add Membership Type");
    return "membership-types/create";
  }

  @PostMapping("/add")
  public String add(@RequestParam String name,
      @RequestParam int maxBooksAllowedHome,
      @RequestParam int maxBooksAllowedLibrary) {

    MembershipType type = new MembershipType();
    type.setName(name);
    type.setMaxBooksAllowedHome(maxBooksAllowedHome);
    type.setMaxBooksAllowedLibrary(maxBooksAllowedLibrary);

    membershipTypeService.save(type);
    return "redirect:/membership-types";
  }

  @GetMapping("/edit/{id}")
  public String editForm(@PathVariable Long id, Model model) {
    MembershipType type = membershipTypeService.findById(id);
    if (type != null) {
      model.addAttribute("membershipType", type);
      model.addAttribute("pageTitle", "Edit Membership Type");
      return "membership-types/edit";
    }
    return "redirect:/membership-types";
  }

  @PostMapping("/update")
  public String update(@RequestParam Long id,
      @RequestParam String name,
      @RequestParam int maxBooksAllowedHome,
      @RequestParam int maxBooksAllowedLibrary) {

    membershipTypeService.update(id, name, maxBooksAllowedHome, maxBooksAllowedLibrary);
    return "redirect:/membership-types";
  }

  @PostMapping("/delete")
  public String delete(@RequestParam Long id) {
    membershipTypeService.delete(id);
    return "redirect:/membership-types";
  }
}
