package com.sam.BankingWebApplication1.DTOs.CustomerDashboardDTOs;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
        private LocalDate date;
        private String description;
        private String type;
        private double amount;
        private String status;

}
