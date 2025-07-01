package mg.razherana.library.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.razherana.library.models.Author;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

}
