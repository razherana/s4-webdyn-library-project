package mg.razherana.library.models.punishments;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import mg.razherana.library.models.loans.Membership;

@Entity
@Table(name = "punishments")
@Data
public class Punishment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @ManyToOne
  @JoinColumn(name = "punishment_type_id", nullable = false)
  private PunishmentType type;

  @ManyToOne
  @JoinColumn(name = "membership_id", nullable = false)
  private Membership membership;

  private String description;

  private float durationHours;

  @Column(columnDefinition = "DATETIME DEFAULT NOW()")
  private LocalDateTime punishmentDate;
}
