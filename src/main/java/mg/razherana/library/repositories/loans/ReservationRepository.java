package mg.razherana.library.repositories.loans;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mg.razherana.library.models.loans.Reservation;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

  @Query("SELECT r FROM Reservation r " +
      "JOIN FETCH r.book b " +
      "LEFT JOIN FETCH b.author " +
      "LEFT JOIN r.reservationStatusHistories h " +
      "LEFT JOIN h.reservationStatusType " +
      "ORDER BY r.reservationDate DESC")
  List<Reservation> findAllWithDetails();

  @Query("SELECT r FROM Reservation r " +
      "JOIN FETCH r.book b " +
      "LEFT JOIN FETCH b.author " +
      "WHERE b.title LIKE %:search% OR b.author.name LIKE %:search% " +
      "ORDER BY r.reservationDate DESC")
  List<Reservation> findByTitleOrAuthorContaining(@Param("search") String search);

  @Query("SELECT r FROM Reservation r " +
      "JOIN FETCH r.book b " +
      "LEFT JOIN FETCH b.author " +
      "WHERE b.id = :bookId AND " +
      "r.reservationDate BETWEEN :startDateTime AND :endDateTime")
  List<Reservation> findByBookAndDateTimeBetween(
      @Param("bookId") Long bookId,
      @Param("startDateTime") LocalDateTime startDateTime,
      @Param("endDateTime") LocalDateTime endDateTime);

  @Query("SELECT r FROM Reservation r " +
      "JOIN FETCH r.book b " +
      "LEFT JOIN FETCH b.author " +
      "WHERE r.takeHome = :takeHome " +
      "ORDER BY r.reservationDate DESC")
  List<Reservation> findByTakeHome(@Param("takeHome") boolean takeHome);

  @Query("SELECT r FROM Reservation r " +
      "JOIN FETCH r.book " +
      "JOIN r.reservationStatusHistories h " +
      "JOIN h.reservationStatusType t " +
      "WHERE t.name = :statusName " +
      "AND h.statusDate = (SELECT MAX(h2.statusDate) FROM ReservationStatusHistory h2 WHERE h2.reservation = r) " +
      "ORDER BY r.reservationDate DESC")
  List<Reservation> findByLatestStatus(@Param("statusName") String statusName);
}
