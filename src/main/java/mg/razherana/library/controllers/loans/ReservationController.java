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
import mg.razherana.library.services.books.BookService;
import mg.razherana.library.services.loans.MembershipService;
import mg.razherana.library.services.loans.ReservationService;
import mg.razherana.library.repositories.loans.ReservationStatusTypeRepository;

@Controller
@RequestMapping("/reservations")
public class ReservationController {

  @Autowired
  private ReservationService reservationService;

  @Autowired
  private BookService bookService;

  @Autowired
  private MembershipService membershipService;

  @Autowired
  private ReservationStatusTypeRepository statusTypeRepository;

  @GetMapping("")
  public String list(Model model) {
    // Initial load without filters
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
    model.addAttribute("today", LocalDate.now());
    model.addAttribute("minTime", LocalTime.now().plusHours(1).toString());

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
}
