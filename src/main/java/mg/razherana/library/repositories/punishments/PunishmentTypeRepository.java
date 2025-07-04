package mg.razherana.library.repositories.punishments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.razherana.library.models.punishments.PunishmentType;

@Repository
public interface PunishmentTypeRepository extends JpaRepository<PunishmentType, Long> {
  boolean existsByName(String name);
}
