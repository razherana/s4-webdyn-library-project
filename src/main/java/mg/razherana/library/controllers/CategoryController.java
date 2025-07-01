package mg.razherana.library.controllers;

import mg.razherana.library.services.CategoryService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/categories")
public class CategoryController {

  private final CategoryService categoryService;

  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @PostMapping("/add")
  public String addCategory(@RequestParam String name) {
    categoryService.saveCategory(name);
    return "redirect:/books";
  }

  @PostMapping("/update")
  public String updateCategory(@RequestParam Long id, @RequestParam String name) {
    categoryService.updateCategory(id, name);
    return "redirect:/books";
  }

  @PostMapping("/delete")
  public String deleteCategory(@RequestParam Long id) {
    categoryService.deleteCategory(id);
    return "redirect:/books";
  }
}
