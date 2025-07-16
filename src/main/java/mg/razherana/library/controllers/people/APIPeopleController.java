package mg.razherana.library.controllers.people;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import mg.razherana.library.models.loans.People;
import mg.razherana.library.services.loans.PeopleService;

@Controller
@RequestMapping("/people-api")
public class APIPeopleController {

    @Autowired
    private PeopleService peopleService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("pageTitle", "People Management");
        return "people/list";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        People person = peopleService.findById(id);
        if (person == null) {
            return "redirect:/people";
        }
        
        model.addAttribute("person", person);
        model.addAttribute("pageTitle", "Person Details - " + person.getName());
        return "people/details";
    }
}
