package com.sam.BankingWebApplication1.DTOs.CustomerDashboardDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountDTO {
    private String accountNumberMasked;
    private String type;
    private String status;
    private double balance;
    private String currency;
}

