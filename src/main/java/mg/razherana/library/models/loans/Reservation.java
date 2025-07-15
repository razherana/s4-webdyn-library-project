package mg.razherana.library.models.loans;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import mg.razherana.library.models.books.Exemplaire;

@Entity
@Table(name = "reservations")
@Data
@ToString(exclude = { "exemplaire", "membership" })
@EqualsAndHashCode(exclude = { "exemplaire", "membership" })
public class Reservation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  @JoinColumn(name = "exemplaire_id", nullable = false)
  private Exemplaire exemplaire;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "membership_id", nullable = false)
  private Membership membership;

  @OneToMany(mappedBy = "reservation", fetch = FetchType.LAZY)
  @OrderBy("statusDate DESC")
  private List<ReservationStatusHistory> reservationStatusHistories;

  private boolean takeHome;

  private LocalDateTime reservationDate;
}
