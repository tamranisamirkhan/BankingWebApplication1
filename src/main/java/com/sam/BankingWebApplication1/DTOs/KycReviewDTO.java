package com.sam.BankingWebApplication1.DTOs;

import com.sam.BankingWebApplication1.Enums.CustomerStatus;
import com.sam.BankingWebApplication1.Enums.KycStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KycReviewDTO {

    private Long customerId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Date bod;
    private String address;
    private KycStatus kycStatus;
    private CustomerStatus customerStatus;
}

