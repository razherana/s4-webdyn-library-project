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
import mg.razherana.library.models.books.Book;

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
  @JoinColumn(name = "book_id", nullable = false)
  private Book book;

  @ManyToOne
  @JoinColumn(name = "loan_type_id", nullable = false)
  private LoanType loanType;

  @Column(nullable = false)
  private LocalDateTime loanDate;

  @Column
  private LocalDateTime returnDate;

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
    long hoursElapsed = loanDate.until(referenceTime, ChronoUnit.HOURS);

    return hoursElapsed > maxHours;
  }
}
