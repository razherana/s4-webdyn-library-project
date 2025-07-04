package mg.razherana.library.repositories.loans;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.razherana.library.models.loans.LoanStatusType;

@Repository
public interface LoanStatusTypeRepository extends JpaRepository<LoanStatusType, Long> {
  Optional<LoanStatusType> findByName(String name);
}
