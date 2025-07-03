package mg.razherana.library.models.loans;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "returned_loan_state_types")
@Data
public class ReturnedLoanStateType {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String name; // e.g., "Normal", "Damaged", "Lost"
  private String description;
}
