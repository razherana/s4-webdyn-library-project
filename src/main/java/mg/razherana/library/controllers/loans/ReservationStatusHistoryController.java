package mg.razherana.library.controllers.loans;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mg.razherana.library.models.loans.ReservationStatusHistory;
import mg.razherana.library.services.loans.ReservationStatusHistoryService;

@Controller
@RequestMapping("/reservation-status-history")
public class ReservationStatusHistoryController {

  @Autowired
  private ReservationStatusHistoryService statusHistoryService;

  @GetMapping("")
  public String list(@RequestParam(required = false) Long reservationId, Model model) {
    List<ReservationStatusHistory> statusHistories;

    if (reservationId != null) {
      statusHistories = statusHistoryService.findByReservationId(reservationId);
      model.addAttribute("reservationFilter", reservationId);
    } else {
      statusHistories = statusHistoryService.findAll();
    }

    model.addAttribute("statusHistories", statusHistories);
    return "reservation-status-history/list";
  }
}
