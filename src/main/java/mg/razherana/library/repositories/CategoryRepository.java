package mg.razherana.library.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.razherana.library.models.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
