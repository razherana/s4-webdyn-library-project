package mg.razherana.library.services.loans;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mg.razherana.library.models.loans.ExtendLoan;
import mg.razherana.library.models.loans.Loan;
import mg.razherana.library.repositories.loans.ExtendLoanRepository;
import mg.razherana.library.repositories.loans.LoanRepository;

@Service
public class ExtendLoanService {

    @Autowired
    private ExtendLoanRepository extendLoanRepository;
    
    @Autowired
    private LoanRepository loanRepository;
    
    public List<ExtendLoan> findAll() {
        return extendLoanRepository.findAll();
    }
    
    public List<ExtendLoan> findPending() {
        return extendLoanRepository.findByConfirmedAtIsNullAndRejectedAtIsNull();
    }
    
    public List<ExtendLoan> findByLoanId(Long loanId) {
        return extendLoanRepository.findByLoanIdOrderByRequestedAtDesc(loanId);
    }
    
    public List<ExtendLoan> findByMembershipId(Long membershipId) {
        return extendLoanRepository.findByLoan_Membership_IdOrderByRequestedAtDesc(membershipId);
    }
    
    @Transactional
    public ExtendLoan requestExtension(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));
        
        // Check if loan can be extended
        if (!loan.canBeExtended()) {
            throw new IllegalArgumentException("Loan cannot be extended");
        }
        
        // Check if there's already a pending request
        List<ExtendLoan> pendingRequests = extendLoanRepository
                .findByLoanIdAndConfirmedAtIsNullAndRejectedAtIsNull(loanId);
        
        if (!pendingRequests.isEmpty()) {
            throw new IllegalArgumentException("There is already a pending extension request for this loan");
        }
        
        ExtendLoan extendLoan = new ExtendLoan();
        extendLoan.setLoan(loan);
        extendLoan.setRequestedAt(LocalDateTime.now());
        
        return extendLoanRepository.save(extendLoan);
    }
    
    @Transactional
    public ExtendLoan approveExtension(Long extendLoanId, LocalDateTime extensionTime) {
        ExtendLoan extendLoan = extendLoanRepository.findById(extendLoanId)
                .orElseThrow(() -> new IllegalArgumentException("Extension request not found"));
        
        if (extendLoan.getConfirmedAt() != null || extendLoan.getRejectedAt() != null) {
            throw new IllegalArgumentException("Extension request has already been processed");
        }
        
        Loan loan = extendLoan.getLoan();
        
        // Update the loan
        loan.setExtendCount(loan.getExtendCount() + 1);
        loan.setExtendedAt(extensionTime);
        loanRepository.save(loan);
        
        // Update the extension request
        extendLoan.setConfirmedAt(LocalDateTime.now());
        return extendLoanRepository.save(extendLoan);
    }
    
    @Transactional
    public ExtendLoan rejectExtension(Long extendLoanId) {
        ExtendLoan extendLoan = extendLoanRepository.findById(extendLoanId)
                .orElseThrow(() -> new IllegalArgumentException("Extension request not found"));
        
        if (extendLoan.getConfirmedAt() != null || extendLoan.getRejectedAt() != null) {
            throw new IllegalArgumentException("Extension request has already been processed");
        }
        
        extendLoan.setRejectedAt(LocalDateTime.now());
        return extendLoanRepository.save(extendLoan);
    }
    
    @Transactional
    public void deleteExtension(Long extendLoanId) {
        ExtendLoan extendLoan = extendLoanRepository.findById(extendLoanId)
                .orElseThrow(() -> new IllegalArgumentException("Extension request not found"));
        
        // Only allow deletion of pending requests
        if (extendLoan.getConfirmedAt() != null || extendLoan.getRejectedAt() != null) {
            throw new IllegalArgumentException("Cannot delete a processed extension request");
        }
        
        extendLoanRepository.delete(extendLoan);
    }

    public ExtendLoan findById(Long id) {
      return extendLoanRepository.findById(id)
          .orElse(null);
    }
}
