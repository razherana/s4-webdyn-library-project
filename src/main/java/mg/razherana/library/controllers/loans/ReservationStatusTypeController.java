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

import mg.razherana.library.models.loans.ReservationStatusType;
import mg.razherana.library.services.loans.ReservationStatusTypeService;

@Controller
@RequestMapping("/reservation-status-types")
public class ReservationStatusTypeController {

  @Autowired
  private ReservationStatusTypeService statusTypeService;

  @GetMapping("")
  public String list(Model model) {
    List<ReservationStatusType> statusTypes = statusTypeService.findAll();
    model.addAttribute("statusTypes", statusTypes);
    return "reservation-status-types/list";
  }

  @GetMapping("/add")
  public String showAddForm(Model model) {
    return "reservation-status-types/create";
  }

  @PostMapping("/add")
  public String add(@RequestParam String name, RedirectAttributes redirectAttributes) {
    try {
      statusTypeService.save(name);
      redirectAttributes.addFlashAttribute("success", "Status type added successfully");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", "Error adding status type: " + e.getMessage());
    }
    return "redirect:/reservation-status-types";
  }

  @GetMapping("/edit/{id}")
  public String showEditForm(@PathVariable Long id, Model model) {
    ReservationStatusType statusType = statusTypeService.findById(id);
    if (statusType == null) {
      return "redirect:/reservation-status-types";
    }
    model.addAttribute("statusType", statusType);
    return "reservation-status-types/edit";
  }

  @PostMapping("/update")
  public String update(@RequestParam Long id, @RequestParam String name, RedirectAttributes redirectAttributes) {
    try {
      statusTypeService.update(id, name);
      redirectAttributes.addFlashAttribute("success", "Status type updated successfully");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", "Error updating status type: " + e.getMessage());
    }
    return "redirect:/reservation-status-types";
  }

  @PostMapping("/delete")
  public String delete(@RequestParam Long id, RedirectAttributes redirectAttributes) {
    try {
      statusTypeService.delete(id);
      redirectAttributes.addFlashAttribute("success", "Status type deleted successfully");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", "Error deleting status type: " + e.getMessage());
    }
    return "redirect:/reservation-status-types";
  }
}
