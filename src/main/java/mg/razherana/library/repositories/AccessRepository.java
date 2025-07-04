package mg.razherana.library.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.razherana.library.models.users.Access;

@Repository
public interface AccessRepository extends JpaRepository<Access, Long> {
  Access findByName(String name);
}
