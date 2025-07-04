package mg.razherana.library.repositories.loans;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.razherana.library.models.loans.LoanType;

@Repository
public interface LoanTypeRepository extends JpaRepository<LoanType, Long> {
  // Basic CRUD methods are automatically provided by JpaRepository

  // Add custom query methods if needed
  boolean existsByName(String name);
}
