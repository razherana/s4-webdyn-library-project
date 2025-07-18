package mg.razherana.library.services.loans;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mg.razherana.library.models.books.Book;
import mg.razherana.library.models.books.Exemplaire;
import mg.razherana.library.models.loans.Membership;
import mg.razherana.library.models.loans.Reservation;
import mg.razherana.library.models.loans.ReservationStatusHistory;
import mg.razherana.library.models.loans.ReservationStatusType;
import mg.razherana.library.repositories.books.BookRepository;
import mg.razherana.library.repositories.books.ExemplaireRepository;
import mg.razherana.library.repositories.loans.MembershipRepository;
import mg.razherana.library.repositories.loans.ReservationRepository;
import mg.razherana.library.repositories.loans.ReservationStatusHistoryRepository;
import mg.razherana.library.repositories.loans.ReservationStatusTypeRepository;

@Service
public class ReservationService {

  @Autowired
  private ReservationRepository reservationRepository;

  @Autowired
  private ReservationStatusHistoryRepository statusHistoryRepository;

  @Autowired
  private ReservationStatusTypeRepository statusTypeRepository;

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private ExemplaireRepository exemplaireRepository;

  @Autowired
  private MembershipRepository membershipRepository;

  // Make sure all public methods that return entities to the controller are
  // marked as @Transactional(readOnly = true)
  @Transactional(readOnly = true)
  public List<Reservation> findAll() {
    return reservationRepository.findAll();
  }

  @Transactional(readOnly = true)
  public Reservation findById(Long id) {
    return reservationRepository.findById(id).orElse(null);
  }

  public ReservationStatusHistory getLatestStatus(Long reservationId) {
    return statusHistoryRepository.findLatestByReservationId(reservationId);
  }

  public List<ReservationStatusHistory> getStatusHistory(Long reservationId) {
    return statusHistoryRepository.findByReservationIdOrderByStatusDateDesc(reservationId);
  }

  @Transactional
  public Reservation createReservation(Long bookId, Long membershipId, LocalDateTime reservationDateTime,
      boolean takeHome)
      throws IllegalArgumentException {

    // Check if book exists
    Optional<Book> bookOpt = bookRepository.findById(bookId);
    if (!bookOpt.isPresent()) {
      throw new IllegalArgumentException("Book not found");
    }

    // Check if membership exists and is active
    Optional<Membership> membershipOpt = membershipRepository.findById(membershipId);
    if (!membershipOpt.isPresent()) {
      throw new IllegalArgumentException("Membership not found");
    }

    Membership membership = membershipOpt.get();
    if (membership.getEndDate().isBefore(LocalDateTime.now().toLocalDate())) {
      throw new IllegalArgumentException("Membership has expired");
    }

    // Find an available exemplaire for the book
    List<Exemplaire> availableExemplaires = exemplaireRepository.findAvailableByBookId(bookId);
    if (availableExemplaires.isEmpty()) {
      throw new IllegalArgumentException("No available exemplaires for this book");
    }

    // Get the first available exemplaire
    Exemplaire exemplaire = availableExemplaires.get(0);

    // Check for existing reservations for this exemplaire at the same time
    // Calculate a window of +/- 1 hour around the requested time
    LocalDateTime startWindow = reservationDateTime.minusHours(1);
    LocalDateTime endWindow = reservationDateTime.plusHours(1);

    List<Reservation> existingReservations = reservationRepository.findByExemplaireAndDateTimeBetween(
        exemplaire.getId(), startWindow, endWindow);

    if (!existingReservations.isEmpty()) {
      throw new IllegalArgumentException("This exemplaire is already reserved around this time");
    }

    // Create new reservation
    Reservation reservation = new Reservation();
    reservation.setExemplaire(exemplaire);
    reservation.setTakeHome(takeHome);
    reservation.setReservationDate(reservationDateTime);
    reservation.setReservationStatusHistories(new ArrayList<>());
    reservation.setMembership(membership);

    // Save reservation
    Reservation savedReservation = reservationRepository.save(reservation);

    // Add initial "Pending" status
    ReservationStatusType pendingStatus = statusTypeRepository.findByName("Pending")
        .orElseThrow(() -> new IllegalStateException("Pending status type not found"));

    ReservationStatusHistory initialStatus = new ReservationStatusHistory();
    initialStatus.setReservation(savedReservation);
    initialStatus.setReservationStatusType(pendingStatus);
    initialStatus.setStatusDate(LocalDateTime.now());

    statusHistoryRepository.save(initialStatus);

    return savedReservation;
  }

