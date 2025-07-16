package mg.razherana.library.services.feries;

import mg.razherana.library.models.feries.Ferie;
import mg.razherana.library.repositories.feries.FerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class FerieService {
    
    @Autowired
    private FerieRepository ferieRepository;
    
    public List<Ferie> getAllFeries() {
        return ferieRepository.findAll();
    }
    
    public Ferie saveFerie(Ferie ferie) {
        return ferieRepository.save(ferie);
    }
    
    public void deleteFerie(Long id) {
        ferieRepository.deleteById(id);
    }
    
    public Ferie getFerieById(Long id) {
        return ferieRepository.findById(id).orElse(null);
    }
    
    public boolean isFerie(LocalDate date) {
        return ferieRepository.existsByDate(date);
    }
    
    public List<Ferie> getFeriesBetween(LocalDate startDate, LocalDate endDate) {
        return ferieRepository.findByDateBetween(startDate, endDate);
    }
    
    /**
     * Calculate business days between two dates, excluding weekends and feries
     */
    public long calculateBusinessDaysBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            return 0;
        }
        
        List<Ferie> feriesBetween = getFeriesBetween(startDate, endDate);
        
        long businessDays = 0;
        LocalDate current = startDate;
        
        while (!current.isAfter(endDate)) {
            // Check if current day is not weekend and not a ferie
            if (!isWeekend(current) && !isFerieDate(current, feriesBetween)) {
                businessDays++;
            }
            current = current.plusDays(1);
        }
        
        return businessDays;
    }
    
    /**
     * Calculate business hours between two dates, excluding weekends and feries
     */
    public long calculateBusinessHoursBetween(LocalDate startDate, LocalDate endDate) {
        return calculateBusinessDaysBetween(startDate, endDate) * 24; // Assuming 24 hours per business day
    }
    
    /**
     * Add business days to a date, skipping weekends and feries
     */
    public LocalDate addBusinessDays(LocalDate startDate, long daysToAdd) {
        LocalDate result = startDate;
        long addedDays = 0;
        
        while (addedDays < daysToAdd) {
            result = result.plusDays(1);
            if (!isWeekend(result) && !isFerie(result)) {
                addedDays++;
            }
        }
        
        return result;
    }
    
    /**
     * Check if a date is a weekend (Saturday or Sunday)
     */
    private boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek().getValue() >= 6; // Saturday = 6, Sunday = 7
    }
    
    /**
     * Check if a date is in the list of feries
     */
    private boolean isFerieDate(LocalDate date, List<Ferie> feries) {
        return feries.stream().anyMatch(ferie -> ferie.getDate().equals(date));
    }
    
    /**
     * Check if current date allows for new loans/reservations
     */
    public boolean canMakeLoansOrReservations() {
        return !isFerie(LocalDate.now());
    }
    
    /**
     * Get next available business day for loans/reservations
     */
    public LocalDate getNextAvailableBusinessDay() {
        LocalDate date = LocalDate.now();
        while (isFerie(date) || isWeekend(date)) {
            date = date.plusDays(1);
        }
        return date;
    }
}
