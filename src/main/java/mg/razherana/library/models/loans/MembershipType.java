package mg.razherana.library.models.loans;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "membership_types")
@Data
public class MembershipType {
  @Id
  @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
  private Long id;

  private String name;

  // Maximum number of books allowed for this membership type
  @Column(nullable = false)
  private int maxBooksAllowedHome;

  // Maximum number of books allowed for this membership type
  @Column(nullable = false)
  private int maxBooksAllowedLibrary;
}
