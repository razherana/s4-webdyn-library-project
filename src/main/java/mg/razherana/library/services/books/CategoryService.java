package mg.razherana.library.services.books;

import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;
import mg.razherana.library.models.books.Category;
import mg.razherana.library.repositories.books.CategoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

  @Autowired
  private CategoryRepository categoryRepository;

  public List<Category> findAll() {
    return categoryRepository.findAll();
  }

  public Category findById(Long id) {
    return categoryRepository.findById(id).orElse(null);
  }

  @Transactional
  public Category saveCategory(String name) {
    Category category = new Category();
    category.setName(name);
    return categoryRepository.save(category);
  }

  @Transactional
  public Category updateCategory(Long id, String name) {
    Optional<Category> optionalCategory = categoryRepository.findById(id);

    if (optionalCategory.isPresent()) {
      Category category = optionalCategory.get();
      category.setName(name);
      return categoryRepository.save(category);
    }

    return null;
  }

  @Transactional
  public void deleteCategory(Long id) {
    categoryRepository.deleteById(id);
  }
}
