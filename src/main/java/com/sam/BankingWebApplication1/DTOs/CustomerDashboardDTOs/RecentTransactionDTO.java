package com.sam.BankingWebApplication1.DTOs.CustomerDashboardDTOs;

import lombok.*;

import java.time.LocalDate;
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecentTransactionDTO {

    private LocalDate transactionDate;

    private String description;

    private String type;      // DEBIT / CREDIT

    private double amount;

    private String status;
}
