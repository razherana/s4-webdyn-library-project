package mg.razherana.library.controllers.users;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import mg.razherana.library.models.books.Book;
import mg.razherana.library.models.books.Category;
import mg.razherana.library.models.users.User;
import mg.razherana.library.services.UserService;
import mg.razherana.library.services.books.BookService;
import mg.razherana.library.services.books.CategoryService;
import mg.razherana.library.services.loans.LoanService;

@Controller
@RequestMapping("/user/books")
public class UserBookController {

    @Autowired
    private BookService bookService;
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private LoanService loanService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public String list(
            HttpSession session,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean availableOnly,
            Model model) {
        
        // Get current user
        User currentUser = userService.getCurrentUser(session);
        if (currentUser == null || !currentUser.isPeople()) {
            return "redirect:/auth/login";
        }
        
        // Get books with filters
        List<Book> books = bookService.findAllWithAuthorAndCategories();
        
        if (search != null && !search.isEmpty()) {
            final var searchFinal = search.trim().toLowerCase();
            books = books.stream()
                    .filter(book -> book.getTitle().toLowerCase().contains(searchFinal)
                            || book.getAuthor().getName().toLowerCase().contains(searchFinal)
                            || book.getCategories().stream()
                                    .anyMatch(category -> category.getName().toLowerCase().contains(searchFinal)))
                    .toList();
        }

        // Filter by category if provided
        if (categoryId != null) {
            books = books.stream()
                    .filter(book -> book.getCategories().stream()
                            .anyMatch(category -> category.getId().equals(categoryId)))
                    .toList();
        }

        
        // Get borrowed book IDs
        Set<Long> borrowedBookIds = loanService.getBorrowedBookIds();
        
        // Filter by availability if needed
        if (availableOnly != null && availableOnly) {
            books = books.stream()
                    .filter(book -> !borrowedBookIds.contains(book.getId()))
                    .toList();
        }
        
        // Get all categories for filter
        List<Category> categories = categoryService.findAll();
        
        model.addAttribute("books", books);
        model.addAttribute("categories", categories);
        model.addAttribute("borrowedBookIds", borrowedBookIds);
        model.addAttribute("searchTerm", search);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("availableOnly", availableOnly);
        model.addAttribute("pageTitle", "Book Catalog");
        
        return "users/books/list";
    }
    
    @GetMapping("/search")
    public String search(HttpSession session, Model model) {
        // Get current user
        User currentUser = userService.getCurrentUser(session);
        if (currentUser == null || !currentUser.isPeople()) {
            return "redirect:/auth/login";
        }
        
        List<Category> categories = categoryService.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("pageTitle", "Search Books");
        
        return "users/books/search";
    }
}
