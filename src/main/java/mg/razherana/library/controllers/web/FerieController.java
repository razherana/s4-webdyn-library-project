package mg.razherana.library.controllers.web;

import mg.razherana.library.models.feries.Ferie;
import mg.razherana.library.services.feries.FerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/feries")
public class FerieController {

  @Autowired
  private FerieService ferieService;

  @GetMapping
  public String listFeries(Model model) {
    List<Ferie> feries = ferieService.getAllFeries();
    model.addAttribute("feries", feries);
    return "feries/list";
  }

  @GetMapping("/create")
  public String createFerieForm(Model model) {
    model.addAttribute("ferie", new Ferie());
    return "feries/create";
  }

  @PostMapping("/create")
  public String createFerie(@ModelAttribute Ferie ferie, RedirectAttributes redirectAttributes) {
    try {
      ferieService.saveFerie(ferie);
      redirectAttributes.addFlashAttribute("success", "Ferie created successfully!");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", "Failed to create ferie: " + e.getMessage());
    }
    return "redirect:/feries";
  }

  @GetMapping("/{id}/edit")
  public String editFerieForm(@PathVariable Long id, Model model) {
    Ferie ferie = ferieService.getFerieById(id);
    if (ferie == null) {
      return "redirect:/feries";
    }
    model.addAttribute("ferie", ferie);
    return "feries/edit";
  }

  @PostMapping("/{id}/edit")
  public String editFerie(@PathVariable Long id, @ModelAttribute Ferie ferie, RedirectAttributes redirectAttributes) {
    try {
      ferie.setId(id);
      ferieService.saveFerie(ferie);
      redirectAttributes.addFlashAttribute("success", "Ferie updated successfully!");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", "Failed to update ferie: " + e.getMessage());
    }
    return "redirect:/feries";
  }

  @PostMapping("/{id}/delete")
  public String deleteFerie(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    try {
      ferieService.deleteFerie(id);
      redirectAttributes.addFlashAttribute("success", "Ferie deleted successfully!");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", "Failed to delete ferie: " + e.getMessage());
    }
    return "redirect:/feries";
  }

  @GetMapping("/check-availability")
  @ResponseBody
  public String checkAvailability() {
    if (ferieService.canMakeLoansOrReservations()) {
      return "Loans and reservations are available today.";
    } else {
      LocalDate nextAvailable = ferieService.getNextAvailableBusinessDay();
      return "Today is a holiday. Next available day for loans/reservations: " + nextAvailable;
    }
  }
}
