package com.sam.BankingWebApplication1.DTOs.CustomerDashboardDTOs;

import lombok.*;

import java.util.List;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponseDTO {
        private UserDTO user;
        private AccountDTO account;
        private List<RecentTransactionDTO> recentTransactions;
}
