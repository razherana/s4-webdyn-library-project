package mg.razherana.library.services.loans;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mg.razherana.library.models.loans.Loan;
import mg.razherana.library.models.loans.ReturnedLoanState;
import mg.razherana.library.models.loans.ReturnedLoanStateType;
import mg.razherana.library.repositories.loans.LoanRepository;
import mg.razherana.library.repositories.loans.ReturnedLoanStateRepository;
import mg.razherana.library.repositories.loans.ReturnedLoanStateTypeRepository;

@Service
public class ReturnedLoanStateService {

  @Autowired
  private ReturnedLoanStateRepository returnedLoanStateRepository;

  @Autowired
  private ReturnedLoanStateTypeRepository stateTypeRepository;

  @Autowired
  private LoanRepository loanRepository;

  public List<ReturnedLoanStateType> findAllStateTypes() {
    return stateTypeRepository.findAll();
  }

  @Transactional
  public ReturnedLoanState recordReturnState(Long loanId, Long stateTypeId, String notes) {
    Loan loan = loanRepository.findById(loanId)
        .orElseThrow(() -> new IllegalArgumentException("Loan not found"));

    if (loan.getReturnDate() == null) {
      throw new IllegalArgumentException("Loan has not been returned yet");
    }

    ReturnedLoanStateType stateType = stateTypeRepository.findById(stateTypeId)
        .orElseThrow(() -> new IllegalArgumentException("Return state type not found"));

    // Check if a state already exists for this loan
    ReturnedLoanState existingState = returnedLoanStateRepository.findByLoanId(loanId);
    if (existingState != null) {
      existingState.setStateType(stateType);
      existingState.setNotes(notes);
      return returnedLoanStateRepository.save(existingState);
    }

    // Create new state
    ReturnedLoanState returnState = new ReturnedLoanState();
    returnState.setLoan(loan);
    returnState.setStateType(stateType);
    returnState.setNotes(notes);

    return returnedLoanStateRepository.save(returnState);
  }

  public ReturnedLoanState findByLoanId(Long loanId) {
    return returnedLoanStateRepository.findByLoanId(loanId);
  }
}
