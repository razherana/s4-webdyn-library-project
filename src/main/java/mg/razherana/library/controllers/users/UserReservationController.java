package mg.razherana.library.controllers.users;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

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

import jakarta.servlet.http.HttpSession;
import mg.razherana.library.models.books.Book;
import mg.razherana.library.models.loans.Membership;
import mg.razherana.library.models.loans.Reservation;
import mg.razherana.library.models.loans.ReservationStatusHistory;
import mg.razherana.library.models.loans.ReservationStatusType;
import mg.razherana.library.models.punishments.Punishment;
import mg.razherana.library.models.users.User;
import mg.razherana.library.repositories.loans.ReservationStatusTypeRepository;
import mg.razherana.library.services.UserService;
import mg.razherana.library.services.books.BookService;
import mg.razherana.library.services.loans.MembershipService;
import mg.razherana.library.services.loans.ReservationService;
import mg.razherana.library.services.punishments.PunishmentService;

@Controller
@RequestMapping("/user/reservations")
public class UserReservationController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private ReservationService reservationService;
    
    @Autowired
    private MembershipService membershipService;
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private ReservationStatusTypeRepository statusTypeRepository;
    
    @Autowired
    private PunishmentService punishmentService;
    
    @GetMapping
    public String list(HttpSession session, Model model) {
        // Get current user
        User currentUser = userService.getCurrentUser(session);
        if (currentUser == null || !currentUser.isPeople()) {
            return "redirect:/auth/login";
        }
        
        // Get user's active memberships
        List<Membership> memberships = membershipService.findByPeople(currentUser.getPeople()).stream()
                .filter(m -> m.getEndDate().isAfter(LocalDate.now()))
                .collect(Collectors.toList());
        
        if (memberships.isEmpty()) {
            model.addAttribute("error", "You don't have any active memberships. Please get a membership first.");
            return "users/reservations/list";
        }
        
        // Get all reservations for all user's memberships
        List<Reservation> allReservations = memberships.stream()
                .flatMap(m -> reservationService.findByMembershipId(m.getId()).stream())
                .collect(Collectors.toList());
        
        // Active/pending reservations
        List<Reservation> activeReservations = allReservations.stream()
                .filter(r -> {
                    ReservationStatusHistory latestStatus = r.getReservationStatusHistories().get(0);
                    String statusName = latestStatus.getReservationStatusType().getName().toLowerCase();
                    return statusName.contains("pending") || statusName.contains("approved");
                })
                .collect(Collectors.toList());
        
        // Past/completed/cancelled reservations
        List<Reservation> pastReservations = allReservations.stream()
                .filter(r -> {
                    ReservationStatusHistory latestStatus = r.getReservationStatusHistories().get(0);
                    String statusName = latestStatus.getReservationStatusType().getName().toLowerCase();
                    return statusName.contains("completed") || statusName.contains("cancelled");
                })
                .collect(Collectors.toList());
        
        model.addAttribute("activeReservations", activeReservations);
        model.addAttribute("pastReservations", pastReservations);
        model.addAttribute("now", LocalDateTime.now());
        model.addAttribute("pageTitle", "My Reservations");
        
        return "users/reservations/list";
    }
    
    @GetMapping("/{id}")
    public String view(@PathVariable Long id, HttpSession session, Model model) {
        // Get current user
        User currentUser = userService.getCurrentUser(session);
        if (currentUser == null || !currentUser.isPeople()) {
            return "redirect:/auth/login";
        }
        
        // Get the reservation
        Reservation reservation = reservationService.findById(id);
        if (reservation == null) {
            return "redirect:/user/reservations";
        }
        
        // Verify reservation belongs to current user
        List<Membership> userMemberships = membershipService.findByPeople(currentUser.getPeople());
        boolean isUserReservation = userMemberships.stream()
                .anyMatch(m -> m.getId().equals(reservation.getMembership().getId()));
        
        if (!isUserReservation) {
            return "redirect:/user/reservations";
        }
        
        // Get reservation status history
        List<ReservationStatusHistory> statusHistory = reservation.getReservationStatusHistories();
        
        model.addAttribute("reservation", reservation);
        model.addAttribute("statusHistory", statusHistory);
        model.addAttribute("now", LocalDateTime.now());
        model.addAttribute("pageTitle", "Reservation Details");
        
        // Check if reservation can be cancelled
        ReservationStatusHistory latestStatus = statusHistory.get(0);
        boolean canCancel = latestStatus.getReservationStatusType().getName().toLowerCase().contains("pending");
        model.addAttribute("canCancel", canCancel);
        
        return "users/reservations/view";
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
                .filter(m -> m.getEndDate().isAfter(LocalDate.now()))
                .collect(Collectors.toList());
        
        if (activeMemberships.isEmpty()) {
            model.addAttribute("error", "You don't have any active memberships. Please get a membership first.");
            return "users/reservations/add";
        }
        
        // Check for active punishments on any membership
        LocalDateTime now = LocalDateTime.now();
        for (Membership membership : activeMemberships) {
            Punishment activePunishment = punishmentService.getActivePunishmentAt(membership.getId(), now);
            if (activePunishment != null) {
                model.addAttribute("error", "You have an active punishment and cannot make reservations at this time.");
                model.addAttribute("punishment", activePunishment);
                return "users/reservations/add";
            }
        }
        
        // Get available books
        List<Book> books = bookService.findAll();
        
        model.addAttribute("memberships", activeMemberships);
        model.addAttribute("books", books);
        model.addAttribute("selectedBookId", bookId);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("minTime", LocalTime.now().plusHours(1).toString());
        model.addAttribute("pageTitle", "Reserve a Book");
        
        return "users/reservations/add";
    }
    
    @PostMapping("/add")
    public String add(
            HttpSession session,
            @RequestParam Long bookId,
            @RequestParam Long membershipId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate reservationDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime reservationTime,
            @RequestParam(defaultValue = "false") boolean takeHome,
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
            return "redirect:/user/reservations/add";
        }
        
        try {
            LocalDateTime reservationDateTime = LocalDateTime.of(reservationDate, reservationTime);

            // Don't allow reservations in the past
            if (reservationDateTime.isBefore(LocalDateTime.now())) {
                redirectAttributes.addFlashAttribute("error", "Cannot make reservations in the past");
                return "redirect:/user/reservations/add";
            }

            // Check for active punishment at reservation time
            if (punishmentService.hasPunishmentAt(membershipId, reservationDateTime)) {
                Punishment activePunishment = punishmentService.getActivePunishmentAt(membershipId, reservationDateTime);
                LocalDateTime endTime = activePunishment.getPunishmentDate()
                        .plusSeconds((long) (activePunishment.getDurationHours() * 3600));

                redirectAttributes.addFlashAttribute("error",
                        "You have a punishment active at the requested reservation time (until " +
                                endTime.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")) +
                                "). Reason: " + activePunishment.getDescription());
                return "redirect:/user/reservations/add";
            }

            // Create the reservation
            reservationService.createReservation(bookId, membershipId, reservationDateTime, takeHome);
            redirectAttributes.addFlashAttribute("success", "Reservation created successfully");
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/reservations/add";
        }
        
        return "redirect:/user/reservations";
    }
    
    @PostMapping("/cancel")
    public String cancel(
            @RequestParam Long reservationId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        // Get current user
        User currentUser = userService.getCurrentUser(session);
        if (currentUser == null || !currentUser.isPeople()) {
            return "redirect:/auth/login";
        }
        
        // Get the reservation
        Reservation reservation = reservationService.findById(reservationId);
        if (reservation == null) {
            redirectAttributes.addFlashAttribute("error", "Reservation not found");
            return "redirect:/user/reservations";
        }
        
        // Verify reservation belongs to current user
        List<Membership> userMemberships = membershipService.findByPeople(currentUser.getPeople());
        boolean isUserReservation = userMemberships.stream()
                .anyMatch(m -> m.getId().equals(reservation.getMembership().getId()));
        
        if (!isUserReservation) {
            redirectAttributes.addFlashAttribute("error", "You don't have permission to cancel this reservation");
            return "redirect:/user/reservations";
        }
        
        try {
            // Find the cancelled status type
            ReservationStatusType cancelledType = statusTypeRepository.findByNameContainingIgnoreCase("Cancelled");

            if (cancelledType == null) {
                throw new IllegalArgumentException("Cancelled status type not found");
            }
            
            // Update status to cancelled
            reservationService.updateStatus(reservationId, cancelledType.getId());
            redirectAttributes.addFlashAttribute("success", "Reservation cancelled successfully");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error cancelling reservation: " + e.getMessage());
        }
        
        return "redirect:/user/reservations";
    }
}
