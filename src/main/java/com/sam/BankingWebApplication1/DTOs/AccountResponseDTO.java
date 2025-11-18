package com.sam.BankingWebApplication1.DTOs;

import com.sam.BankingWebApplication1.Enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponseDTO {

    private String accountNumber;
    private AccountType accountType;
    private String accountStatus;
    private Double balance;
}
