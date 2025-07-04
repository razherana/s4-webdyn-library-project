package mg.razherana.library.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

import mg.razherana.library.repositories.books.BookRepository;
import mg.razherana.library.repositories.loans.LoanRepository;
import mg.razherana.library.repositories.loans.PeopleRepository;
import mg.razherana.library.repositories.loans.ReservationRepository;

@Controller
@RequestMapping("/")
public class HomeController {

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private PeopleRepository peopleRepository;

  @Autowired
  private LoanRepository loanRepository;

  @Autowired
  private ReservationRepository reservationRepository;

  @GetMapping({ "", "home" })
  public String home(Model model) {
    model.addAttribute("pageTitle", "Library Management System");
    model.addAttribute("welcomeMessage", "Welcome to our Library Management System");

    // Add statistics to the model
    model.addAttribute("totalBooks", bookRepository.count());
    model.addAttribute("totalMembers", peopleRepository.count());
    model.addAttribute("activeLoans", loanRepository.countByReturnedFalse());
    model.addAttribute("pendingReservations", reservationRepository.count());

    return "home";
  }
}
