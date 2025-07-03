package mg.razherana.library.models.loans;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "returned_loan_states")
@Data
public class ReturnedLoanState {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @OneToOne
  @JoinColumn(name = "loan_id", nullable = false, unique = true)
  private Loan loan;

  @ManyToOne
  @JoinColumn(name = "returned_loan_state_type_id", nullable = false)
  private ReturnedLoanStateType stateType;

  @Column
  private String notes; // Additional notes about the return condition
}
