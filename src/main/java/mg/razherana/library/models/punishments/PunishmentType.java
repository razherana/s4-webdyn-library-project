package mg.razherana.library.models.punishments;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "punishment_types")
@Data
public class PunishmentType {
  @Id
  @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
  private long id; // Unique identifier for the punishment type
  
  private String name; // Name of the punishment type (Late, etc.)
}
