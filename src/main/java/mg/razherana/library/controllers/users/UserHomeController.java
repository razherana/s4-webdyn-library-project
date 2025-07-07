package mg.razherana.library.controllers.users;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import mg.razherana.library.models.loans.Loan;
import mg.razherana.library.models.loans.Membership;
import mg.razherana.library.models.loans.Reservation;
import mg.razherana.library.models.punishments.Punishment;
import mg.razherana.library.models.users.User;
import mg.razherana.library.services.UserService;
import mg.razherana.library.services.loans.LoanService;
import mg.razherana.library.services.loans.MembershipService;
import mg.razherana.library.services.loans.ReservationService;
import mg.razherana.library.services.punishments.PunishmentService;

@Controller
@RequestMapping("/user/home")
public class UserHomeController {

  @Autowired
  private UserService userService;

  @Autowired
  private MembershipService membershipService;

  @Autowired
  private LoanService loanService;

  @Autowired
  private ReservationService reservationService;

  @Autowired
  private PunishmentService punishmentService;

  @GetMapping()
  public String home(HttpSession session, Model model) {
    // Get current user
    User currentUser = userService.getCurrentUser(session);
    if (currentUser == null || !currentUser.isPeople()) {
      return "redirect:/auth/login";
    }

    // Get user's memberships
    List<Membership> memberships = membershipService.findByPeople(currentUser.getPeople());
    if (memberships.isEmpty()) {
      model.addAttribute("error", "No active membership found for your account.");
      return "users/home";
    }

    // Get primary membership (first active one)
    Membership primaryMembership = memberships.stream()
        .filter(m -> m.getEndDate().isAfter(java.time.LocalDate.now()))
        .findFirst()
        .orElse(memberships.get(0));

    // Get loans statistics
    List<Loan> allLoans = loanService.findByMembershipId(primaryMembership.getId());
    long totalLoans = allLoans.size();
    long activeLoans = allLoans.stream().filter(loan -> loan.getReturnDate() == null).count();
    long overdueLoans = allLoans.stream()
        .filter(loan -> loan.getReturnDate() == null && loan.checkLate(LocalDateTime.now()))
        .count();

    // Get reservations
    List<Reservation> reservations = reservationService.findByMembershipId(primaryMembership.getId());
    long activeReservations = reservations.stream()
        .filter(r -> r.getReservationStatusHistories().get(0).getReservationStatusType().getName().toLowerCase()
            .contains("pending")
            || r.getReservationStatusHistories().get(0).getReservationStatusType().getName().toLowerCase()
                .contains("approved"))
        .filter(
            r -> r.getReservationDate().isAfter(
                LocalDateTime.now().minusDays(30)))
        .count();

    // Check for active punishments
    Punishment activePunishment = punishmentService.getActivePunishmentAt(primaryMembership.getId(),
        LocalDateTime.now());

    // Get recent loans and reservations
    List<Loan> recentLoans = loanService.findRecentByMembershipId(primaryMembership.getId(), 5);
    List<Reservation> recentReservations = reservationService.findRecentByMembershipId(primaryMembership.getId(), 5);

    // Add all data to model
    model.addAttribute("primaryMembership", primaryMembership);
    model.addAttribute("totalLoans", totalLoans);
    model.addAttribute("activeLoans", activeLoans);
    model.addAttribute("overdueLoans", overdueLoans);
    model.addAttribute("activeReservations", activeReservations);
    model.addAttribute("activePunishment", activePunishment);
    model.addAttribute("recentLoans", recentLoans);
    model.addAttribute("recentReservations", recentReservations);

    return "users/home";
  }
}
