package mg.razherana.library.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;

import mg.razherana.library.repositories.books.BookRepository;
import mg.razherana.library.repositories.loans.LoanRepository;
import mg.razherana.library.repositories.loans.MembershipRepository;
import mg.razherana.library.repositories.loans.ReservationRepository;
import mg.razherana.library.models.loans.Loan;

@Controller
@RequestMapping("/")
public class HomeController {

  private final MembershipRepository membershipRepository;

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private LoanRepository loanRepository;

  @Autowired
  private ReservationRepository reservationRepository;

  HomeController(MembershipRepository membershipRepository) {
    this.membershipRepository = membershipRepository;
  }

  @GetMapping({ "", "home" })
  @Transactional
  public String home(Model model, @RequestParam(defaultValue = "", required = false) String dateStringReference) {
    model.addAttribute("pageTitle", "Library Management System");
    model.addAttribute("welcomeMessage", "Welcome to our Library Management System");

    LocalDate dateReference = null;

    if (dateStringReference.isEmpty())
      dateReference = LocalDate.now();
    else
      dateReference = LocalDate.parse(dateStringReference);

    // Add statistics to the model
    model.addAttribute("totalBooks", bookRepository.count());
    model.addAttribute("totalMembers", membershipRepository.countWhereMembershipValid(dateReference));
    model.addAttribute("activeLoans", loanRepository.countByReturnedFalse());
    model.addAttribute("pendingReservations", reservationRepository.count());

    // Add data for weekly loans chart
    LocalDate today = LocalDate.now();
    LocalDate oneWeekAgo = today.minusDays(6);

    List<String> lastWeekDays = new ArrayList<>();
    List<Long> loanCounts = new ArrayList<>();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE");
    for (int i = 0; i < 7; i++) {
      LocalDate date = oneWeekAgo.plusDays(i);
      lastWeekDays.add(date.format(formatter));
      loanCounts.add(loanRepository.countByLoanDateEquals(LocalDateTime.of(date, LocalTime.of(0, 0))));
    }

    model.addAttribute("lastWeekDays", lastWeekDays);
    model.addAttribute("loanCounts", loanCounts);

    // Add data for active vs inactive memberships chart
    long activeMembers = membershipRepository.countWhereMembershipValid(dateReference);
    long inactiveMembers = membershipRepository.count() - activeMembers;

    model.addAttribute("activeMembers", activeMembers);
    model.addAttribute("inactiveMembers", inactiveMembers);

    // Add latest loans and returns
    List<Loan> latestLoans = loanRepository.findTop5ByOrderByLoanDateDesc();
    List<Loan> latestReturns = loanRepository.findTop5ByReturnedTrueOrderByReturnDateDesc();

    model.addAttribute("latestLoans", latestLoans);
    model.addAttribute("latestReturns", latestReturns);

    return "home";
  }
}
