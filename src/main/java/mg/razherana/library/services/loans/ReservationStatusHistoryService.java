package mg.razherana.library.services.loans;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mg.razherana.library.models.loans.ReservationStatusHistory;
import mg.razherana.library.repositories.loans.ReservationStatusHistoryRepository;

@Service
public class ReservationStatusHistoryService {

  @Autowired
  private ReservationStatusHistoryRepository statusHistoryRepository;

  public List<ReservationStatusHistory> findAll() {
    return statusHistoryRepository.findAll();
  }

  public List<ReservationStatusHistory> findByReservationId(Long reservationId) {
    return statusHistoryRepository.findByReservationIdOrderByStatusDateDesc(reservationId);
  }
}
