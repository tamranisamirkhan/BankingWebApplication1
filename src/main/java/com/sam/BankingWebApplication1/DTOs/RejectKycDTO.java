package com.sam.BankingWebApplication1.DTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RejectKycDTO {
    @NotBlank
    private String reason;
}
