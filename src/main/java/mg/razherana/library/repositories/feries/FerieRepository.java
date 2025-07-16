package mg.razherana.library.repositories.feries;

import mg.razherana.library.models.feries.Ferie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FerieRepository extends JpaRepository<Ferie, Long> {
    @Query("SELECT f FROM Ferie f WHERE f.date BETWEEN :startDate AND :endDate ORDER BY f.date")
    List<Ferie> findByDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT f FROM Ferie f WHERE f.date >= :startDate ORDER BY f.date")
    List<Ferie> findFeriesFromDate(@Param("startDate") LocalDate startDate);

    @Query("SELECT f FROM Ferie f WHERE f.date = :date")
    List<Ferie> getByDate(LocalDate date);
}
