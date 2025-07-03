package mg.razherana.library.models.loans;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "loan_status_history")
@Data
@ToString(exclude = { "loan", "loanStatusType" })
public class LoanStatusHistory {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @ManyToOne
  @JoinColumn(name = "loan_id", nullable = false)
  private Loan loan;

  @ManyToOne
  @JoinColumn(name = "loan_status_type_id", nullable = false)
  private LoanStatusType loanStatusType;
}
