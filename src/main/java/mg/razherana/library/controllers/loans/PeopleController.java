package mg.razherana.library.controllers.loans;

import mg.razherana.library.models.loans.People;
import mg.razherana.library.models.users.Role;
import mg.razherana.library.services.RoleService;
import mg.razherana.library.services.UserService;
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

  private final UserService userService;

  @Autowired
  private PeopleService peopleService;

  @Autowired
  private RoleService roleService;

  PeopleController(UserService userService) {
    this.userService = userService;
  }

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

    People people = peopleService.save(name, birthDate, address);
    if (people == null) {
      return "redirect:/peoples?error=Person already exists";
    }

    Role peopleRole = roleService.findByName("people");
    if (peopleRole == null) {
      return "redirect:/peoples?error=Role 'people' not found";
    }

    userService.save(people.getName().replaceAll(" ", "_").toLowerCase(),
        "password",
        List.of(peopleRole.getId()), people.getId());

    return "redirect:/peoples";
  }
}
