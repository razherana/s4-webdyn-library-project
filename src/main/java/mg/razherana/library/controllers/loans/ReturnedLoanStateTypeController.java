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

import mg.razherana.library.models.loans.ReturnedLoanStateType;
import mg.razherana.library.repositories.loans.ReturnedLoanStateTypeRepository;

@Controller
@RequestMapping("/returned-loan-state-types")
public class ReturnedLoanStateTypeController {

  @Autowired
  private ReturnedLoanStateTypeRepository stateTypeRepository;

  @GetMapping
  public String listStateTypes(Model model) {
    List<ReturnedLoanStateType> stateTypes = stateTypeRepository.findAll();
    model.addAttribute("stateTypes", stateTypes);
    return "returned-loan-state-types/list";
  }

  @GetMapping("/add")
  public String showAddForm() {
    return "returned-loan-state-types/create";
  }

  @PostMapping("/add")
  public String addStateType(
      @RequestParam String name,
      @RequestParam String description,
      RedirectAttributes redirectAttributes) {

    ReturnedLoanStateType stateType = new ReturnedLoanStateType();
    stateType.setName(name);
    stateType.setDescription(description);

    stateTypeRepository.save(stateType);
    redirectAttributes.addFlashAttribute("success", "Return state type added successfully.");

    return "redirect:/returned-loan-state-types";
  }

  @GetMapping("/edit/{id}")
  public String showEditForm(@PathVariable Long id, Model model) {
    ReturnedLoanStateType stateType = stateTypeRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Invalid state type ID: " + id));

    model.addAttribute("stateType", stateType);
    return "returned-loan-state-types/edit";
  }

  @PostMapping("/update")
  public String updateStateType(
      @RequestParam Long id,
      @RequestParam String name,
      @RequestParam String description,
      RedirectAttributes redirectAttributes) {

    ReturnedLoanStateType stateType = stateTypeRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Invalid state type ID: " + id));

    stateType.setName(name);
    stateType.setDescription(description);

    stateTypeRepository.save(stateType);
    redirectAttributes.addFlashAttribute("success", "Return state type updated successfully.");

    return "redirect:/returned-loan-state-types";
  }

  @PostMapping("/delete")
  public String deleteStateType(@RequestParam Long id, RedirectAttributes redirectAttributes) {
    stateTypeRepository.deleteById(id);
    redirectAttributes.addFlashAttribute("success", "Return state type deleted successfully.");
    return "redirect:/returned-loan-state-types";
  }
}
