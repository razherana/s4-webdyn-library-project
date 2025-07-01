package mg.razherana.library.services.books;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mg.razherana.library.models.books.Author;
import mg.razherana.library.repositories.books.AuthorRepository;

@Service
public class AuthorService {

  @Autowired
  private AuthorRepository authorRepository;

  public List<Author> findAll() {
    return authorRepository.findAll();
  }
}
