package mg.razherana.library.controllers.loans;

import mg.razherana.library.models.loans.People;
import mg.razherana.library.services.loans.PeopleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/peoples")
public class PeopleController {

  @Autowired
  private PeopleService peopleService;

  @GetMapping("")
  public String list(Model model) {
    List<People> peoples = peopleService.findAll();
    model.addAttribute("peoples", peoples);
    model.addAttribute("pageTitle", "People Management");
    return "peoples/list";
  }

  @GetMapping("/add")
  public String addForm(Model model) {
    model.addAttribute("pageTitle", "Add Person");
    return "peoples/create";
  }

  @PostMapping("/add")
  public String add(@RequestParam String name,
      @RequestParam LocalDate birthDate,
      @RequestParam String address) {

    peopleService.save(name, birthDate, address);
    return "redirect:/peoples";
  }
}
