package mg.razherana.library.services.loans;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mg.razherana.library.models.loans.ReservationStatusType;
import mg.razherana.library.repositories.loans.ReservationStatusTypeRepository;

@Service
public class ReservationStatusTypeService {

  @Autowired
  private ReservationStatusTypeRepository statusTypeRepository;

  public List<ReservationStatusType> findAll() {
    return statusTypeRepository.findAll();
  }

  public ReservationStatusType findById(Long id) {
    return statusTypeRepository.findById(id).orElse(null);
  }

  @Transactional
  public ReservationStatusType save(String name) {
    // Check if name already exists
    Optional<ReservationStatusType> existing = statusTypeRepository.findByName(name);
    if (existing.isPresent()) {
      throw new IllegalArgumentException("A status type with this name already exists");
    }

    ReservationStatusType statusType = new ReservationStatusType();
    statusType.setName(name);
    return statusTypeRepository.save(statusType);
  }

  @Transactional
  public ReservationStatusType update(Long id, String name) {
    // Check if status type exists
    ReservationStatusType statusType = statusTypeRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Status type not found"));

    // Check if name already exists for a different status type
    Optional<ReservationStatusType> existing = statusTypeRepository.findByName(name);
    if (existing.isPresent() && existing.get().getId() == id) {
      throw new IllegalArgumentException("A status type with this name already exists");
    }

    statusType.setName(name);
    return statusTypeRepository.save(statusType);
  }

  @Transactional
  public void delete(Long id) {
    // Check if status type exists
    if (!statusTypeRepository.existsById(id)) {
      throw new IllegalArgumentException("Status type not found");
    }

    statusTypeRepository.deleteById(id);
  }
}
