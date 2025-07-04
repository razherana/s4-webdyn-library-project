package mg.razherana.library.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import mg.razherana.library.models.users.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);

  @EntityGraph(attributePaths = { "roles", "roles.accesses" })
  @Query("SELECT u FROM User u WHERE u.id = ?1")
  Optional<User> findByIdWithAll(long attribute);
}