  @Transactional
  public ReservationStatusHistory updateStatus(Long reservationId, Long statusTypeId) {
    Optional<Reservation> reservationOpt = reservationRepository.findById(reservationId);
    Optional<ReservationStatusType> statusTypeOpt = statusTypeRepository.findById(statusTypeId);

    if (!reservationOpt.isPresent() || !statusTypeOpt.isPresent()) {
      throw new IllegalArgumentException("Reservation or status type not found");
    }

    ReservationStatusHistory statusHistory = new ReservationStatusHistory();
    statusHistory.setReservation(reservationOpt.get());
    statusHistory.setReservationStatusType(statusTypeOpt.get());
    statusHistory.setStatusDate(LocalDateTime.now());

    return statusHistoryRepository.save(statusHistory);
  }

  public List<Reservation> findWithFilters(String search, Boolean takeHome, String status) {
    // Normalize empty strings to null
    search = (search != null && search.trim().isEmpty()) ? null : search;
    status = (status != null && status.trim().isEmpty()) ? null : status;

    return reservationRepository.findWithFilters(search, takeHome, status);
  }

  /**
   * Find confirmed reservations for a specific exemplaire
   */
  public List<Reservation> findConfirmedReservationsForExemplaire(Long exemplaireId) {
    List<Reservation> result = new ArrayList<>();
    List<Reservation> exemplaireReservations = reservationRepository.findByExemplaireId(exemplaireId);

    for (Reservation reservation : exemplaireReservations) {
      ReservationStatusHistory latestStatus = statusHistoryRepository.findLatestByReservationId(reservation.getId());
      if (latestStatus != null && latestStatus.getReservationStatusType().getName().equals("Confirmed")) {
        result.add(reservation);
      }
    }

    return result;
  }

  /**
   * Find confirmed reservations for a specific book
   */
  public List<Reservation> findConfirmedReservationsForBook(Long bookId) {
    List<Reservation> result = new ArrayList<>();
    List<Reservation> bookReservations = reservationRepository.findByBookId(bookId);

    for (Reservation reservation : bookReservations) {
      ReservationStatusHistory latestStatus = statusHistoryRepository.findLatestByReservationId(reservation.getId());
      if (latestStatus != null && latestStatus.getReservationStatusType().getName().equals("Confirmed")) {
        result.add(reservation);
      }
    }

    return result;
  }

  /**
   * Find pending reservations for a specific book
   */
  @Transactional(readOnly = true)
  public List<Reservation> findPendingReservationsForBook(Long bookId) {
    return reservationRepository.findPendingReservationsForBook(bookId);
  }

  /**
   * Cancel a reservation
   */
  @Transactional
  public ReservationStatusHistory cancelReservation(Long reservationId) {
    Reservation reservation = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

    ReservationStatusType cancelledStatus = statusTypeRepository.findByName("Cancelled")
        .orElseThrow(() -> new IllegalStateException("Cancelled status type not found"));

    ReservationStatusHistory statusHistory = new ReservationStatusHistory();
    statusHistory.setReservation(reservation);
    statusHistory.setReservationStatusType(cancelledStatus);
    statusHistory.setStatusDate(LocalDateTime.now());

    return statusHistoryRepository.save(statusHistory);
  }

  public List<Reservation> findByMembershipId(Long id) {
  return reservationRepository.findByMembershipId(id);
  }

  public List<Reservation> findRecentByMembershipId(Long id, int size) {
    Pageable pageable = PageRequest.of(0, size);
    return reservationRepository.findByMembershipIdOrderByReservationDateDesc(id, pageable);
  }
}
