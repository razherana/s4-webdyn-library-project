package mg.razherana.library.repositories.loans;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import mg.razherana.library.models.loans.Reservation;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
  @Query("SELECT r FROM Reservation r LEFT JOIN FETCH r.membership WHERE r.book.id = :bookId ORDER BY r.reservationDate DESC")
  List<Reservation> findByBookId(Long bookId);

  @EntityGraph(attributePaths = { "reservationStatusHistories", "book", "membership", "membership.people",
      "membership.membershipType" })
  @Query("SELECT r FROM Reservation r ORDER BY r.reservationDate DESC")
  List<Reservation> findAllWithDetails();

  @Query("SELECT r FROM Reservation r " +
      "JOIN FETCH r.book b " +
      "LEFT JOIN FETCH b.author " +
      "LEFT JOIN FETCH r.membership m " +
      "LEFT JOIN FETCH m.people " +
      "LEFT JOIN FETCH m.membershipType " +
      "WHERE b.title LIKE %:search% OR b.author.name LIKE %:search% " +
      "ORDER BY r.reservationDate DESC")
  List<Reservation> findByTitleOrAuthorContaining(@Param("search") String search);

  @Query("SELECT r FROM Reservation r " +
      "JOIN FETCH r.book b " +
      "LEFT JOIN FETCH b.author " +
      "LEFT JOIN FETCH r.membership m " +
      "LEFT JOIN FETCH m.people " +
      "LEFT JOIN FETCH m.membershipType " +
      "WHERE b.id = :bookId AND " +
      "r.reservationDate BETWEEN :startDateTime AND :endDateTime")
  List<Reservation> findByBookAndDateTimeBetween(
      @Param("bookId") Long bookId,
      @Param("startDateTime") LocalDateTime startDateTime,
      @Param("endDateTime") LocalDateTime endDateTime);

  @Query("SELECT r FROM Reservation r " +
      "JOIN FETCH r.book b " +
      "LEFT JOIN FETCH b.author " +
      "LEFT JOIN FETCH r.membership m " +
      "LEFT JOIN FETCH m.people " +
      "LEFT JOIN FETCH m.membershipType " +
      "WHERE r.takeHome = :takeHome " +
      "ORDER BY r.reservationDate DESC")
  List<Reservation> findByTakeHome(@Param("takeHome") boolean takeHome);

  @Query("SELECT r FROM Reservation r " +
      "JOIN FETCH r.book " +
      "LEFT JOIN FETCH r.membership m " +
      "LEFT JOIN FETCH m.people " +
      "LEFT JOIN FETCH m.membershipType " +
      "JOIN r.reservationStatusHistories h " +
      "JOIN h.reservationStatusType t " +
      "WHERE t.name = :statusName " +
      "AND h.statusDate = (SELECT MAX(h2.statusDate) FROM ReservationStatusHistory h2 WHERE h2.reservation = r) " +
      "ORDER BY r.reservationDate DESC")
  List<Reservation> findByLatestStatus(@Param("statusName") String statusName);

  // New combined search query with all possible filters
  @Query("SELECT DISTINCT r FROM Reservation r " +
      "JOIN FETCH r.book b " +
      "LEFT JOIN FETCH b.author a " +
      "LEFT JOIN FETCH r.membership m " +
      "LEFT JOIN FETCH m.people " +
      "LEFT JOIN FETCH m.membershipType " +
      "LEFT JOIN FETCH r.reservationStatusHistories h " +
      "LEFT JOIN FETCH h.reservationStatusType t " +
      "WHERE (:search IS NULL OR b.title LIKE %:search% OR a.name LIKE %:search%) " +
      "AND (:takeHome IS NULL OR r.takeHome = :takeHome) " +
      "AND (:status IS NULL OR (h.statusDate = (SELECT MAX(h2.statusDate) FROM ReservationStatusHistory h2 WHERE h2.reservation = r) "
      +
      "     AND t.name = :status)) " +
      "ORDER BY r.reservationDate DESC")
  List<Reservation> findWithFilters(
      @Param("search") String search,
      @Param("takeHome") Boolean takeHome,
      @Param("status") String status);

  @EntityGraph(attributePaths = { "book", "book.author", "membership", "membership.people", "membership.membershipType",
      "reservationStatusHistories", "reservationStatusHistories.reservationStatusType" })
  @Override
  @NonNull
  List<Reservation> findAll();

  @EntityGraph(attributePaths = { "book", "book.author", "membership", "membership.people", "membership.membershipType",
      "reservationStatusHistories", "reservationStatusHistories.reservationStatusType" })
  @Override
  @NonNull
  Reservation getById(@NonNull Long id);

  @EntityGraph(attributePaths = { "book", "book.author", "membership", "membership.people", "membership.membershipType",
      "reservationStatusHistories", "reservationStatusHistories.reservationStatusType" })
  @Query("SELECT r FROM Reservation r LEFT JOIN FETCH r.reservationStatusHistories rsh WHERE r.book.id = :bookId AND rsh.reservationStatusType.name = 'Pending'")
  List<Reservation> findPendingReservationsForBook(@Param("bookId") Long bookId);
}
