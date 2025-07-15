package mg.razherana.library.controllers.loans;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import mg.razherana.library.dto.loans.LoanStatusHistoryDTO;
import mg.razherana.library.models.books.Book;
import mg.razherana.library.models.loans.ExtendLoan;
import mg.razherana.library.models.loans.Loan;
import mg.razherana.library.models.loans.LoanStatusHistory;
import mg.razherana.library.models.loans.LoanType;
import mg.razherana.library.models.loans.Membership;
import mg.razherana.library.models.loans.People;
import mg.razherana.library.models.loans.Reservation;
import mg.razherana.library.models.loans.ReturnedLoanState;
import mg.razherana.library.models.loans.ReturnedLoanStateType;
import mg.razherana.library.models.punishments.Punishment;
import mg.razherana.library.models.punishments.PunishmentType;
import mg.razherana.library.repositories.books.BookRepository;
import mg.razherana.library.repositories.loans.LoanTypeRepository;
import mg.razherana.library.services.books.ExemplaireService;
import mg.razherana.library.services.loans.ExtendLoanService;
import mg.razherana.library.services.loans.LoanService;
import mg.razherana.library.services.loans.LoanTypeService;
import mg.razherana.library.services.loans.PeopleService;
import mg.razherana.library.services.loans.ReservationService;
import mg.razherana.library.services.loans.ReturnedLoanStateService;
import mg.razherana.library.services.punishments.PunishmentService;
import mg.razherana.library.services.punishments.PunishmentTypeService;

@Controller
@RequestMapping("/loans")
public class LoanController {

  @Autowired
  private LoanService loanService;

  @Autowired
  private PeopleService peopleService;

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private LoanTypeRepository loanTypeRepository;

  @Autowired
  private ReturnedLoanStateService returnedLoanStateService;

  @Autowired
  private ReservationService reservationService;

  @Autowired
  private PunishmentTypeService punishmentTypeService;

  @Autowired
  private PunishmentService punishmentService;

  @Autowired
  private ExtendLoanService extendLoanService;

  @Autowired
  private LoanTypeService loanTypeService;

  @Autowired
  private ExemplaireService exemplaireService;

  @GetMapping
  public String listLoans(Model model) {
    List<Loan> loans = loanService.findAll();
    model.addAttribute("loans", loans);

    // Get return states
    List<ReturnedLoanStateType> returnStates = returnedLoanStateService.findAllStateTypes();
    model.addAttribute("returnStates", returnStates);

    // Get punishment types for late returns
    List<PunishmentType> punishmentTypes = punishmentTypeService.findAll();
    model.addAttribute("punishmentTypes", punishmentTypes);

    return "loans/list";
  }

  @GetMapping("/create")
  public String showCreateForm(Model model) {
    List<People> people = peopleService.findAll();
    List<Book> books = bookRepository.findAll();
    List<LoanType> loanTypes = loanTypeRepository.findAll();

    // Get counts of active and late loans for all memberships
    Map<Long, Long> activeLoanCounts = loanService.getActiveLoanCountsForAllMemberships();
    Map<Long, Long> lateLoanCounts = loanService.getLateLoanCountsForAllMemberships();

    // Get active punishments for all memberships
    Map<Long, Punishment> activePunishments = new HashMap<>();
    LocalDateTime now = LocalDateTime.now();
    for (People person : people) {
      for (Membership membership : person.getMemberships()) {
        Punishment activePunishment = punishmentService.getActivePunishmentAt(membership.getId(), now);
        if (activePunishment != null) {
          activePunishments.put(membership.getId(), activePunishment);
        }
      }
    }

    Set<Long> borrowedBookIds = loanService.getBorrowedBookIds();

    // Get available exemplaires count for each book
    Map<Long, Long> availableExemplairesCount = new HashMap<>();
    for (Book book : books) {
      availableExemplairesCount.put(book.getId(), exemplaireService.countAvailableByBookId(book.getId()));
    }

    model.addAttribute("borrowedBookIds", borrowedBookIds);
    model.addAttribute("people", people);
    model.addAttribute("books", books);
    model.addAttribute("loanTypes", loanTypes);
    model.addAttribute("activeLoanCounts", activeLoanCounts);
    model.addAttribute("lateLoanCounts", lateLoanCounts);
    model.addAttribute("activePunishments", activePunishments);
    model.addAttribute("availableExemplairesCount", availableExemplairesCount);

    return "loans/create";
  }

