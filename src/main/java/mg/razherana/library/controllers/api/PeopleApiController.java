package mg.razherana.library.controllers.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mg.razherana.library.models.loans.Loan;
import mg.razherana.library.models.loans.Membership;
import mg.razherana.library.models.loans.People;
import mg.razherana.library.models.loans.Reservation;
import mg.razherana.library.models.punishments.Punishment;
import mg.razherana.library.services.loans.LoanService;
import mg.razherana.library.services.loans.MembershipService;
import mg.razherana.library.services.loans.PeopleService;
import mg.razherana.library.services.loans.ReservationService;
import mg.razherana.library.services.punishments.PunishmentService;

@RestController
@RequestMapping("/api/people")
public class PeopleApiController {

  @Autowired
  private PeopleService peopleService;

  @Autowired
  private MembershipService membershipService;

  @Autowired
  private LoanService loanService;

  @Autowired
  private ReservationService reservationService;

  @Autowired
  private PunishmentService punishmentService;

  @GetMapping
  public ResponseEntity<List<Map<String, Object>>> getAllPeople() {
    List<People> people = peopleService.findAll();

    List<Map<String, Object>> response = people.stream()
        .map(person -> {
          Map<String, Object> personMap = new HashMap<>();
          personMap.put("id", person.getId());
          personMap.put("name", person.getName());
          personMap.put("birthDate", person.getBirthDate());
          personMap.put("address", person.getAddress());
          personMap.put("membershipsCount", person.getMemberships() != null ? person.getMemberships().size() : 0);
          return personMap;
        })
        .collect(Collectors.toList());

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Map<String, Object>> getPersonDetails(@PathVariable Long id) {
    People person = peopleService.findById(id);

    if (person == null) {
      return ResponseEntity.notFound().build();
    }

    Map<String, Object> response = new HashMap<>();
    response.put("id", person.getId());
    response.put("name", person.getName());
    response.put("birthDate", person.getBirthDate());
    response.put("address", person.getAddress());

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}/memberships")
  public ResponseEntity<List<Map<String, Object>>> getPersonMemberships(@PathVariable Long id) {
    People person = peopleService.findById(id);

    if (person == null) {
      return ResponseEntity.notFound().build();
    }

    List<Membership> memberships = membershipService.findByPeople(person);

    List<Map<String, Object>> response = memberships.stream()
        .map(membership -> {
          Map<String, Object> membershipMap = new HashMap<>();
          membershipMap.put("id", membership.getId());
          membershipMap.put("startDate", membership.getStartDate());
          membershipMap.put("endDate", membership.getEndDate());
          membershipMap.put("isActive", membership.getEndDate().isAfter(java.time.LocalDate.now()));

          // Membership type information
          Map<String, Object> membershipType = new HashMap<>();
          membershipType.put("id", membership.getMembershipType().getId());
          membershipType.put("name", membership.getMembershipType().getName());
          membershipType.put("maxBooksAllowedHome", membership.getMembershipType().getMaxBooksAllowedHome());
          membershipType.put("maxBooksAllowedLibrary", membership.getMembershipType().getMaxBooksAllowedLibrary());
          membershipType.put("maxTimeHoursHome", membership.getMembershipType().getMaxTimeHoursHome());
          membershipType.put("maxTimeHoursLibrary", membership.getMembershipType().getMaxTimeHoursLibrary());
          membershipType.put("maxExtensionsAllowed", membership.getMembershipType().getMaxExtensionsAllowed());
          membershipMap.put("membershipType", membershipType);

          return membershipMap;
        })
        .collect(Collectors.toList());

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}/memberships/{membershipId}/loans")
  public ResponseEntity<List<Map<String, Object>>> getMembershipLoans(@PathVariable Long id,
      @PathVariable Long membershipId) {
    List<Loan> loans = loanService.findByMembershipId(membershipId);

    List<Map<String, Object>> response = loans.stream()
        .map(loan -> {
          Map<String, Object> loanMap = new HashMap<>();
          loanMap.put("id", loan.getId());
          loanMap.put("loanDate", loan.getLoanDate());
          loanMap.put("returnDate", loan.getReturnDate());
          loanMap.put("extendedAt", loan.getExtendedAt());
          loanMap.put("extendCount", loan.getExtendCount());
          loanMap.put("isReturned", loan.getReturnDate() != null);
          loanMap.put("isLate", loan.checkLate(java.time.LocalDateTime.now()));
          loanMap.put("canBeExtended", loan.canBeExtended());

          // Exemplaire and book information
          Map<String, Object> exemplaire = new HashMap<>();
          exemplaire.put("id", loan.getExemplaire().getId());
          exemplaire.put("code", loan.getExemplaire().getCode());
          exemplaire.put("status", loan.getExemplaire().getStatus());

          Map<String, Object> book = new HashMap<>();
          book.put("id", loan.getExemplaire().getBook().getId());
          book.put("title", loan.getExemplaire().getBook().getTitle());
          book.put("author",
              loan.getExemplaire().getBook().getAuthor() != null ? loan.getExemplaire().getBook().getAuthor().getName()
                  : "Unknown");
          exemplaire.put("book", book);
          loanMap.put("exemplaire", exemplaire);

          // Loan type information
          Map<String, Object> loanType = new HashMap<>();
          loanType.put("id", loan.getLoanType().getId());
          loanType.put("name", loan.getLoanType().getName());
          loanType.put("isHomeAllowed", loan.getLoanType().getName().toLowerCase().contains("home"));
          loanMap.put("loanType", loanType);

          return loanMap;
        })
        .collect(Collectors.toList());

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}/memberships/{membershipId}/reservations")
  public ResponseEntity<List<Map<String, Object>>> getMembershipReservations(@PathVariable Long id,
      @PathVariable Long membershipId) {
    List<Reservation> reservations = reservationService.findByMembershipId(membershipId);

    List<Map<String, Object>> response = reservations.stream()
        .map(reservation -> {
          Map<String, Object> reservationMap = new HashMap<>();
          reservationMap.put("id", reservation.getId());
          reservationMap.put("reservationDate", reservation.getReservationDate());
          reservationMap.put("takeHome", reservation.isTakeHome());

          // Exemplaire and book information
          Map<String, Object> exemplaire = new HashMap<>();
          exemplaire.put("id", reservation.getExemplaire().getId());
          exemplaire.put("code", reservation.getExemplaire().getCode());
          exemplaire.put("status", reservation.getExemplaire().getStatus());

          Map<String, Object> book = new HashMap<>();
          book.put("id", reservation.getExemplaire().getBook().getId());
          book.put("title", reservation.getExemplaire().getBook().getTitle());
          book.put("author",
              reservation.getExemplaire().getBook().getAuthor() != null
                  ? reservation.getExemplaire().getBook().getAuthor().getName()
                  : "Unknown");
          exemplaire.put("book", book);
          reservationMap.put("exemplaire", exemplaire);

          // Latest status
          if (!reservation.getReservationStatusHistories().isEmpty()) {
            Map<String, Object> status = new HashMap<>();
            status.put("name", reservation.getReservationStatusHistories().get(0).getReservationStatusType().getName());
            status.put("date", reservation.getReservationStatusHistories().get(0).getStatusDate());
            reservationMap.put("currentStatus", status);
          }

          return reservationMap;
        })
        .collect(Collectors.toList());

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}/memberships/{membershipId}/punishments")
  public ResponseEntity<List<Map<String, Object>>> getMembershipPunishments(@PathVariable Long id,
      @PathVariable Long membershipId) {
    List<Punishment> punishments = punishmentService.findByMembershipId(membershipId);

    List<Map<String, Object>> response = punishments.stream()
        .map(punishment -> {
          Map<String, Object> punishmentMap = new HashMap<>();
          punishmentMap.put("id", punishment.getId());
          punishmentMap.put("description", punishment.getDescription());
          punishmentMap.put("punishmentDate", punishment.getPunishmentDate());
          punishmentMap.put("durationHours", punishment.getDurationHours());

          // Calculate if punishment is active
          java.time.LocalDateTime endTime = punishment.getPunishmentDate()
              .plusSeconds((long) (punishment.getDurationHours() * 3600));
          punishmentMap.put("isActive", endTime.isAfter(java.time.LocalDateTime.now()));
          punishmentMap.put("endTime", endTime);

          // Punishment type information
          Map<String, Object> punishmentType = new HashMap<>();
          punishmentType.put("id", punishment.getType().getId());
          punishmentType.put("name", punishment.getType().getName());
          punishmentMap.put("punishmentType", punishmentType);

          return punishmentMap;
        })
        .collect(Collectors.toList());

    return ResponseEntity.ok(response);
  }
}
