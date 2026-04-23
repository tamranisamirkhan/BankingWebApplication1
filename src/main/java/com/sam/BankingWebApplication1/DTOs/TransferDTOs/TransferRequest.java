package com.sam.BankingWebApplication1.DTOs.TransferDTOs;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransferRequest {

    @Positive
    private double amount;
    @NotNull
    private String receiversAccountNumber;
    @NotNull
    private String pin;
    private String description;
}
