package mg.razherana.library.models.users;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "roles")
@Data
@ToString(exclude = "accesses")
public class Role {
  @Id
  @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
  private long id;

  private String name;

  private String description;

  @ManyToMany
  @JoinTable(name = "role_accesses", joinColumns = @jakarta.persistence.JoinColumn(name = "role_id"), inverseJoinColumns = @jakarta.persistence.JoinColumn(name = "access_id"))
  private Set<Access> accesses;
}
