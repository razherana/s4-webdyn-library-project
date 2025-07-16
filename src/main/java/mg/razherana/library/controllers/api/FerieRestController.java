package mg.razherana.library.controllers.api;

import mg.razherana.library.models.feries.Ferie;
import mg.razherana.library.services.feries.FerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feries")
public class FerieRestController {

  @Autowired
  private FerieService ferieService;

  @GetMapping
  public ResponseEntity<List<Ferie>> getAllFeries() {
    List<Ferie> feries = ferieService.getAllFeries();
    return ResponseEntity.ok(feries);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Ferie> getFerieById(@PathVariable Long id) {
    Ferie ferie = ferieService.getFerieById(id);
    if (ferie == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(ferie);
  }

  @PostMapping
  public ResponseEntity<Ferie> createFerie(@RequestBody Ferie ferie) {
    Ferie savedFerie = ferieService.saveFerie(ferie);
    return ResponseEntity.ok(savedFerie);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Ferie> updateFerie(@PathVariable Long id, @RequestBody Ferie ferie) {
    Ferie existingFerie = ferieService.getFerieById(id);
    if (existingFerie == null) {
      return ResponseEntity.notFound().build();
    }
    ferie.setId(id);
    Ferie updatedFerie = ferieService.saveFerie(ferie);
    return ResponseEntity.ok(updatedFerie);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteFerie(@PathVariable Long id) {
    Ferie ferie = ferieService.getFerieById(id);
    if (ferie == null) {
      return ResponseEntity.notFound().build();
    }
    ferieService.deleteFerie(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/check-date/{date}")
  public ResponseEntity<Map<String, Boolean>> checkIfFerie(@PathVariable String date) {
    LocalDate checkDate = LocalDate.parse(date);
    boolean isFerie = ferieService.isFerie(checkDate);
    return ResponseEntity.ok(Map.of("isFerie", isFerie));
  }

  @GetMapping("/can-make-reservations")
  public ResponseEntity<Map<String, Object>> canMakeReservations() {
    boolean canMake = ferieService.canMakeLoansOrReservations();
    LocalDate nextAvailable = ferieService.getNextAvailableBusinessDay();

    return ResponseEntity.ok(Map.of(
        "canMake", canMake,
        "nextAvailableDate", nextAvailable));
  }

  @PostMapping("/calculate-business-hours")
  public ResponseEntity<Map<String, Long>> calculateBusinessHours(@RequestBody Map<String, String> request) {
    String startDateStr = request.get("startDate");
    String endDateStr = request.get("endDate");

    LocalDate startDate = LocalDate.parse(startDateStr);
    LocalDate endDate = LocalDate.parse(endDateStr);

    long businessHours = ferieService.calculateBusinessHoursBetween(startDate, endDate);

    return ResponseEntity.ok(Map.of("businessHours", businessHours));
  }

  @PostMapping("/calculate-business-days")
  public ResponseEntity<Map<String, Long>> calculateBusinessDays(@RequestBody Map<String, String> request) {
    String startDateStr = request.get("startDate");
    String endDateStr = request.get("endDate");

    LocalDate startDate = LocalDate.parse(startDateStr);
    LocalDate endDate = LocalDate.parse(endDateStr);

    long businessDays = ferieService.calculateBusinessDaysBetween(startDate, endDate);

    return ResponseEntity.ok(Map.of("businessDays", businessDays));
  }

  @GetMapping("/between/{startDate}/{endDate}")
  public ResponseEntity<List<Ferie>> getFeriesBetween(@PathVariable String startDate, @PathVariable String endDate) {
    LocalDate start = LocalDate.parse(startDate);
    LocalDate end = LocalDate.parse(endDate);

    List<Ferie> feries = ferieService.getFeriesBetween(start, end);
    return ResponseEntity.ok(feries);
  }
}
