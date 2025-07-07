package mg.razherana.library.controllers.users;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import mg.razherana.library.models.loans.Membership;
import mg.razherana.library.models.loans.MembershipType;
import mg.razherana.library.models.loans.People;
import mg.razherana.library.models.users.User;
import mg.razherana.library.repositories.loans.MembershipTypeRepository;
import mg.razherana.library.services.UserService;
import mg.razherana.library.services.loans.MembershipService;

@Controller
@RequestMapping("/user/memberships")
public class UserMembershipController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private MembershipService membershipService;
    
    @Autowired
    private MembershipTypeRepository membershipTypeRepository;
    
    @GetMapping("")
    public String list(HttpSession session, Model model) {
        // Get current user
        User currentUser = userService.getCurrentUser(session);
        if (currentUser == null || !currentUser.isPeople()) {
            return "redirect:/auth/login";
        }
        
        People people = currentUser.getPeople();
        List<Membership> memberships = membershipService.findByPeople(people);
        
        model.addAttribute("people", people);
        model.addAttribute("memberships", memberships);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("pageTitle", "My Memberships");
        
        return "users/memberships/list";
    }
    
    @GetMapping("/add")
    public String addForm(HttpSession session, Model model) {
        // Get current user
        User currentUser = userService.getCurrentUser(session);
        if (currentUser == null || !currentUser.isPeople()) {
            return "redirect:/auth/login";
        }
        
        People people = currentUser.getPeople();
        List<MembershipType> types = membershipTypeRepository.findAll();
        
        model.addAttribute("people", people);
        model.addAttribute("membershipTypes", types);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("pageTitle", "New Membership");
        
        return "users/memberships/add";
    }
    
    @PostMapping("/add")
    public String add(
            HttpSession session,
            @RequestParam Long membershipTypeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            RedirectAttributes redirectAttributes) {
        
        // Get current user
        User currentUser = userService.getCurrentUser(session);
        if (currentUser == null || !currentUser.isPeople()) {
            return "redirect:/auth/login";
        }
        
        People people = currentUser.getPeople();
        
        try {
            // Validate dates
            if (startDate.isAfter(endDate)) {
                throw new IllegalArgumentException("Start date must be before end date");
            }
            
            // Check for overlapping memberships
            List<Membership> existingMemberships = membershipService.findByPeople(people);
            for (Membership existing : existingMemberships) {
                // Check if dates overlap
                if (!(endDate.isBefore(existing.getStartDate()) || startDate.isAfter(existing.getEndDate()))) {
                    throw new IllegalArgumentException("New membership overlaps with an existing membership period");
                }
            }
            
            // Create membership
            membershipService.register(people.getId(), membershipTypeId, startDate, endDate);
            redirectAttributes.addFlashAttribute("success", "Membership created successfully");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/memberships/add";
        }
        
        return "redirect:/user/memberships";
    }
}
