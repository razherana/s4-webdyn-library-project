package mg.razherana.library.models.loans;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "extend_loans")
@Data
public class ExtendLoan {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "loan_id", nullable = false)
  private Loan loan;

  private LocalDateTime requestedAt;

  @Column(nullable = true)
  private LocalDateTime confirmedAt = null;

  @Column(nullable = true)
  private LocalDateTime rejectedAt = null;
}
