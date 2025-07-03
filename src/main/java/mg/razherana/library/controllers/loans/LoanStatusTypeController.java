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

import mg.razherana.library.models.loans.LoanStatusType;
import mg.razherana.library.repositories.loans.LoanStatusTypeRepository;

@Controller
@RequestMapping("/loan-status-types")
public class LoanStatusTypeController {

  @Autowired
  private LoanStatusTypeRepository loanStatusTypeRepository;

  @GetMapping
  public String listStatusTypes(Model model) {
    List<LoanStatusType> statusTypes = loanStatusTypeRepository.findAll();
    model.addAttribute("statusTypes", statusTypes);
    return "loan-status-types/list";
  }

  @GetMapping("/add")
  public String showAddForm() {
    return "loan-status-types/create";
  }

  @PostMapping("/add")
  public String addStatusType(@RequestParam String name, RedirectAttributes redirectAttributes) {
    LoanStatusType statusType = new LoanStatusType();
    statusType.setName(name);
    loanStatusTypeRepository.save(statusType);

    redirectAttributes.addFlashAttribute("success", "Status type added successfully.");
    return "redirect:/loan-status-types";
  }

  @GetMapping("/edit/{id}")
  public String showEditForm(@PathVariable Long id, Model model) {
    LoanStatusType statusType = loanStatusTypeRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Invalid status type ID: " + id));

    model.addAttribute("statusType", statusType);
    return "loan-status-types/edit";
  }

  @PostMapping("/update")
  public String updateStatusType(
      @RequestParam Long id,
      @RequestParam String name,
      RedirectAttributes redirectAttributes) {

    LoanStatusType statusType = loanStatusTypeRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Invalid status type ID: " + id));

    statusType.setName(name);
    loanStatusTypeRepository.save(statusType);

    redirectAttributes.addFlashAttribute("success", "Status type updated successfully.");
    return "redirect:/loan-status-types";
  }

  @PostMapping("/delete")
  public String deleteStatusType(@RequestParam Long id, RedirectAttributes redirectAttributes) {
    loanStatusTypeRepository.deleteById(id);
    redirectAttributes.addFlashAttribute("success", "Status type deleted successfully.");
    return "redirect:/loan-status-types";
  }
}
