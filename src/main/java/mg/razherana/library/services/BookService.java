package mg.razherana.library.services;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import jakarta.transaction.Transactional;
import mg.razherana.library.models.Author;
import mg.razherana.library.models.Book;
import mg.razherana.library.models.Category;
import mg.razherana.library.repositories.AuthorRepository;
import mg.razherana.library.repositories.BookRepository;
import mg.razherana.library.repositories.CategoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService {

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private AuthorRepository authorRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  public List<Book> findAll() {
    return bookRepository.findAll();
  }

  public Book findById(Long id) {
    return bookRepository.findById(id).orElse(null);
  }

  @Transactional
  public Book saveBook(String title, Long authorId, List<Long> categoryIds) {
    Book book = new Book();
    book.setTitle(title);

    if (authorId != null) {
      Optional<Author> author = authorRepository.findById(authorId);
      author.ifPresent(book::setAuthor);
    } else {

    }

    Book savedBook = bookRepository.save(book);

    if (categoryIds != null && !categoryIds.isEmpty()) {
      updateBookCategories(savedBook.getId(), categoryIds);
    }

    return savedBook;
  }

  @Transactional
  public Book updateBook(Long id, String title, Long authorId) {
    Optional<Book> optionalBook = bookRepository.findById(id);

    if (optionalBook.isPresent()) {
      Book book = optionalBook.get();
      book.setTitle(title);

      if (authorId != null) {
        Optional<Author> author = authorRepository.findById(authorId);
        author.ifPresent(book::setAuthor);
      } else {
        book.setAuthor(null);
      }

      return bookRepository.save(book);
    }

    return null;
  }

  @Transactional
  public void deleteBook(Long id) {
    bookRepository.deleteById(id);
  }

  @Transactional
  public Book updateBookCategories(Long bookId, List<Long> categoryIds) {
    Optional<Book> optionalBook = bookRepository.findById(bookId);

    if (optionalBook.isPresent()) {
      Book book = optionalBook.get();

      // First remove book from all existing categories
      if (book.getCategories() != null) {
        for (Category category : book.getCategories()) {
          if (category.getBooks() != null) {
            category.getBooks().remove(book);
          }
        }
      }

      // Then add to selected categories
      Set<Category> categories = new HashSet<>();

      if (categoryIds != null) {
        for (Long categoryId : categoryIds) {
          Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
          optionalCategory.ifPresent(category -> {
            categories.add(category);
            if (category.getBooks() == null) {
              category.setBooks(new HashSet<>());
            }
            category.getBooks().add(book);
          });
        }
      }

      book.setCategories(categories);
      return bookRepository.save(book);
    }

    return null;
  }

  public List<Book> findAllWithAuthorAndCategories() {
    return bookRepository.findAll2();
  }
}
