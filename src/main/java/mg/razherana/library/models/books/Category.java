package mg.razherana.library.models.books;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@Table(name = "categories")
@EqualsAndHashCode(exclude = "books")
@ToString(exclude = "books")
public class Category {
  @Id
  @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
  private Long id;

  private String name;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "book_categories", joinColumns = @JoinColumn(name = "category_id", nullable = false), inverseJoinColumns = @JoinColumn(name = "book_id", nullable = false))
  @JsonIgnore
  private Set<Book> books = new HashSet<>();
}
