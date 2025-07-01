package mg.razherana.library.controllers.loans;

import mg.razherana.library.models.loans.Membership;
import mg.razherana.library.models.loans.MembershipType;
import mg.razherana.library.models.loans.People;
import mg.razherana.library.services.loans.MembershipService;
import mg.razherana.library.services.loans.MembershipTypeService;
import mg.razherana.library.services.loans.PeopleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/memberships")
public class MembershipController {

  @Autowired
  private MembershipService membershipService;

  @Autowired
  private PeopleService peopleService;

  @Autowired
  private MembershipTypeService membershipTypeService;

  @GetMapping("")
  public String list(Model model) {
    List<Membership> memberships = membershipService.findAll();
    model.addAttribute("memberships", memberships);
    model.addAttribute("today", LocalDate.now());
    model.addAttribute("pageTitle", "Memberships Management");
    return "memberships/list";
  }

  @GetMapping("/add")
  public String registerForm(Model model) {
    List<People> peoples = peopleService.findAll();
    List<MembershipType> types = membershipTypeService.findAll();

    model.addAttribute("peoples", peoples);
    model.addAttribute("membershipTypes", types);
    model.addAttribute("today", LocalDate.now());
    model.addAttribute("pageTitle", "Register Membership");

    return "memberships/add";
  }

  @PostMapping("/add")
  public String register(@RequestParam Long peopleId,
      @RequestParam Long membershipTypeId,
      @RequestParam LocalDate startDate,
      @RequestParam LocalDate endDate,
      RedirectAttributes redirectAttributes) {

    try {
      membershipService.register(peopleId, membershipTypeId, startDate, endDate);
      redirectAttributes.addFlashAttribute("success", "Membership registered successfully");
    } catch (IllegalStateException e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "redirect:/memberships/add";
    }

    return "redirect:/memberships";
  }
}
