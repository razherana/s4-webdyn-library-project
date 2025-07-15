package mg.razherana.library.services.loans;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mg.razherana.library.models.loans.LoanType;
import mg.razherana.library.repositories.loans.LoanTypeRepository;

@Service
public class LoanTypeService {

  @Autowired
  private LoanTypeRepository loanTypeRepository;

  public List<LoanType> findAll() {
    return loanTypeRepository.findAll();
  }

  public LoanType findById(Long id) {
    return loanTypeRepository.findById(id)
        .orElse(null);
  }

  public LoanType save(String name) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Loan type name cannot be empty");
    }

    LoanType loanType = new LoanType();
    loanType.setName(name);
    return loanTypeRepository.save(loanType);
  }

  public LoanType update(Long id, String name) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Loan type name cannot be empty");
    }

    LoanType loanType = findById(id);
    loanType.setName(name);
    return loanTypeRepository.save(loanType);
  }

  public void delete(Long id) {
    LoanType loanType = findById(id);
    loanTypeRepository.delete(loanType);
  }

  public boolean existsByName(String name) {
    return loanTypeRepository.existsByName(name);
  }
}
