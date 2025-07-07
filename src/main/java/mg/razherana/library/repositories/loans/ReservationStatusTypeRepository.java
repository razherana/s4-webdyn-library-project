package mg.razherana.library.repositories.loans;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import mg.razherana.library.models.loans.ReservationStatusType;

@Repository
public interface ReservationStatusTypeRepository extends JpaRepository<ReservationStatusType, Long> {

  Optional<ReservationStatusType> findByName(String name);

  @Query("SELECT rst FROM ReservationStatusType rst WHERE LOWER(rst.name) = LOWER(:name)")
  ReservationStatusType findByNameContainingIgnoreCase(String name);
}
