package mg.razherana.library.repositories.loans;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mg.razherana.library.models.loans.Loan;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

  // Find active loans for a membership
  @Query("SELECT l FROM Loan l WHERE l.membership.id = :membershipId AND l.returnDate IS NULL")
  List<Loan> findActiveByMembershipId(@Param("membershipId") Long membershipId);

  @Query("SELECT l FROM Loan l WHERE l.membership.id = :membershipId")
  List<Loan> findByMembershipId(@Param("membershipId") Long membershipId);

  // Count active loans for a membership
  @Query("SELECT COUNT(l) FROM Loan l WHERE l.membership.id = :membershipId AND l.returnDate IS NULL")
  long countActiveByMembershipId(@Param("membershipId") Long membershipId);

  // Count active loans by loan type (home/library) for a membership
  @Query("SELECT COUNT(l) FROM Loan l WHERE l.membership.id = :membershipId AND l.returnDate IS NULL AND " +
      "LOWER(l.loanType.name) LIKE (CASE WHEN :isHome = true THEN '%home%' ELSE '%library%' END)")
  long countActiveByMembershipIdAndType(@Param("membershipId") Long membershipId, @Param("isHome") boolean isHome);

  // Find loans by exemplaire ID
  List<Loan> findByExemplaireId(Long exemplaireId);

  // Check if an exemplaire is currently loaned
  @Query("SELECT COUNT(l) > 0 FROM Loan l WHERE l.exemplaire.id = :exemplaireId AND l.returnDate IS NULL")
  boolean isExemplaireCurrentlyLoaned(@Param("exemplaireId") Long exemplaireId);

  // Find by title or author containing search term
  @Query("SELECT l FROM Loan l JOIN l.exemplaire e JOIN e.book b LEFT JOIN b.author a WHERE " +
      "LOWER(b.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
      "LOWER(a.name) LIKE LOWER(CONCAT('%', :search, '%'))")
  List<Loan> findByTitleOrAuthorContaining(@Param("search") String search);

  // Count late loans for a membership
  @Query("SELECT COUNT(l) FROM Loan l WHERE l.membership.id = :membershipId AND l.returnDate IS NULL AND " +
      "((LOWER(l.loanType.name) LIKE '%home%' AND " +
      "TIMESTAMPDIFF(HOUR, l.loanDate, CURRENT_TIMESTAMP) > l.membership.membershipType.maxTimeHoursHome AND " +
      "l.membership.membershipType.maxTimeHoursHome > 0) OR " +
      "(LOWER(l.loanType.name) NOT LIKE '%home%' AND " +
      "TIMESTAMPDIFF(HOUR, l.loanDate, CURRENT_TIMESTAMP) > l.membership.membershipType.maxTimeHoursLibrary AND " +
      "l.membership.membershipType.maxTimeHoursLibrary > 0))")
  long countLateByMembershipId(@Param("membershipId") Long membershipId);

  @Query("SELECT l FROM Loan l WHERE l.returnDate IS NULL")
  List<Loan> findByReturnDateIsNull();

  @Query("SELECT COUNT(l) FROM Loan l WHERE l.returnDate IS NULL")
  long countByReturnedFalse();

  @Query("SELECT l FROM Loan l WHERE l.returnDate IS NOT NULL ORDER BY l.returnDate DESC LIMIT 5")
  List<Loan> findTop5ByReturnedTrueOrderByReturnDateDesc();

  @Query("SELECT l FROM Loan l ORDER BY l.loanDate DESC LIMIT 5")
  List<Loan> findTop5ByOrderByLoanDateDesc();

  Long countByLoanDateEquals(LocalDateTime date);

  @Query("SELECT l FROM Loan l LEFT JOIN FETCH membership m WHERE m.id = :id ORDER BY l.loanDate DESC")
  List<Loan> findByMembershipIdOrderByLoanDateDesc(Long id, Pageable pageable);
}
