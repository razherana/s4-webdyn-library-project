package mg.razherana.library.controllers.admin;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import mg.razherana.library.models.loans.ExtendLoan;
import mg.razherana.library.services.loans.ExtendLoanService;
import mg.razherana.library.services.loans.ReservationService;

@Controller
@RequestMapping("/admin/extend-loans")
public class ExtendLoanController {

    @Autowired
    private ExtendLoanService extendLoanService;
    
    @Autowired
    private ReservationService reservationService;
    
    @GetMapping("")
    public String list(Model model) {
        List<ExtendLoan> allRequests = extendLoanService.findAll();
        List<ExtendLoan> pendingRequests = extendLoanService.findPending();
        
        model.addAttribute("allRequests", allRequests);
        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("now", LocalDateTime.now());
        
        return "admin/extend-loans/list";
    }
    
    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            ExtendLoan extendLoan = extendLoanService.findById(id);
            if (extendLoan == null) {
                redirectAttributes.addFlashAttribute("error", "Extension request not found");
                return "redirect:/admin/extend-loans";
            }
            
            // Check if there are confirmed reservations for this book
            boolean hasConfirmedReservations = !reservationService
                    .findConfirmedReservationsForBook(extendLoan.getLoan().getBook().getId())
                    .isEmpty();
            
            model.addAttribute("extendLoan", extendLoan);
            model.addAttribute("hasConfirmedReservations", hasConfirmedReservations);
            model.addAttribute("now", LocalDateTime.now());
            
            return "admin/extend-loans/view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/extend-loans";
        }
    }
    
    @PostMapping("/approve")
    public String approve(
            @RequestParam Long extendLoanId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime extensionTime,
            RedirectAttributes redirectAttributes) {
        
        try {
            extendLoanService.approveExtension(extendLoanId, extensionTime);
            redirectAttributes.addFlashAttribute("success", "Extension request approved successfully");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/admin/extend-loans";
    }
    
    @PostMapping("/reject")
    public String reject(
            @RequestParam Long extendLoanId,
            RedirectAttributes redirectAttributes) {
        
        try {
            extendLoanService.rejectExtension(extendLoanId);
            redirectAttributes.addFlashAttribute("success", "Extension request rejected successfully");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/admin/extend-loans";
    }
}
