package mg.razherana.library.controllers.punishments;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import mg.razherana.library.models.loans.Membership;
import mg.razherana.library.models.punishments.Punishment;
import mg.razherana.library.models.punishments.PunishmentType;
import mg.razherana.library.services.loans.MembershipService;
import mg.razherana.library.services.punishments.PunishmentService;
import mg.razherana.library.services.punishments.PunishmentTypeService;

@Controller
@RequestMapping("/punishments")
public class PunishmentController {

  @Autowired
  private PunishmentService punishmentService;

  @Autowired
  private PunishmentTypeService punishmentTypeService;

  @Autowired
  private MembershipService membershipService;

  @GetMapping
  public String list(Model model) {
    List<Punishment> punishments = punishmentService.findAll();
    model.addAttribute("punishments", punishments);
    return "punishments/list";
  }

  @GetMapping("/add")
  public String showAddForm(Model model) {
    List<PunishmentType> punishmentTypes = punishmentTypeService.findAll();
    List<Membership> memberships = membershipService.findAll();

    model.addAttribute("punishmentTypes", punishmentTypes);
    model.addAttribute("memberships", memberships);
    model.addAttribute("now", LocalDateTime.now());
    return "punishments/create";
  }

  @PostMapping("/add")
  public String add(
      @RequestParam Long membershipId,
      @RequestParam Long punishmentTypeId,
      @RequestParam float durationHours,
      @RequestParam String description,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime punishmentDate,
      RedirectAttributes redirectAttributes) {

    try {
      punishmentService.createPunishment(membershipId, punishmentTypeId, durationHours, description, punishmentDate);
      redirectAttributes.addFlashAttribute("success", "Punishment added successfully");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "redirect:/punishments/add";
    }
    return "redirect:/punishments";
  }

  @GetMapping("/edit/{id}")
  public String showEditForm(@PathVariable Long id, Model model) {
    try {
      Punishment punishment = punishmentService.findById(id);
      List<PunishmentType> punishmentTypes = punishmentTypeService.findAll();

      model.addAttribute("punishment", punishment);
      model.addAttribute("punishmentTypes", punishmentTypes);
      return "punishments/edit";
    } catch (Exception e) {
      return "redirect:/punishments";
    }
  }

  @PostMapping("/update")
  public String update(
      @RequestParam Long id,
      @RequestParam Long punishmentTypeId,
      @RequestParam float durationHours,
      @RequestParam String description,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime punishmentDate,
      RedirectAttributes redirectAttributes) {

    try {
      punishmentService.updatePunishment(id, punishmentTypeId, durationHours, description, punishmentDate);
      redirectAttributes.addFlashAttribute("success", "Punishment updated successfully");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
    }
    return "redirect:/punishments";
  }

  @PostMapping("/delete")
  public String delete(@RequestParam Long id, RedirectAttributes redirectAttributes) {
    try {
      punishmentService.deletePunishment(id);
      redirectAttributes.addFlashAttribute("success", "Punishment deleted successfully");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
    }
    return "redirect:/punishments";
  }

  // API endpoint for creating punishments from loan return
  @PostMapping("/api/create")
  @ResponseBody
  public ResponseEntity<Map<String, Object>> apiCreatePunishment(@RequestBody Map<String, Object> payload) {
    try {
      Long membershipId = Long.valueOf(payload.get("membershipId").toString());
      Long punishmentTypeId = Long.valueOf(payload.get("punishmentTypeId").toString());
      float durationHours = Float.parseFloat(payload.get("durationHours").toString());
      String description = (String) payload.get("description");

      // Use current date if not provided
      LocalDateTime punishmentDate = LocalDateTime.now();
      if (payload.containsKey("punishmentDate") && payload.get("punishmentDate") != null) {
        punishmentDate = LocalDateTime.parse(payload.get("punishmentDate").toString());
      }

      Punishment punishment = punishmentService.createPunishment(
          membershipId, punishmentTypeId, durationHours, description, punishmentDate);

      Map<String, Object> response = new HashMap<>();
      response.put("success", true);
      response.put("id", punishment.getId());

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      Map<String, Object> error = new HashMap<>();
      error.put("success", false);
      error.put("message", e.getMessage());

      return ResponseEntity.badRequest().body(error);
    }
  }
}
