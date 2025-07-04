package mg.razherana.library.controllers.punishments;

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

import mg.razherana.library.models.punishments.PunishmentType;
import mg.razherana.library.services.punishments.PunishmentTypeService;

@Controller
@RequestMapping("/punishment-types")
public class PunishmentTypeController {

  @Autowired
  private PunishmentTypeService punishmentTypeService;

  @GetMapping
  public String list(Model model) {
    List<PunishmentType> punishmentTypes = punishmentTypeService.findAll();
    model.addAttribute("punishmentTypes", punishmentTypes);
    return "punishment-types/list";
  }

  @GetMapping("/add")
  public String showAddForm() {
    return "punishment-types/create";
  }

  @PostMapping("/add")
  public String add(@RequestParam String name, RedirectAttributes redirectAttributes) {
    try {
      punishmentTypeService.save(name);
      redirectAttributes.addFlashAttribute("success", "Punishment type added successfully");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "redirect:/punishment-types/add";
    }
    return "redirect:/punishment-types";
  }

  @GetMapping("/edit/{id}")
  public String showEditForm(@PathVariable Long id, Model model) {
    try {
      PunishmentType punishmentType = punishmentTypeService.findById(id);
      model.addAttribute("punishmentType", punishmentType);
      return "punishment-types/edit";
    } catch (Exception e) {
      return "redirect:/punishment-types";
    }
  }

  @PostMapping("/update")
  public String update(@RequestParam Long id, @RequestParam String name, RedirectAttributes redirectAttributes) {
    try {
      punishmentTypeService.update(id, name);
      redirectAttributes.addFlashAttribute("success", "Punishment type updated successfully");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
    }
    return "redirect:/punishment-types";
  }

  @PostMapping("/delete")
  public String delete(@RequestParam Long id, RedirectAttributes redirectAttributes) {
    try {
      punishmentTypeService.delete(id);
      redirectAttributes.addFlashAttribute("success", "Punishment type deleted successfully");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
    }
    return "redirect:/punishment-types";
  }
}
