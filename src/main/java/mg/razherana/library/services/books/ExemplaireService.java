package mg.razherana.library.services.books;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mg.razherana.library.models.books.Book;
import mg.razherana.library.models.books.Exemplaire;
import mg.razherana.library.repositories.books.BookRepository;
import mg.razherana.library.repositories.books.ExemplaireRepository;

@Service
public class ExemplaireService {
  
  @Autowired
  private ExemplaireRepository exemplaireRepository;
  
  @Autowired
  private BookRepository bookRepository;
  
  public List<Exemplaire> findAll() {
    return exemplaireRepository.findAll();
  }
  
  public Exemplaire findById(Long id) {
    return exemplaireRepository.findById(id).orElse(null);
  }
  
  public List<Exemplaire> findByBookId(Long bookId) {
    return exemplaireRepository.findByBookId(bookId);
  }
  
  public List<Exemplaire> findAvailableByBookId(Long bookId) {
    return exemplaireRepository.findAvailableByBookId(bookId);
  }
  
  public Exemplaire findByCode(String code) {
    return exemplaireRepository.findByCode(code);
  }
  
  public List<Exemplaire> findByStatus(String status) {
    return exemplaireRepository.findByStatus(status);
  }
  
  public long countAvailableByBookId(Long bookId) {
    return exemplaireRepository.countAvailableByBookId(bookId);
  }
  
  @Transactional
  public Exemplaire save(Exemplaire exemplaire) {
    return exemplaireRepository.save(exemplaire);
  }
  
  @Transactional
  public Exemplaire create(Long bookId, String code, String location, String notes) {
    Book book = bookRepository.findById(bookId)
        .orElseThrow(() -> new IllegalArgumentException("Book not found"));
    
    // Check if code already exists
    if (exemplaireRepository.findByCode(code) != null) {
      throw new IllegalArgumentException("Exemplaire code already exists");
    }
    
    Exemplaire exemplaire = new Exemplaire();
    exemplaire.setBook(book);
    exemplaire.setCode(code);
    exemplaire.setLocation(location);
    exemplaire.setNotes(notes);
    exemplaire.setStatus("AVAILABLE");
    
    return exemplaireRepository.save(exemplaire);
  }
  
  @Transactional
  public Exemplaire update(Long id, String code, String location, String notes, String status) {
    Exemplaire exemplaire = findById(id);
    if (exemplaire == null) {
      throw new IllegalArgumentException("Exemplaire not found");
    }
    
    // Check if code already exists for another exemplaire
    Exemplaire existingExemplaire = exemplaireRepository.findByCode(code);
    if (existingExemplaire != null && !existingExemplaire.getId().equals(id)) {
      throw new IllegalArgumentException("Exemplaire code already exists");
    }
    
    exemplaire.setCode(code);
    exemplaire.setLocation(location);
    exemplaire.setNotes(notes);
    exemplaire.setStatus(status);
    
    return exemplaireRepository.save(exemplaire);
  }
  
  @Transactional
  public void delete(Long id) {
    Exemplaire exemplaire = findById(id);
    if (exemplaire == null) {
      throw new IllegalArgumentException("Exemplaire not found");
    }
    
    // Check if exemplaire is currently borrowed
    if ("BORROWED".equals(exemplaire.getStatus())) {
      throw new IllegalArgumentException("Cannot delete borrowed exemplaire");
    }
    
    exemplaireRepository.delete(exemplaire);
  }
  
  @Transactional
  public void updateStatus(Long id, String status) {
    Exemplaire exemplaire = findById(id);
    if (exemplaire == null) {
      throw new IllegalArgumentException("Exemplaire not found");
    }
    
    exemplaire.setStatus(status);
    exemplaireRepository.save(exemplaire);
  }
}
