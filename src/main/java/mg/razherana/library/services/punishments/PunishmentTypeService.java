package mg.razherana.library.services.punishments;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mg.razherana.library.models.punishments.PunishmentType;
import mg.razherana.library.repositories.punishments.PunishmentTypeRepository;

@Service
public class PunishmentTypeService {

  @Autowired
  private PunishmentTypeRepository punishmentTypeRepository;

  public List<PunishmentType> findAll() {
    return punishmentTypeRepository.findAll();
  }

  public PunishmentType findById(Long id) {
    return punishmentTypeRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Invalid punishment type ID: " + id));
  }

  public PunishmentType save(String name) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Punishment type name cannot be empty");
    }

    if (punishmentTypeRepository.existsByName(name.trim())) {
      throw new IllegalArgumentException("A punishment type with this name already exists");
    }

    PunishmentType punishmentType = new PunishmentType();
    punishmentType.setName(name.trim());
    return punishmentTypeRepository.save(punishmentType);
  }

  public PunishmentType update(Long id, String name) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Punishment type name cannot be empty");
    }

    PunishmentType punishmentType = findById(id);

    // Check if another punishment type already has this name
    if (!punishmentType.getName().equals(name.trim()) &&
        punishmentTypeRepository.existsByName(name.trim())) {
      throw new IllegalArgumentException("A punishment type with this name already exists");
    }

    punishmentType.setName(name.trim());
    return punishmentTypeRepository.save(punishmentType);
  }

  public void delete(Long id) {
    PunishmentType punishmentType = findById(id);
    punishmentTypeRepository.delete(punishmentType);
  }
}
