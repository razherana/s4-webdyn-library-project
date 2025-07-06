package mg.razherana.library.controllers.users;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequestMapping("/user/home")
public class UserHomeController {
  
  @GetMapping()
  public String home() {
    return "users/home";
  }
}
