package mg.razherana.library.services.punishments;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mg.razherana.library.models.loans.Membership;
import mg.razherana.library.models.punishments.Punishment;
import mg.razherana.library.models.punishments.PunishmentType;
import mg.razherana.library.repositories.loans.MembershipRepository;
import mg.razherana.library.repositories.punishments.PunishmentRepository;
import mg.razherana.library.repositories.punishments.PunishmentTypeRepository;

@Service
public class PunishmentService {

  @Autowired
  private PunishmentRepository punishmentRepository;

  @Autowired
  private PunishmentTypeRepository punishmentTypeRepository;

  @Autowired
  private MembershipRepository membershipRepository;

  public List<Punishment> findAll() {
    return punishmentRepository.findAll();
  }

  public Punishment findById(Long id) {
    return punishmentRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Invalid punishment ID: " + id));
  }

  public List<Punishment> findByMembershipId(Long membershipId) {
    return punishmentRepository.findByMembershipId(membershipId);
  }

  public Punishment createPunishment(Long membershipId, Long punishmentTypeId,
      float durationHours, String description) {
    return createPunishment(membershipId, punishmentTypeId, durationHours, description, LocalDateTime.now());
  }

  public Punishment createPunishment(Long membershipId, Long punishmentTypeId,
      float durationHours, String description, LocalDateTime punishmentDate) {

    if (durationHours <= 0) {
      throw new IllegalArgumentException("Duration must be greater than zero");
    }

    Membership membership = membershipRepository.findById(membershipId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid membership ID: " + membershipId));

    PunishmentType punishmentType = punishmentTypeRepository.findById(punishmentTypeId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid punishment type ID: " + punishmentTypeId));

    Punishment punishment = new Punishment();
    punishment.setMembership(membership);
    punishment.setType(punishmentType);
    punishment.setDurationHours(durationHours);
    punishment.setDescription(description);
    punishment.setPunishmentDate(punishmentDate);

    return punishmentRepository.save(punishment);
  }

  public Punishment updatePunishment(Long id, Long punishmentTypeId,
      float durationHours, String description) {
    return updatePunishment(id, punishmentTypeId, durationHours, description, null);
  }

  public Punishment updatePunishment(Long id, Long punishmentTypeId,
      float durationHours, String description, LocalDateTime punishmentDate) {

    Punishment punishment = findById(id);

    if (punishmentTypeId != null) {
      PunishmentType punishmentType = punishmentTypeRepository.findById(punishmentTypeId)
          .orElseThrow(() -> new IllegalArgumentException("Invalid punishment type ID: " + punishmentTypeId));
      punishment.setType(punishmentType);
    }

    if (durationHours > 0) {
      punishment.setDurationHours(durationHours);
    } else {
      throw new IllegalArgumentException("Duration must be greater than zero");
    }

    punishment.setDescription(description);

    if (punishmentDate != null) {
      punishment.setPunishmentDate(punishmentDate);
    }

    return punishmentRepository.save(punishment);
  }

  public void deletePunishment(Long id) {
    Punishment punishment = findById(id);
    punishmentRepository.delete(punishment);
  }
}
