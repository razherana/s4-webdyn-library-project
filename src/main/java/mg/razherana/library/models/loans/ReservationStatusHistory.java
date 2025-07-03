package mg.razherana.library.models.loans;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "reservation_status_histories")
@Data
@ToString(exclude = { "reservation", "reservationStatusType" })
@EqualsAndHashCode(exclude = { "reservation", "reservationStatusType" })
public class ReservationStatusHistory {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "reservation_id", nullable = false)
  private Reservation reservation;

  @ManyToOne(optional = false)
  @JoinColumn(name = "reservation_status_type_id", nullable = false)
  private ReservationStatusType reservationStatusType;

  private LocalDateTime statusDate;
}
