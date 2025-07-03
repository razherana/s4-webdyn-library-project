package mg.razherana.library.models.loans;

import java.time.LocalDateTime;

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
}
