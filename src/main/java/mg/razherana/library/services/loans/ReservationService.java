package mg.razherana.library.services.loans;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mg.razherana.library.models.books.Book;
import mg.razherana.library.models.loans.Membership;
import mg.razherana.library.models.loans.Reservation;
import mg.razherana.library.models.loans.ReservationStatusHistory;
import mg.razherana.library.models.loans.ReservationStatusType;
import mg.razherana.library.repositories.books.BookRepository;
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
  private MembershipRepository membershipRepository;

  public List<Reservation> findAll() {
    return reservationRepository.findAllWithDetails();
  }

  public Reservation findById(Long id) {
    return reservationRepository.findById(id).orElse(null);
  }

  public List<Reservation> search(String keyword) {
    if (keyword == null || keyword.trim().isEmpty()) {
      return findAll();
    }
    return reservationRepository.findByTitleOrAuthorContaining(keyword.trim());
  }

  public List<Reservation> filterByTakeHome(boolean takeHome) {
    return reservationRepository.findByTakeHome(takeHome);
  }

  public List<Reservation> filterByStatus(String statusName) {
    return reservationRepository.findByLatestStatus(statusName);
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

    // Check for existing reservations at the same time
    // Calculate a window of +/- 1 hour around the requested time
    LocalDateTime startWindow = reservationDateTime.minusHours(1);
    LocalDateTime endWindow = reservationDateTime.plusHours(1);

    List<Reservation> existingReservations = reservationRepository.findByBookAndDateTimeBetween(
        bookId, startWindow, endWindow);

    if (!existingReservations.isEmpty()) {
      throw new IllegalArgumentException("This book is already reserved around this time");
    }

    // Create new reservation
    Reservation reservation = new Reservation();
    reservation.setBook(bookOpt.get());
    reservation.setTakeHome(takeHome);
    reservation.setReservationDate(reservationDateTime);
    reservation.setReservationStatusHistories(new ArrayList<>());

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
}
