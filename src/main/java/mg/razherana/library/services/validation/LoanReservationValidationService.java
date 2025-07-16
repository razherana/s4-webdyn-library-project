package mg.razherana.library.services.validation;

import mg.razherana.library.services.feries.FerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class LoanReservationValidationService {
    
    @Autowired
    private FerieService ferieService;
    
    /**
     * Check if loans can be made on the given date
     */
    public boolean canMakeLoan(LocalDate date) {
        return !ferieService.isFerie(date);
    }
    
    /**
     * Check if loans can be made on the given datetime
     */
    public boolean canMakeLoan(LocalDateTime dateTime) {
        return canMakeLoan(dateTime.toLocalDate());
    }
    
    /**
     * Check if reservations can be made on the given date
     */
    public boolean canMakeReservation(LocalDate date) {
        return !ferieService.isFerie(date);
    }
    
    /**
     * Check if reservations can be made on the given datetime
     */
    public boolean canMakeReservation(LocalDateTime dateTime) {
        return canMakeReservation(dateTime.toLocalDate());
    }
    
    /**
     * Check if loans can be made today
     */
    public boolean canMakeLoanToday() {
        return canMakeLoan(LocalDate.now());
    }
    
    /**
     * Check if reservations can be made today
     */
    public boolean canMakeReservationToday() {
        return canMakeReservation(LocalDate.now());
    }
    
    /**
     * Get validation message for loan creation
     */
    public String getLoanValidationMessage() {
        if (!canMakeLoanToday()) {
            LocalDate nextAvailable = ferieService.getNextAvailableBusinessDay();
            return "Cannot create loans on holidays. Next available date: " + nextAvailable;
        }
        return null;
    }
    
    /**
     * Get validation message for reservation creation
     */
    public String getReservationValidationMessage() {
        if (!canMakeReservationToday()) {
            LocalDate nextAvailable = ferieService.getNextAvailableBusinessDay();
            return "Cannot create reservations on holidays. Next available date: " + nextAvailable;
        }
        return null;
    }
    
    /**
     * Validate loan creation and throw exception if not allowed
     */
    public void validateLoanCreation() throws IllegalStateException {
        String message = getLoanValidationMessage();
        if (message != null) {
            throw new IllegalStateException(message);
        }
    }
    
    /**
     * Validate reservation creation and throw exception if not allowed
     */
    public void validateReservationCreation() throws IllegalStateException {
        String message = getReservationValidationMessage();
        if (message != null) {
            throw new IllegalStateException(message);
        }
    }
}
