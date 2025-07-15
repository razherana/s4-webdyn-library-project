package mg.razherana.library.models.loans;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import mg.razherana.library.models.books.Exemplaire;

@Entity
@Table(name = "loans")
@Data
public class Loan {
  @Id
  @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "membership_id", nullable = false)
  private Membership membership;

  @ManyToOne
  @JoinColumn(name = "exemplaire_id", nullable = false)
  private Exemplaire exemplaire;

  @ManyToOne
  @JoinColumn(name = "loan_type_id", nullable = false)
  private LoanType loanType;

  @Column(nullable = false)
  private LocalDateTime loanDate;

  @Column
  private LocalDateTime returnDate;

  @Column
  private LocalDateTime extendedAt; 
  
  @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
  private int extendCount = 0;

  /**
   * Calculates the current status of the loan
   * 
   * @return "In loan", "Late", or "Returned"
   */
  public String getStatus() {
    if (returnDate != null) {
      return "Returned";
    }

    if (checkLate(LocalDateTime.now())) {
      return "Late";
    }

    return "In loan";
  }

  /**
   * Checks if the loan is late based on the given reference time
   * 
   * @param referenceTime The time to check against
   * @return true if the loan is late, false otherwise
   */
  public boolean checkLate(LocalDateTime referenceTime) {
    if (returnDate != null) {
      return false; // Already returned, not late
    }

    // Determine max hours based on loan type
    boolean isTakeHome = loanType.getName().toLowerCase().contains("home");
    int maxHours = isTakeHome ? membership.getMembershipType().getMaxTimeHoursHome()
        : membership.getMembershipType().getMaxTimeHoursLibrary();

    // If maxHours is 0, treat as no limit
    if (maxHours == 0) {
      return false;
    }

    // Calculate hours elapsed since loan
    float hoursElapsed;
    
    if (extendedAt != null) {
      hoursElapsed = ChronoUnit.SECONDS.between(extendedAt, referenceTime);
    } else {
      hoursElapsed = ChronoUnit.SECONDS.between(loanDate, referenceTime);
    }

    hoursElapsed /= 3600; // Convert seconds to hours

    return hoursElapsed > maxHours;
  }
  
  /**
   * Checks if the loan can be extended
   * 
   * @return true if the loan can be extended, false otherwise
   */
  public boolean canBeExtended() {
    // Already returned, can't extend
    if (returnDate != null) {
      return false;
    }
    
    // Check if max extensions reached
    if (extendCount >= membership.getMembershipType().getMaxExtensionsAllowed()) {
      return false;
    }
    
    // Already late, can't extend
    if (checkLate(LocalDateTime.now())) {
      return false;
    }
    
    return true;
  }
}
