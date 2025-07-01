package mg.razherana.library.repositories.loans;

import mg.razherana.library.models.loans.Membership;
import mg.razherana.library.models.loans.People;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {

  @Query("SELECT m FROM Membership m ORDER BY m.startDate DESC")
  List<Membership> findAllOrderByStartDateDesc();

  List<Membership> findByPeopleOrderByStartDateDesc(People people);

  @Query("SELECT m FROM Membership m WHERE m.people.id = :peopleId AND m.endDate >= CURRENT_DATE")
  List<Membership> findActiveMembershipsByPeopleId(@Param("peopleId") Long peopleId);
}
