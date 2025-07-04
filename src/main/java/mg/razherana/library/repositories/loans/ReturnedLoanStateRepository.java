package mg.razherana.library.repositories.loans;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.razherana.library.models.loans.ReturnedLoanState;

@Repository
public interface ReturnedLoanStateRepository extends JpaRepository<ReturnedLoanState, Long> {
  ReturnedLoanState findByLoanId(Long loanId);
}
