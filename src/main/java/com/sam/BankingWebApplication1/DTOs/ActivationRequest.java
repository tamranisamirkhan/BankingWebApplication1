package com.sam.BankingWebApplication1.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActivationRequest {

    private String token;

    private String password;
    private String confirmPassword;

    private String transactionPin;
    private String confirmTransactionPin;
}

