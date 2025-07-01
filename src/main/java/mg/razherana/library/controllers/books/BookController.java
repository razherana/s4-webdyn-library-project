package mg.razherana.library.controllers.books;

import java.util.List;
import java.util.stream.Collectors;

import mg.razherana.library.models.books.Author;
import mg.razherana.library.models.books.Book;
import mg.razherana.library.models.books.Category;
import mg.razherana.library.services.books.AuthorService;
import mg.razherana.library.services.books.BookService;
import mg.razherana.library.services.books.CategoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/books")
public class BookController {

  @Autowired
  private BookService bookService;

  @Autowired
  private AuthorService authorService;

  @Autowired
  private CategoryService categoryService;

  @GetMapping("")
  public String list(Model model) {
    List<Book> books = bookService.findAllWithAuthorAndCategories();

    model.addAttribute("books", books);
    model.addAttribute("pageTitle", "Book Management");

    return "books/list";
  }

  @GetMapping({ "/add", "/add/" })
  public String addBookView(Model model) {
    List<Author> authors = authorService.findAll();
    List<Category> categories = categoryService.findAll();

    model.addAttribute("authors", authors);
    model.addAttribute("categories", categories);

    return "books/create";
  }

  @PostMapping("/add")
  public String addBook(@RequestParam String title,
      @RequestParam(required = false) Long authorId,
      @RequestParam(required = false) List<Long> categoryIds) {

    bookService.saveBook(title, authorId, categoryIds);
    return "redirect:/books";
  }

  @PostMapping("/update")
  public String updateBook(@RequestParam Long id,
      @RequestParam String title,
      @RequestParam(required = false) Long authorId) {

    bookService.updateBook(id, title, authorId);
    return "redirect:/books";
  }

  @PostMapping("/delete")
  public String deleteBook(@RequestParam Long id) {
    bookService.deleteBook(id);
    return "redirect:/books";
  }

  @GetMapping("/get/{id}")
  @ResponseBody
  public ResponseEntity<Book> getBook(@PathVariable Long id) {
    Book book = bookService.findById(id);
    if (book != null) {
      return ResponseEntity.ok(book);
    }
    return ResponseEntity.notFound().build();
  }

  @GetMapping("/categories/{bookId}")
  @ResponseBody
  public ResponseEntity<List<Long>> getBookCategories(@PathVariable Long bookId) {
    Book book = bookService.findById(bookId);
    if (book != null && book.getCategories() != null) {
      List<Long> categoryIds = book.getCategories().stream()
          .map(Category::getId)
          .collect(Collectors.toList());
      return ResponseEntity.ok(categoryIds);
    }
    return ResponseEntity.ok(List.of());
  }

  @PostMapping("/categories/update")
  public String updateBookCategories(@RequestParam Long bookId,
      @RequestParam(required = false) List<Long> categoryIds) {

    bookService.updateBookCategories(bookId, categoryIds);
    return "redirect:/books";
  }
}
