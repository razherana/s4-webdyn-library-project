package mg.razherana.library.models.loans;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "reservation_status_types")
@Data
public class ReservationStatusType {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String name;

  // For UI color coding: "success", "warning", "danger", etc.
  @Column(nullable = true)
  private String colorClass;
}
