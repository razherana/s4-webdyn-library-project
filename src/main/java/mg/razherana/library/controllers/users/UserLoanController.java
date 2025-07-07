package mg.razherana.library.controllers.users;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import mg.razherana.library.models.books.Book;
import mg.razherana.library.models.loans.ExtendLoan;
import mg.razherana.library.models.loans.Loan;
import mg.razherana.library.models.loans.LoanStatusHistory;
import mg.razherana.library.models.loans.LoanType;
import mg.razherana.library.models.loans.Membership;
import mg.razherana.library.models.punishments.Punishment;
import mg.razherana.library.models.users.User;
import mg.razherana.library.repositories.books.BookRepository;
import mg.razherana.library.repositories.loans.LoanTypeRepository;
import mg.razherana.library.services.UserService;
import mg.razherana.library.services.loans.ExtendLoanService;
import mg.razherana.library.services.loans.LoanService;
import mg.razherana.library.services.loans.MembershipService;
import mg.razherana.library.services.punishments.PunishmentService;

@Controller
@RequestMapping("/user/loans")
public class UserLoanController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private LoanService loanService;
    
    @Autowired
    private MembershipService membershipService;
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private LoanTypeRepository loanTypeRepository;
    
    @Autowired
    private PunishmentService punishmentService;
    
    @Autowired
    private ExtendLoanService extendLoanService;
    
    @GetMapping
    public String list(HttpSession session, Model model) {
        // Get current user
        User currentUser = userService.getCurrentUser(session);
        if (currentUser == null || !currentUser.isPeople()) {
            return "redirect:/auth/login";
        }
        
        // Get user's active memberships
        List<Membership> memberships = membershipService.findByPeople(currentUser.getPeople()).stream()
                .filter(m -> m.getEndDate().isAfter(java.time.LocalDate.now()))
                .collect(Collectors.toList());
        
        if (memberships.isEmpty()) {
            model.addAttribute("error", "You don't have any active memberships.");
            return "users/loans/list";
        }
        
        // Get all loans for all user's memberships
        List<Loan> allLoans = memberships.stream()
                .flatMap(m -> loanService.findByMembershipId(m.getId()).stream())
                .collect(Collectors.toList());
        
        // Active loans (not returned)
        List<Loan> activeLoans = allLoans.stream()
                .filter(loan -> loan.getReturnDate() == null)
                .collect(Collectors.toList());
        
        // History (returned loans)
        List<Loan> loanHistory = allLoans.stream()
                .filter(loan -> loan.getReturnDate() != null)
                .collect(Collectors.toList());
        
        model.addAttribute("activeLoans", activeLoans);
        model.addAttribute("loanHistory", loanHistory);
        model.addAttribute("now", LocalDateTime.now());
        model.addAttribute("pageTitle", "My Loans");
        
        return "users/loans/list";
    }
    
    @GetMapping("/add")
    public String addForm(
            HttpSession session, 
            @RequestParam(required = false) Long bookId,
            Model model) {
        
        // Get current user
        User currentUser = userService.getCurrentUser(session);
        if (currentUser == null || !currentUser.isPeople()) {
            return "redirect:/auth/login";
        }
        
        // Get user's active memberships
        List<Membership> activeMemberships = membershipService.findByPeople(currentUser.getPeople()).stream()
                .filter(m -> m.getEndDate().isAfter(java.time.LocalDate.now()))
                .collect(Collectors.toList());
        
        if (activeMemberships.isEmpty()) {
            model.addAttribute("error", "You don't have any active memberships. Please get a membership first.");
            return "users/loans/add";
        }
        
        // Check for active punishments on any membership
        LocalDateTime now = LocalDateTime.now();
        for (Membership membership : activeMemberships) {
            Punishment activePunishment = punishmentService.getActivePunishmentAt(membership.getId(), now);
            if (activePunishment != null) {
                model.addAttribute("error", "You have an active punishment and cannot borrow books at this time.");
                model.addAttribute("punishment", activePunishment);
                return "users/loans/add";
            }
        }
        
        // Get available books (not borrowed)
        List<Book> availableBooks = bookRepository.findAll().stream()
                .filter(book -> loanService.isBookAvailable(book.getId()))
                .collect(Collectors.toList());
        
        // Get loan types
        List<LoanType> loanTypes = loanTypeRepository.findAll();
        
        model.addAttribute("memberships", activeMemberships);
        model.addAttribute("books", availableBooks);
        model.addAttribute("loanTypes", loanTypes);
        model.addAttribute("selectedBookId", bookId);
        model.addAttribute("pageTitle", "Borrow a Book");
        
        return "users/loans/add";
    }
    
    @PostMapping("/add")
    public String add(
            HttpSession session,
            @RequestParam Long bookId,
            @RequestParam Long membershipId,
            @RequestParam Long loanTypeId,
            RedirectAttributes redirectAttributes) {
        
        // Get current user
        User currentUser = userService.getCurrentUser(session);
        if (currentUser == null || !currentUser.isPeople()) {
            return "redirect:/auth/login";
        }
        
        // Verify membership belongs to current user
        List<Membership> userMemberships = membershipService.findByPeople(currentUser.getPeople());
        boolean isValidMembership = userMemberships.stream()
                .anyMatch(m -> m.getId().equals(membershipId));
        
        if (!isValidMembership) {
            redirectAttributes.addFlashAttribute("error", "Invalid membership selection");
            return "redirect:/user/loans/add";
        }
        
        try {
            // Create the loan
            loanService.createLoan(bookId, membershipId, loanTypeId);
            redirectAttributes.addFlashAttribute("success", "Book borrowed successfully!");
            return "redirect:/user/loans";
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/loans/add";
        }
    }
    
    @GetMapping("/{id}")
    public String view(@PathVariable Long id, HttpSession session, Model model) {
        // Get current user
        User currentUser = userService.getCurrentUser(session);
        if (currentUser == null || !currentUser.isPeople()) {
            return "redirect:/auth/login";
        }
        
        // Get the loan
        Loan loan = loanService.findById(id);
        if (loan == null) {
            return "redirect:/user/loans";
        }
        
        // Verify loan belongs to current user
        List<Membership> userMemberships = membershipService.findByPeople(currentUser.getPeople());
        boolean isUserLoan = userMemberships.stream()
                .anyMatch(m -> m.getId().equals(loan.getMembership().getId()));
        
        if (!isUserLoan) {
            return "redirect:/user/loans";
        }
        
        // Get loan status history
        List<LoanStatusHistory> statusHistory = loanService.getStatusHistory(id);
        
        // Get extension history
        List<ExtendLoan> extensions = extendLoanService.findByLoanId(id);
        
        // Get pending extensions
        List<ExtendLoan> pendingExtensions = extensions.stream()
                .filter(e -> e.getConfirmedAt() == null && e.getRejectedAt() == null)
                .collect(Collectors.toList());
        
        model.addAttribute("loan", loan);
        model.addAttribute("statusHistory", statusHistory);
        model.addAttribute("extensions", extensions);
        model.addAttribute("pendingExtensions", pendingExtensions);
        model.addAttribute("now", LocalDateTime.now());
        model.addAttribute("pageTitle", "Loan Details");
        
        return "users/loans/view";
    }
    
    @PostMapping("/extend")
    public String requestExtension(
            @RequestParam Long loanId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        // Get current user
        User currentUser = userService.getCurrentUser(session);
        if (currentUser == null || !currentUser.isPeople()) {
            return "redirect:/auth/login";
        }
        
        // Get the loan
        Loan loan = loanService.findById(loanId);
        if (loan == null) {
            redirectAttributes.addFlashAttribute("error", "Loan not found");
            return "redirect:/user/loans";
        }
        
        // Verify loan belongs to current user
        List<Membership> userMemberships = membershipService.findByPeople(currentUser.getPeople());
        boolean isUserLoan = userMemberships.stream()
                .anyMatch(m -> m.getId().equals(loan.getMembership().getId()));
        
        if (!isUserLoan) {
            redirectAttributes.addFlashAttribute("error", "You don't have permission to extend this loan");
            return "redirect:/user/loans";
        }
        
        try {
            // Request the extension
            extendLoanService.requestExtension(loanId);
            redirectAttributes.addFlashAttribute("success", "Extension request submitted successfully");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/user/loans/" + loanId;
    }
    
    @PostMapping("/cancel-extension")
    public String cancelExtension(
            @RequestParam Long extensionId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        // Get current user
        User currentUser = userService.getCurrentUser(session);
        if (currentUser == null || !currentUser.isPeople()) {
            return "redirect:/auth/login";
        }
        
        try {
            // Get the extension request
            ExtendLoan extension = extendLoanService.findById(extensionId);
            if (extension == null) {
                redirectAttributes.addFlashAttribute("error", "Extension request not found");
                return "redirect:/user/loans";
            }
            
            // Verify loan belongs to current user
            Loan loan = extension.getLoan();
            List<Membership> userMemberships = membershipService.findByPeople(currentUser.getPeople());
            boolean isUserLoan = userMemberships.stream()
                    .anyMatch(m -> m.getId().equals(loan.getMembership().getId()));
            
            if (!isUserLoan) {
                redirectAttributes.addFlashAttribute("error", "You don't have permission to cancel this extension request");
                return "redirect:/user/loans";
            }
            
            // Delete the extension request
            extendLoanService.deleteExtension(extensionId);
            redirectAttributes.addFlashAttribute("success", "Extension request cancelled successfully");
            
            return "redirect:/user/loans/" + loan.getId();
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/loans";
        }
    }
}
