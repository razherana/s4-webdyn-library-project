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
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import mg.razherana.library.models.books.Book;

@Entity
@Table(name = "reservations")
@Data
@ToString(exclude = "book")
@EqualsAndHashCode(exclude = "book")
public class Reservation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "book_id", nullable = false)
  private Book book;

  @OneToMany(mappedBy = "reservation", fetch = FetchType.LAZY)
  private List<ReservationStatusHistory> reservationStatusHistories;

  private boolean takeHome;

  private LocalDateTime reservationDate;
}
