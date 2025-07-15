package mg.razherana.library.repositories.books;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mg.razherana.library.models.books.Exemplaire;

@Repository
public interface ExemplaireRepository extends JpaRepository<Exemplaire, Long> {
  
  // Find exemplaires by book ID
  List<Exemplaire> findByBookId(Long bookId);
  
  // Find available exemplaires for a book
  @Query("SELECT e FROM Exemplaire e WHERE e.book.id = :bookId AND e.status = 'AVAILABLE'")
  List<Exemplaire> findAvailableByBookId(@Param("bookId") Long bookId);

  // Find available exemplaires
  @Query("SELECT e FROM Exemplaire e WHERE e.status = 'AVAILABLE'")
  List<Exemplaire> findAvailableExemplaires();
  
  // Find by code (unique identifier)
  Exemplaire findByCode(String code);
  
  // Find by status
  List<Exemplaire> findByStatus(String status);
  
  // Count available exemplaires for a book
  @Query("SELECT COUNT(e) FROM Exemplaire e WHERE e.book.id = :bookId AND e.status = 'AVAILABLE'")
  long countAvailableByBookId(@Param("bookId") Long bookId);
}
