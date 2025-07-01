package mg.razherana.library.repositories.books;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import mg.razherana.library.models.books.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
  @EntityGraph(attributePaths = { "author", "categories" })
  @Query("SELECT b FROM Book b")
  @NonNull
  List<Book> findAll2();
}
