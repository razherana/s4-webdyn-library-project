package mg.razherana.library.repositories.loans;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mg.razherana.library.models.loans.LoanStatusHistory;

@Repository
public interface LoanStatusHistoryRepository extends JpaRepository<LoanStatusHistory, Long> {

  @Query("SELECT lsh FROM LoanStatusHistory lsh WHERE lsh.loan.id = :loanId ORDER BY lsh.statusDate DESC")
  List<LoanStatusHistory> findByLoanIdOrderByStatusDateDesc(@Param("loanId") Long loanId);

  @Query("SELECT lsh FROM LoanStatusHistory lsh WHERE lsh.loan.id = :loanId ORDER BY lsh.statusDate DESC LIMIT 1")
  LoanStatusHistory findLatestByLoanId(@Param("loanId") Long loanId);
}
