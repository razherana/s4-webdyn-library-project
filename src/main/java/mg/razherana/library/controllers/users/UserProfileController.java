package mg.razherana.library.controllers.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import mg.razherana.library.models.loans.People;
import mg.razherana.library.models.users.User;
import mg.razherana.library.services.UserService;
import mg.razherana.library.services.loans.PeopleService;

@Controller
@RequestMapping("/user/profile")
public class UserProfileController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private PeopleService peopleService;
    
    @GetMapping
    public String viewProfile(HttpSession session, Model model) {
        // Get current user
        User currentUser = userService.getCurrentUser(session);
        if (currentUser == null || !currentUser.isPeople()) {
            return "redirect:/auth/login";
        }
        
        // Get user's people data
        People people = currentUser.getPeople();
        
        model.addAttribute("user", currentUser);
        model.addAttribute("people", people);
        model.addAttribute("pageTitle", "My Profile");
        
        return "users/profile/view";
    }
    
    @GetMapping("/edit")
    public String editProfileForm(HttpSession session, Model model) {
        // Get current user
        User currentUser = userService.getCurrentUser(session);
        if (currentUser == null || !currentUser.isPeople()) {
            return "redirect:/auth/login";
        }
        
        // Get user's people data
        People people = currentUser.getPeople();
        
        model.addAttribute("user", currentUser);
        model.addAttribute("people", people);
        model.addAttribute("pageTitle", "Edit Profile");
        
        return "users/profile/edit";
    }
    
    @PostMapping("/update")
    public String updateProfile(
            HttpSession session,
            @RequestParam String name,
            @RequestParam String address,
            @RequestParam(required = false) String username,
            RedirectAttributes redirectAttributes) {
        
        // Get current user
        User currentUser = userService.getCurrentUser(session);
        if (currentUser == null || !currentUser.isPeople()) {
            return "redirect:/auth/login";
        }
        
        try {
            // Get and update people entity
            People people = currentUser.getPeople();
            people.setName(name);
            people.setAddress(address);
            
            // Save the updated people entity
            peopleService.save(people);
            
            // Update username if provided and different from current
            if (username != null && !username.isEmpty() && !currentUser.getUsername().equals(username)) {
                currentUser.setUsername(username);
                userService.save(currentUser);
            }
            
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating profile: " + e.getMessage());
        }
        
        return "redirect:/user/profile";
    }
    
    @GetMapping("/change-password")
    public String changePasswordForm(HttpSession session, Model model) {
        // Get current user
        User currentUser = userService.getCurrentUser(session);
        if (currentUser == null || !currentUser.isPeople()) {
            return "redirect:/auth/login";
        }
        
        model.addAttribute("user", currentUser);
        model.addAttribute("pageTitle", "Change Password");
        
        return "users/profile/change-password";
    }
    
    @PostMapping("/change-password")
    public String changePassword(
            HttpSession session,
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {
        
        // Get current user
        User currentUser = userService.getCurrentUser(session);
        if (currentUser == null || !currentUser.isPeople()) {
            return "redirect:/auth/login";
        }
        
        try {
            // Validate input
            if (!newPassword.equals(confirmPassword)) {
                throw new IllegalArgumentException("New password and confirmation do not match");
            }
            
            if (newPassword.length() < 6) {
                throw new IllegalArgumentException("New password must be at least 6 characters long");
            }
            
            // Verify current password
            if (!userService.verifyPassword(currentUser, currentPassword)) {
                throw new IllegalArgumentException("Current password is incorrect");
            }
            
            // Update password
            userService.updatePassword(currentUser, newPassword);
            
            redirectAttributes.addFlashAttribute("success", "Password changed successfully");
            return "redirect:/user/profile";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/profile/change-password";
        }
    }
}
