package mg.razherana.library.services.loans;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mg.razherana.library.models.books.Exemplaire;
import mg.razherana.library.models.loans.Loan;
import mg.razherana.library.models.loans.LoanStatusHistory;
import mg.razherana.library.models.loans.LoanStatusType;
import mg.razherana.library.models.loans.LoanType;
import mg.razherana.library.models.loans.Membership;
import mg.razherana.library.models.loans.MembershipType;
import mg.razherana.library.models.loans.Reservation;
import mg.razherana.library.repositories.books.BookRepository;
import mg.razherana.library.repositories.books.ExemplaireRepository;
import mg.razherana.library.repositories.loans.LoanRepository;
import mg.razherana.library.repositories.loans.LoanStatusHistoryRepository;
import mg.razherana.library.repositories.loans.LoanStatusTypeRepository;
import mg.razherana.library.repositories.loans.LoanTypeRepository;
import mg.razherana.library.repositories.loans.MembershipRepository;

@Service
public class LoanService {

  @Autowired
  private LoanRepository loanRepository;

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private ExemplaireRepository exemplaireRepository;

  @Autowired
  private MembershipRepository membershipRepository;

  @Autowired
  private LoanTypeRepository loanTypeRepository;

  @Autowired
  private LoanStatusTypeRepository statusTypeRepository;

  @Autowired
  private LoanStatusHistoryRepository statusHistoryRepository;

  @Autowired
  private ReservationService reservationService;

  public List<Loan> findAll() {
    return loanRepository.findAll();
  }

  public Loan findById(Long id) {
    return loanRepository.findById(id).orElse(null);
  }

  public List<Loan> search(String keyword) {
    if (keyword == null || keyword.trim().isEmpty()) {
      return findAll();
    }
    return loanRepository.findByTitleOrAuthorContaining(keyword.trim());
  }

  public boolean isBookAvailable(Long bookId) {
    return exemplaireRepository.countAvailableByBookId(bookId) > 0;
  }

  public Set<Long> getAvailableBookIds() {
    // Get all available exemplaires
    List<Exemplaire> availableExemplaires = exemplaireRepository.findAvailableExemplaires();

    // Return the unique book IDs from available exemplaires
    return availableExemplaires.stream()
        .map(exemplaire -> exemplaire.getBook().getId())
        .collect(Collectors.toSet());
  }

  public Set<Long> getBorrowedBookIds() {
    // We find from available exemplaires
    Set<Long> availableBookIds = getAvailableBookIds();

    // Get all book ids
    Set<Long> allBookIds = bookRepository.findAll()
        .stream()
        .map(book -> book.getId())
        .collect(Collectors.toSet());

    // Return the difference between all book ids and available book ids
    return allBookIds.stream()
        .filter(bookId -> !availableBookIds.contains(bookId))
        .collect(Collectors.toSet());
  }

  @Transactional
  public Loan createLoan(Long bookId, Long membershipId, Long loanTypeId, LocalDateTime loanDate) throws IllegalArgumentException {
    // Check if book exists
    if (!bookRepository.existsById(bookId)) {
      throw new IllegalArgumentException("Book not found");
    }

    // Check if membership exists and is active
    Membership membership = membershipRepository.findById(membershipId)
        .orElseThrow(() -> new IllegalArgumentException("Membership not found"));

    if (membership.getEndDate().isBefore(LocalDateTime.now().toLocalDate())) {
      throw new IllegalArgumentException("Membership has expired");
    }

    // Check if loan type exists
    LoanType loanType = loanTypeRepository.findById(loanTypeId)
        .orElseThrow(() -> new IllegalArgumentException("Loan type not found"));

    // Find an available exemplaire for the book
    List<Exemplaire> availableExemplaires = exemplaireRepository.findAvailableByBookId(bookId);
    if (availableExemplaires.isEmpty()) {
      throw new IllegalArgumentException("No available exemplaires for this book");
    }

    // Get the first available exemplaire
    Exemplaire exemplaire = availableExemplaires.get(0);

    // Check for late loans
    if (loanRepository.countLateByMembershipId(membershipId) > 0) {
      throw new IllegalArgumentException("This member has late loans and cannot borrow more books");
    }

    // Check membership limit based on loan type
    MembershipType membershipType = membership.getMembershipType();
    boolean isTakeHome = loanType.getName().toLowerCase().contains("home");

    long activeLoans = loanRepository.countActiveByMembershipIdAndType(membershipId, isTakeHome);
    int maxAllowed = isTakeHome ? membershipType.getMaxBooksAllowedHome() : membershipType.getMaxBooksAllowedLibrary();

    if (activeLoans >= maxAllowed) {
      throw new IllegalArgumentException(
          "Membership has reached maximum allowed books (" + maxAllowed + ") for " +
              (isTakeHome ? "home" : "library") + " loans");
    }

    // Check for confirmed reservations on the exemplaire
    List<Reservation> confirmedReservations = reservationService.findConfirmedReservationsForExemplaire(exemplaire.getId());
    if (!confirmedReservations.isEmpty()) {
      throw new IllegalArgumentException("This exemplaire has confirmed reservations");
    }

    // Update exemplaire status to borrowed
    exemplaire.setStatus("BORROWED");
    exemplaireRepository.save(exemplaire);

    // Create loan
    Loan loan = new Loan();
    loan.setExemplaire(exemplaire);
    loan.setMembership(membership);
    loan.setLoanType(loanType);
    loan.setLoanDate(loanDate);

    return loanRepository.save(loan);
  }

