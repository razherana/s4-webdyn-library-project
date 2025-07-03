package mg.razherana.library.repositories.loans;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.razherana.library.models.loans.ReturnedLoanStateType;

@Repository
public interface ReturnedLoanStateTypeRepository extends JpaRepository<ReturnedLoanStateType, Long> {
  Optional<ReturnedLoanStateType> findByName(String name);
}
