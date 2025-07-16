package mg.razherana.library.models.books;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "exemplaires")
@Data
@ToString(exclude = "book")
@EqualsAndHashCode(exclude = "book")
public class Exemplaire {
  @Id
  @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "book_id", nullable = false)
  @JsonIgnore
  private Book book;

  @Column(nullable = false, unique = true)
  private String code; // Unique identifier for this exemplaire (e.g., "EX001", "EX002")

  @Column(nullable = false)
  private String status = "AVAILABLE"; // AVAILABLE, BORROWED

  @Column
  private String location; // Where the exemplaire is located (e.g., "Shelf A1", "Section B")

  @Column
  private String notes; // Additional notes about this exemplaire
}
