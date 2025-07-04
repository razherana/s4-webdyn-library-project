package mg.razherana.library.models.users;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
  @Id
  @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
  private long id;

  private String username;

  private String password;

  @ManyToMany
  @JoinTable(name = "users_roles", joinColumns = @jakarta.persistence.JoinColumn(name = "user_id"), inverseJoinColumns = @jakarta.persistence.JoinColumn(name = "role_id"))
  private List<Role> roles;
}
