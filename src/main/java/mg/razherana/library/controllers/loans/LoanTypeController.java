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

import mg.razherana.library.models.loans.LoanType;
import mg.razherana.library.services.loans.LoanTypeService;

@Controller
@RequestMapping("/loan-types")
public class LoanTypeController {

  @Autowired
  private LoanTypeService loanTypeService;

  @GetMapping
  public String listLoanTypes(Model model) {
    List<LoanType> loanTypes = loanTypeService.findAll();
    model.addAttribute("loanTypes", loanTypes);
    return "loan-types/list";
  }

  @GetMapping("/add")
  public String showAddForm() {
    return "loan-types/create";
  }

  @PostMapping("/add")
  public String addLoanType(@RequestParam String name, RedirectAttributes redirectAttributes) {
    try {
      loanTypeService.save(name);
      redirectAttributes.addFlashAttribute("success", "Loan type added successfully.");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "redirect:/loan-types/add";
    }
    return "redirect:/loan-types";
  }

  @GetMapping("/edit/{id}")
  public String showEditForm(@PathVariable Long id, Model model) {
    try {
      LoanType loanType = loanTypeService.findById(id);
      model.addAttribute("loanType", loanType);
      return "loan-types/edit";
    } catch (IllegalArgumentException e) {
      return "redirect:/loan-types";
    }
  }

  @PostMapping("/update")
  public String updateLoanType(
      @RequestParam Long id,
      @RequestParam String name,
      RedirectAttributes redirectAttributes) {
    try {
      loanTypeService.update(id, name);
      redirectAttributes.addFlashAttribute("success", "Loan type updated successfully.");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
    }
    return "redirect:/loan-types";
  }

  @PostMapping("/delete")
  public String deleteLoanType(@RequestParam Long id, RedirectAttributes redirectAttributes) {
    try {
      loanTypeService.delete(id);
      redirectAttributes.addFlashAttribute("success", "Loan type deleted successfully.");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
    }
    return "redirect:/loan-types";
  }
}
