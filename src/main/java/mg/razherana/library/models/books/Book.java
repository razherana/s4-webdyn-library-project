package mg.razherana.library.models.books;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "books")
@Data
@EqualsAndHashCode(exclude = { "categories", "author", "exemplaires" }) // Exclude collections and potentially lazy ManyToOne
@ToString(exclude = { "categories", "author", "exemplaires" })
public class Book {
  @Id
  @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
  private Long id;

  private String title;

  @ManyToOne
  @JoinColumn(nullable = false)
  private Author author;

  @ManyToMany(mappedBy = "books", fetch = FetchType.LAZY)
  private Set<Category> categories = new HashSet<>();

  @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
  private Set<Exemplaire> exemplaires = new HashSet<>();
}
