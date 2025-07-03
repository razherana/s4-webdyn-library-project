package mg.razherana.library.repositories.loans;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mg.razherana.library.models.loans.Reservation;
import mg.razherana.library.models.loans.ReservationStatusHistory;

@Repository
public interface ReservationStatusHistoryRepository extends JpaRepository<ReservationStatusHistory, Long> {

  List<ReservationStatusHistory> findByReservationOrderByStatusDateDesc(Reservation reservation);

  @Query("SELECT h FROM ReservationStatusHistory h " +
      "WHERE h.reservation.id = :reservationId " +
      "ORDER BY h.statusDate DESC")
  List<ReservationStatusHistory> findByReservationIdOrderByStatusDateDesc(@Param("reservationId") Long reservationId);

  @Query("SELECT h FROM ReservationStatusHistory h " +
      "WHERE h.reservation.id = :reservationId " +
      "AND h.statusDate = (SELECT MAX(h2.statusDate) FROM ReservationStatusHistory h2 WHERE h2.reservation.id = :reservationId)")
  ReservationStatusHistory findLatestByReservationId(@Param("reservationId") Long reservationId);
}
