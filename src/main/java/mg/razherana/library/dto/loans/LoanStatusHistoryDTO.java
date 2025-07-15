package mg.razherana.library.dto.loans;

import lombok.Data;

@Data
public class LoanStatusHistoryDTO {
  private String statusType;
  private String date;

  public LoanStatusHistoryDTO(String statusType, String date) {
    this.statusType = statusType;
    this.date = date;
  }
}