package mg.razherana.library.models.loans;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "people_id", nullable = false)
  private People people;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "loan_books", joinColumns = @JoinColumn(name = "loan_id", nullable = false), inverseJoinColumns = @JoinColumn(name = "book_id", nullable = false))
  private Set<Book> books;
}