  @Transactional
  public Loan returnLoan(Long loanId, LocalDateTime returnDateTime) {
    Loan loan = loanRepository.findById(loanId)
        .orElseThrow(() -> new IllegalArgumentException("Loan not found"));

    if (loan.getReturnDate() != null) {
      throw new IllegalArgumentException("Loan has already been returned");
    }

    loan.setReturnDate(returnDateTime);
    
    // Update exemplaire status back to available
    Exemplaire exemplaire = loan.getExemplaire();
    exemplaire.setStatus("AVAILABLE");
    exemplaireRepository.save(exemplaire);
    
    return loanRepository.save(loan);
  }

  @Transactional
  public LoanStatusHistory addLoanStatus(Long loanId, Long statusTypeId) {
    Loan loan = loanRepository.findById(loanId)
        .orElseThrow(() -> new IllegalArgumentException("Loan not found"));

    LoanStatusType statusType = statusTypeRepository.findById(statusTypeId)
        .orElseThrow(() -> new IllegalArgumentException("Status type not found"));

    LoanStatusHistory statusHistory = new LoanStatusHistory();
    statusHistory.setLoan(loan);
    statusHistory.setLoanStatusType(statusType);
    statusHistory.setStatusDate(LocalDateTime.now());

    return statusHistoryRepository.save(statusHistory);
  }

  public List<Loan> findActiveByMembershipId(Long membershipId) {
    return loanRepository.findActiveByMembershipId(membershipId);
  }

  public List<Loan> findByMembershipId(Long membershipId) {
    return loanRepository.findByMembershipId(membershipId);
  }

  public long countActiveByMembershipId(Long membershipId) {
    return loanRepository.countActiveByMembershipId(membershipId);
  }

  public Map<Long, Long> getActiveLoanCountsForAllMemberships() {
    Map<Long, Long> result = new HashMap<>();
    List<Membership> allMemberships = membershipRepository.findAll();

    for (Membership membership : allMemberships) {
      long count = loanRepository.countActiveByMembershipId(membership.getId());
      if (count > 0) {
        result.put(membership.getId(), count);
      }
    }

    return result;
  }

  public Map<Long, Long> getLateLoanCountsForAllMemberships() {
    Map<Long, Long> result = new HashMap<>();
    List<Membership> allMemberships = membershipRepository.findAll();

    for (Membership membership : allMemberships) {
      long count = loanRepository.countLateByMembershipId(membership.getId());
      if (count > 0) {
        result.put(membership.getId(), count);
      }
    }

    return result;
  }

  public List<LoanStatusHistory> getStatusHistory(Long loanId) {
    return statusHistoryRepository.findByLoanIdOrderByStatusDateDesc(loanId);
  }

  public List<Loan> findRecentByMembershipId(Long id, int size) {
    Pageable pageable = Pageable.ofSize(size);
    return loanRepository.findByMembershipIdOrderByLoanDateDesc(id, pageable);
  }
}
