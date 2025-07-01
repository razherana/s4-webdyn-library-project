package mg.razherana.library.models.loans;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@Table(name = "peoples")
@ToString(exclude = "memberships")
@EqualsAndHashCode(exclude = "memberships")
public class People {
  @Id
  @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
  private Long id;

  private String name;

  private LocalDate birthDate;

  private String address;

  @OneToMany(mappedBy = "people")
  private List<Membership> memberships;
}
