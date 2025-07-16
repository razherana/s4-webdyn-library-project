package mg.razherana.library.controllers.loans;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import mg.razherana.library.models.books.Book;
import mg.razherana.library.models.loans.Membership;
import mg.razherana.library.models.loans.Reservation;
import mg.razherana.library.models.loans.ReservationStatusType;
import mg.razherana.library.models.loans.ReservationStatusHistory;
import mg.razherana.library.models.punishments.Punishment;
import mg.razherana.library.services.books.BookService;
import mg.razherana.library.services.books.ExemplaireService;
import mg.razherana.library.services.feries.FerieService;
import mg.razherana.library.services.loans.LoanService;
import mg.razherana.library.services.loans.MembershipService;
import mg.razherana.library.services.loans.ReservationService;
import mg.razherana.library.services.punishments.PunishmentService;
import mg.razherana.library.repositories.loans.ReservationStatusTypeRepository;

@Controller
@RequestMapping("/reservations")
public class ReservationController {

  @Autowired
  private ReservationService reservationService;

  @Autowired
  private BookService bookService;

  @Autowired
  private ExemplaireService exemplaireService;

  @Autowired
  private MembershipService membershipService;

  @Autowired
  private ReservationStatusTypeRepository statusTypeRepository;

  @Autowired
  private PunishmentService punishmentService;

  @Autowired
  private LoanService loanService;

  @Autowired
  private FerieService ferieService;

  @GetMapping("")
  public String list(Model model) {
    List<Reservation> reservations = reservationService.findAll();
    List<ReservationStatusType> statusTypes = statusTypeRepository.findAll();

    model.addAttribute("reservations", reservations);
    model.addAttribute("statusTypes", statusTypes);
    model.addAttribute("today", LocalDateTime.now());

    return "reservations/list";
  }

  @GetMapping("/add")
  public String createForm(Model model) {
    List<Book> books = bookService.findAll();
    List<Membership> activeMemberships = membershipService.findActiveMemberships(LocalDate.now());
    List<ReservationStatusType> statusTypes = statusTypeRepository.findAll();

    model.addAttribute("books", books);
    model.addAttribute("memberships", activeMemberships);
    model.addAttribute("statusTypes", statusTypes);
    model.addAttribute("exemplaireService", exemplaireService);
    model.addAttribute("today", LocalDate.now());

    return "reservations/create";
  }

  @PostMapping("/add")
  public String create(
      @RequestParam Long bookId,
      @RequestParam Long membershipId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate reservationDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime reservationTime,
      @RequestParam(defaultValue = "false") boolean takeHome,
      RedirectAttributes redirectAttributes) {

    try {
      LocalDateTime reservationDateTime = LocalDateTime.of(reservationDate, reservationTime);

      // Don't allow reservations in the past
      if (reservationDateTime.isBefore(LocalDateTime.now())) {
        redirectAttributes.addFlashAttribute("error", "Cannot make reservations in the past");
        return "redirect:/reservations/add";
      }

      // Check if member has an active punishment at the reservation date
      if (punishmentService.hasPunishmentAt(membershipId, reservationDateTime)) {
        Punishment activePunishment = punishmentService.getActivePunishmentAt(membershipId, reservationDateTime);
        LocalDateTime endTime = activePunishment.getPunishmentDate()
            .plusSeconds((long) (activePunishment.getDurationHours() * 3600));

        redirectAttributes.addFlashAttribute("error",
            "This member has a punishment active at the requested reservation time (until " +
                endTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")) +
                "). Reason: " + activePunishment.getDescription());
        return "redirect:/reservations/add";
      }

      if(!ferieService.canMakeLoansOrReservations(reservationDate)){
        LocalDate nextAvailable = ferieService.getNextAvailableBusinessDay(reservationDate);
        redirectAttributes.addFlashAttribute("error",
            "Cannot create reservations on holidays. Next available date: " + nextAvailable);
        return "redirect:/loans/create";
      }

      reservationService.createReservation(bookId, membershipId, reservationDateTime, takeHome);
      redirectAttributes.addFlashAttribute("success", "Reservation created successfully");
      return "redirect:/reservations";

    } catch (IllegalArgumentException e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "redirect:/reservations/add";
    }
  }

  @GetMapping("/status-history/{id}")
  @ResponseBody
  public ResponseEntity<List<Map<String, String>>> getStatusHistory(@PathVariable Long id) {
    List<ReservationStatusHistory> history = reservationService.getStatusHistory(id);

    List<Map<String, String>> response = history.stream()
        .map(status -> {
          Map<String, String> statusMap = new HashMap<>();
          statusMap.put("statusType", status.getReservationStatusType().getName());
          statusMap.put("date", status.getStatusDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
          return statusMap;
        })
        .collect(Collectors.toList());

    return ResponseEntity.ok(response);
  }

  @PostMapping("/update-status")
  public String updateStatus(
      @RequestParam Long reservationId,
      @RequestParam Long statusTypeId,
      RedirectAttributes redirectAttributes) {

    try {
      reservationService.updateStatus(reservationId, statusTypeId);
      redirectAttributes.addFlashAttribute("success", "Reservation status updated successfully");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", "Error updating status: " + e.getMessage());
    }

    return "redirect:/reservations";
  }

  @PostMapping("/cancel-and-create-loan")
  public String cancelReservationAndCreateLoan(
      @RequestParam Long reservationId,
      @RequestParam Long loanTypeId,
      RedirectAttributes redirectAttributes) {

    try {
      // Get the reservation to extract exemplaire and membership info
      Reservation reservation = reservationService.findById(reservationId);
      if (reservation == null) {
        throw new IllegalArgumentException("Reservation not found");
      }

      // First, cancel the reservation by updating its status to "Cancelled"
      ReservationStatusType cancelledType = statusTypeRepository.findByNameContainingIgnoreCase("cancel");
      if (cancelledType == null) {
        throw new IllegalArgumentException("Cancelled status type not found");
      }

      // Update the reservation status
      reservationService.updateStatus(reservationId, cancelledType.getId());

      // Create the loan using the exemplaire from the reservation
      loanService.createLoan(reservation.getExemplaire().getId(), reservation.getMembership().getId(), loanTypeId, LocalDateTime.now());

      redirectAttributes.addFlashAttribute("success",
          "Reservation cancelled and loan created successfully.");
      return "redirect:/loans";
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", "Error processing request: " + e.getMessage());
      return "redirect:/reservations";
    }
  }

}
