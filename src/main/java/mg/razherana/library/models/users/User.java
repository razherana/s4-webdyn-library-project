package mg.razherana.library.models.users;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import mg.razherana.library.models.loans.People;

@Entity
@Table(name = "users")
@Data
public class User {
  @Id
  @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
  private long id;

  private String username;

  private String password;

  @ManyToOne
  @JoinColumn(name = "people_id")
  private People people;

  @ManyToMany
  @JoinTable(name = "users_roles", joinColumns = @jakarta.persistence.JoinColumn(name = "user_id"), inverseJoinColumns = @jakarta.persistence.JoinColumn(name = "role_id"))
  private Set<Role> roles = new HashSet<>();

  public boolean isPeople() {
    return people != null;
  }
}
