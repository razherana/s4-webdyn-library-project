package mg.razherana.library.models.loans;

import java.time.LocalDate;

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
@Table(name = "memberships")
@Data
@ToString(exclude = "people")
@EqualsAndHashCode(exclude = "people")
public class Membership {
  @Id
  @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "people_id", nullable = false)
  private People people;

  @ManyToOne
  @JoinColumn(name = "membership_type_id", nullable = false)
  private MembershipType membershipType;

  public String getNumRepr() {
    return membershipType.getName().substring(0, 3).toUpperCase() + "_" + String.format("%03d", people.getId()) + "_MEM_" + String.format("%03d", id);
  }

  private LocalDate startDate;

  private LocalDate endDate;
}
