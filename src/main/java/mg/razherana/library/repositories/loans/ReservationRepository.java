package mg.razherana.library.repositories.loans;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import mg.razherana.library.models.loans.Reservation;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
  @Query("SELECT r FROM Reservation r LEFT JOIN FETCH r.membership WHERE r.exemplaire.id = :exemplaireId ORDER BY r.reservationDate DESC")
  List<Reservation> findByExemplaireId(Long exemplaireId);

  @Query("SELECT r FROM Reservation r LEFT JOIN FETCH r.membership WHERE r.exemplaire.book.id = :bookId ORDER BY r.reservationDate DESC")
  List<Reservation> findByBookId(Long bookId);

  @EntityGraph(attributePaths = { "reservationStatusHistories", "exemplaire", "exemplaire.book", "membership", "membership.people",
      "membership.membershipType" })
  @Query("SELECT r FROM Reservation r ORDER BY r.reservationDate DESC")
  List<Reservation> findAllWithDetails();

  @Query("SELECT r FROM Reservation r " +
      "JOIN FETCH r.exemplaire e " +
      "JOIN FETCH e.book b " +
      "LEFT JOIN FETCH b.author " +
      "LEFT JOIN FETCH r.membership m " +
      "LEFT JOIN FETCH m.people " +
      "LEFT JOIN FETCH m.membershipType " +
      "WHERE b.title LIKE %:search% OR b.author.name LIKE %:search% " +
      "ORDER BY r.reservationDate DESC")
  List<Reservation> findByTitleOrAuthorContaining(@Param("search") String search);

  @Query("SELECT r FROM Reservation r " +
      "JOIN FETCH r.exemplaire e " +
      "JOIN FETCH e.book b " +
      "LEFT JOIN FETCH b.author " +
      "LEFT JOIN FETCH r.membership m " +
      "LEFT JOIN FETCH m.people " +
      "LEFT JOIN FETCH m.membershipType " +
      "WHERE e.id = :exemplaireId AND " +
      "r.reservationDate BETWEEN :startDateTime AND :endDateTime")
  List<Reservation> findByExemplaireAndDateTimeBetween(
      @Param("exemplaireId") Long exemplaireId,
      @Param("startDateTime") LocalDateTime startDateTime,
      @Param("endDateTime") LocalDateTime endDateTime);

  @Query("SELECT r FROM Reservation r " +
      "JOIN FETCH r.exemplaire e " +
      "JOIN FETCH e.book b " +
      "LEFT JOIN FETCH b.author " +
      "LEFT JOIN FETCH r.membership m " +
      "LEFT JOIN FETCH m.people " +
      "LEFT JOIN FETCH m.membershipType " +
      "WHERE e.book.id = :bookId AND " +
      "r.reservationDate BETWEEN :startDateTime AND :endDateTime")
  List<Reservation> findByBookAndDateTimeBetween(
      @Param("bookId") Long bookId,
      @Param("startDateTime") LocalDateTime startDateTime,
      @Param("endDateTime") LocalDateTime endDateTime);

  @Query("SELECT r FROM Reservation r " +
      "JOIN FETCH r.exemplaire e " +
      "JOIN FETCH e.book b " +
      "LEFT JOIN FETCH b.author " +
      "LEFT JOIN FETCH r.membership m " +
      "LEFT JOIN FETCH m.people " +
      "LEFT JOIN FETCH m.membershipType " +
      "WHERE r.takeHome = :takeHome " +
      "ORDER BY r.reservationDate DESC")
  List<Reservation> findByTakeHome(@Param("takeHome") boolean takeHome);

  @Query("SELECT r FROM Reservation r " +
      "JOIN FETCH r.exemplaire e " +
      "JOIN FETCH e.book " +
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
      "JOIN FETCH r.exemplaire e " +
      "JOIN FETCH e.book b " +
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

  @EntityGraph(attributePaths = { "exemplaire", "exemplaire.book", "exemplaire.book.author", "membership", "membership.people", "membership.membershipType",
      "reservationStatusHistories", "reservationStatusHistories.reservationStatusType" })
  @Override
  @NonNull
  List<Reservation> findAll();

  @EntityGraph(attributePaths = { "exemplaire", "exemplaire.book", "exemplaire.book.author", "membership", "membership.people", "membership.membershipType",
      "reservationStatusHistories", "reservationStatusHistories.reservationStatusType" })
  @Override
  @NonNull
  Reservation getById(@NonNull Long id);

  @EntityGraph(attributePaths = { "exemplaire", "exemplaire.book", "exemplaire.book.author", "membership", "membership.people", "membership.membershipType",
      "reservationStatusHistories", "reservationStatusHistories.reservationStatusType" })
  @Query("SELECT r FROM Reservation r LEFT JOIN FETCH r.reservationStatusHistories rsh WHERE r.exemplaire.book.id = :bookId AND rsh.reservationStatusType.name = 'Pending'")
  List<Reservation> findPendingReservationsForBook(@Param("bookId") Long bookId);

  List<Reservation> findByMembershipId(Long id);

  @EntityGraph(attributePaths = { "exemplaire", "exemplaire.book", "exemplaire.book.author", "membership", "membership.people", "membership.membershipType",
      "reservationStatusHistories", "reservationStatusHistories.reservationStatusType" })
  @Query("SELECT r FROM Reservation r WHERE r.membership.id = :id ORDER BY r.reservationDate DESC")
  List<Reservation> findByMembershipIdOrderByReservationDateDesc(Long id, Pageable pageable);
}
