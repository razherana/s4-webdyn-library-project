package mg.razherana.library.repositories.punishments;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import mg.razherana.library.models.loans.Membership;
import mg.razherana.library.models.punishments.Punishment;

@Repository
public interface PunishmentRepository extends JpaRepository<Punishment, Long> {
  List<Punishment> findByMembership(Membership membership);

  @EntityGraph("membership")
  @Query("SELECT p FROM Punishment p LEFT JOIN FETCH p.membership m WHERE m.id = :membershipId")
  List<Punishment> findByMembershipId(Long membershipId);
}
