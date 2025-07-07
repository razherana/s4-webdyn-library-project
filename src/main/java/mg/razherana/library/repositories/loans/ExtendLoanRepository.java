package mg.razherana.library.repositories.loans;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.razherana.library.models.loans.ExtendLoan;

@Repository
public interface ExtendLoanRepository extends JpaRepository<ExtendLoan, Long> {
    
    List<ExtendLoan> findByLoanIdOrderByRequestedAtDesc(Long loanId);
    
    List<ExtendLoan> findByLoanIdAndConfirmedAtIsNullAndRejectedAtIsNull(Long loanId);
    
    List<ExtendLoan> findByConfirmedAtIsNullAndRejectedAtIsNull();
    
    List<ExtendLoan> findByLoan_Membership_IdOrderByRequestedAtDesc(Long membershipId);
}
