package mg.razherana.library.repositories.loans;

import mg.razherana.library.models.loans.People;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PeopleRepository extends JpaRepository<People, Long> {
  @EntityGraph(attributePaths = "memberships")
  @Query("SELECT p FROM People p")
  List<People> findAllWithMemberships();
}
