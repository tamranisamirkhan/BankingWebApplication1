package com.sam.BankingWebApplication1.DTOs;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterCustomerDTO {
    private String accountNumber;
    private String email;
    private String phoneNumber;
    private String CIFNumber;
    private String BranchCode;
    private String country;
}
