package mg.razherana.library.controllers.books;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import mg.razherana.library.models.books.Book;
import mg.razherana.library.models.books.Exemplaire;
import mg.razherana.library.repositories.books.BookRepository;
import mg.razherana.library.services.books.ExemplaireService;

@Controller
@RequestMapping("/books/{bookId}/exemplaires")
public class ExemplaireController {

  @Autowired
  private ExemplaireService exemplaireService;

  @Autowired
  private BookRepository bookRepository;

  @GetMapping
  public String listExemplaires(@PathVariable Long bookId, Model model) {
    Book book = bookRepository.findById(bookId).orElse(null);
    if (book == null) {
      return "redirect:/books";
    }

    List<Exemplaire> exemplaires = exemplaireService.findByBookId(bookId);

    model.addAttribute("availableCount", exemplaires.stream().filter(e -> "AVAILABLE".equals(e.getStatus())).count());
    model.addAttribute("book", book);
    model.addAttribute("exemplaires", exemplaires);
    model.addAttribute("pageTitle", "Exemplaires for " + book.getTitle());

    return "books/exemplaires/list";
  }

  @GetMapping("/add")
  public String showAddForm(@PathVariable Long bookId, Model model) {
    Book book = bookRepository.findById(bookId).orElse(null);
    if (book == null) {
      return "redirect:/books";
    }

    model.addAttribute("book", book);
    model.addAttribute("pageTitle", "Add Exemplaire for " + book.getTitle());

    return "books/exemplaires/add";
  }

  @PostMapping("/add")
  public String addExemplaire(
      @PathVariable Long bookId,
      @RequestParam String code,
      @RequestParam(required = false) String location,
      @RequestParam(required = false) String notes,
      RedirectAttributes redirectAttributes) {

    try {
      exemplaireService.create(bookId, code, location, notes);
      redirectAttributes.addFlashAttribute("success", "Exemplaire added successfully.");
    } catch (IllegalArgumentException e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
    }

    return "redirect:/books/" + bookId + "/exemplaires";
  }

  @GetMapping("/{id}/edit")
  public String showEditForm(@PathVariable Long bookId, @PathVariable Long id, Model model) {
    Book book = bookRepository.findById(bookId).orElse(null);
    if (book == null) {
      return "redirect:/books";
    }

    Exemplaire exemplaire = exemplaireService.findById(id);
    if (exemplaire == null || !exemplaire.getBook().getId().equals(bookId)) {
      return "redirect:/books/" + bookId + "/exemplaires";
    }

    model.addAttribute("book", book);
    model.addAttribute("exemplaire", exemplaire);
    model.addAttribute("pageTitle", "Edit Exemplaire");

    return "books/exemplaires/edit";
  }

  @PostMapping("/{id}/update")
  public String updateExemplaire(
      @PathVariable Long bookId,
      @PathVariable Long id,
      @RequestParam String code,
      @RequestParam(required = false) String location,
      @RequestParam(required = false) String notes,
      @RequestParam String status,
      RedirectAttributes redirectAttributes) {

    try {
      exemplaireService.update(id, code, location, notes, status);
      redirectAttributes.addFlashAttribute("success", "Exemplaire updated successfully.");
    } catch (IllegalArgumentException e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
    }

    return "redirect:/books/" + bookId + "/exemplaires";
  }

  @PostMapping("/{id}/delete")
  public String deleteExemplaire(
      @PathVariable Long bookId,
      @PathVariable Long id,
      RedirectAttributes redirectAttributes) {

    try {
      exemplaireService.delete(id);
      redirectAttributes.addFlashAttribute("success", "Exemplaire deleted successfully.");
    } catch (IllegalArgumentException e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
    }

    return "redirect:/books/" + bookId + "/exemplaires";
  }
}
