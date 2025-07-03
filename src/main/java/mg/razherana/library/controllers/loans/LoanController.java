package mg.razherana.library.controllers.loans;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

import lombok.Data;
import mg.razherana.library.models.books.Book;
import mg.razherana.library.models.loans.Loan;
import mg.razherana.library.models.loans.LoanStatusHistory;
import mg.razherana.library.models.loans.LoanType;
import mg.razherana.library.models.loans.People;
import mg.razherana.library.models.loans.Reservation;
import mg.razherana.library.models.loans.ReturnedLoanState;
import mg.razherana.library.models.loans.ReturnedLoanStateType;
import mg.razherana.library.repositories.books.BookRepository;
import mg.razherana.library.repositories.loans.LoanTypeRepository;
import mg.razherana.library.services.loans.LoanService;
import mg.razherana.library.services.loans.PeopleService;
import mg.razherana.library.services.loans.ReservationService;
import mg.razherana.library.services.loans.ReturnedLoanStateService;

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

  @GetMapping
  public String listLoans(Model model) {
    List<Loan> loans = loanService.findAll();
    model.addAttribute("loans", loans);

    // Get return states

    List<ReturnedLoanStateType> returnStates = returnedLoanStateService.findAllStateTypes();
    model.addAttribute("returnStates", returnStates);

    System.out.println("Returns = " + returnStates);

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

    Set<Long> borrowedBookIds = loanService.getBorrowedBookIds();

    model.addAttribute("borrowedBookIds", borrowedBookIds);
    model.addAttribute("people", people);
    model.addAttribute("books", books);
    model.addAttribute("loanTypes", loanTypes);
    model.addAttribute("activeLoanCounts", activeLoanCounts);
    model.addAttribute("lateLoanCounts", lateLoanCounts);

    return "loans/create";
  }

  @PostMapping("/create")
  public String createLoan(
      @RequestParam Long bookId,
      @RequestParam Long membershipId,
      @RequestParam Long loanTypeId,
      Model model,
      RedirectAttributes redirectAttributes) {

    try {
      // Check for pending reservations
      List<Reservation> pendingReservations = reservationService.findPendingReservationsForBook(bookId);
      if (!pendingReservations.isEmpty()) {
        // Add pending reservation to model and return to form
        model.addAttribute("pendingReservation", pendingReservations.get(0));

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
      loanService.createLoan(bookId, membershipId, loanTypeId);
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

    model.addAttribute("loan", loan);
    model.addAttribute("statusHistory", statusHistory);
    model.addAttribute("returnState", returnState);

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

  // DTO for status history
  @Data
  private static class LoanStatusHistoryDTO {
    private String statusType;
    private String date;

    public LoanStatusHistoryDTO(String statusType, String date) {
      this.statusType = statusType;
      this.date = date;
    }
  }
}