  @PostMapping("/create")
  public String createLoan(
      @RequestParam Long bookId,
      @RequestParam Long membershipId,
      @RequestParam Long loanTypeId,
      @RequestParam(name = "date") String dateString,
      Model model,
      RedirectAttributes redirectAttributes) {

    try {
      System.out.println(dateString);

      // Check if member has an active punishment
      LocalDateTime loanDate = dateString.isEmpty() ? LocalDateTime.now() : LocalDateTime.parse(dateString);

      if (punishmentService.hasPunishmentAt(membershipId, loanDate)) {
        Punishment activePunishment = punishmentService.getActivePunishmentAt(membershipId, loanDate);
        LocalDateTime endTime = activePunishment.getPunishmentDate()
            .plusSeconds((long) (activePunishment.getDurationHours() * 3600));

        redirectAttributes.addFlashAttribute("error",
            "This member has an active punishment until " +
                endTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")) +
                ". Reason: " + activePunishment.getDescription());
        return "redirect:/loans/create";
      }

      LoanType loanType = loanTypeService.findById(loanTypeId);

      if (loanType == null) {
        redirectAttributes.addFlashAttribute("error", "Invalid loan type");
        return "redirect:/loans/create";
      }

      // Check for pending reservations
      List<Reservation> pendingReservations = reservationService.findPendingReservationsForBook(bookId);

      Reservation reservation = pendingReservations.stream()
          .filter((res) -> (loanDate.isBefore(
              res.getReservationDate().plusHours(
                  loanType.getId() == 1 ? res.getMembership().getMembershipType().getMaxTimeHoursHome()
                      : res.getMembership().getMembershipType().getMaxTimeHoursLibrary()))
              || loanDate.isEqual(
                  res.getReservationDate().plusHours(
                      loanType.getId() == 1 ? res.getMembership().getMembershipType().getMaxTimeHoursHome()
                          : res.getMembership().getMembershipType().getMaxTimeHoursLibrary())))
              &&
              (loanDate.isAfter(res.getReservationDate())
                  ||
                  loanDate.isEqual(res.getReservationDate())))
          .findFirst().orElse(null);

      if (reservation != null) {
        // Add pending reservation to model and return to form
        model.addAttribute("pendingReservation", reservation);

        // Re-add form data
        List<People> people = peopleService.findAll();
        List<Book> books = bookRepository.findAll();
        List<LoanType> loanTypes = loanTypeRepository.findAll();
        Map<Long, Long> activeLoanCounts = loanService.getActiveLoanCountsForAllMemberships();
        Map<Long, Long> lateLoanCounts = loanService.getLateLoanCountsForAllMemberships();

        model.addAttribute("people", people);
        model.addAttribute("books", books);
        model.addAttribute("loanTypes", loanTypes);
        model.addAttribute("activeLoanCounts", activeLoanCounts);
        model.addAttribute("lateLoanCounts", lateLoanCounts);
        model.addAttribute("selectedBookId", bookId);
        model.addAttribute("selectedMembershipId", membershipId);
        model.addAttribute("selectedLoanTypeId", loanTypeId);

        return "loans/create";
      }

      // Create the loan
      loanService.createLoan(bookId, membershipId, loanTypeId, loanDate);
      redirectAttributes.addFlashAttribute("success", "Loan created successfully.");
      return "redirect:/loans";

    } catch (IllegalArgumentException e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "redirect:/loans/create";
    }
  }

  @GetMapping("/{id}")
  public String viewLoan(@PathVariable Long id, Model model) {
    Loan loan = loanService.findById(id);
    if (loan == null) {
      return "redirect:/loans";
    }

    List<LoanStatusHistory> statusHistory = loanService.getStatusHistory(id);
    ReturnedLoanState returnState = returnedLoanStateService.findByLoanId(id);
    List<ExtendLoan> extensions = extendLoanService.findByLoanId(id);

    model.addAttribute("loan", loan);
    model.addAttribute("statusHistory", statusHistory);
    model.addAttribute("returnState", returnState);
    model.addAttribute("extensions", extensions);
    model.addAttribute("now", LocalDateTime.now());

    return "loans/view";
  }

  @PostMapping("/return")
  public String returnLoan(
      @RequestParam Long loanId,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime returnDate,
      @RequestParam(required = false) Long returnStateTypeId,
      @RequestParam(required = false) String notes,
      RedirectAttributes redirectAttributes) {

    try {
      // Return the loan
      loanService.returnLoan(loanId, returnDate);

      // Record return state if provided
      if (returnStateTypeId != null) {
        returnedLoanStateService.recordReturnState(loanId, returnStateTypeId, notes);
      }

      redirectAttributes.addFlashAttribute("success", "Book marked as returned successfully.");
    } catch (IllegalArgumentException e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
    }

    return "redirect:/loans";
  }

  @GetMapping("/status-history/{loanId}")
  @ResponseBody
  public List<LoanStatusHistoryDTO> getLoanStatusHistory(@PathVariable Long loanId) {
    List<LoanStatusHistory> history = loanService.getStatusHistory(loanId);

    // Convert to DTO
    return history.stream()
        .map(status -> new LoanStatusHistoryDTO(
            status.getLoanStatusType().getName(),
            status.getStatusDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))))
        .collect(Collectors.toList());
  }

  @GetMapping("/get-loan-data/{loanId}")
  @ResponseBody
  public Map<String, Object> getLoanData(@PathVariable Long loanId) {
    Loan loan = loanService.findById(loanId);
    if (loan == null) {
      throw new IllegalArgumentException("Loan not found with ID: " + loanId);
    }

    Map<String, Object> result = new HashMap<>();
    result.put("loanId", loan.getId());
    result.put("loanDate", loan.getLoanDate());
    result.put("bookTitle", loan.getExemplaire().getBook().getTitle());
    result.put("exemplaireCode", loan.getExemplaire().getCode());
    result.put("membershipId", loan.getMembership().getId());
    result.put("isHomeLoan", loan.getLoanType().getName().toLowerCase().contains("home"));

    // Get max hours from membership type
    // boolean isHomeLoan =
    // loan.getLoanType().getName().toLowerCase().contains("home");

    // int maxHours = isHomeLoan
    // ? loan.getMembership().getMembershipType().getMaxTimeHoursHome()
    // : loan.getMembership().getMembershipType().getMaxTimeHoursLibrary();

    result.put("maxTimeHoursHome", loan.getMembership().getMembershipType().getMaxTimeHoursHome());
    result.put("maxTimeHoursLibrary", loan.getMembership().getMembershipType().getMaxTimeHoursLibrary());

    return result;
  }
}
