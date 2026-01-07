package com.sam.BankingWebApplication1.Services;

import com.sam.BankingWebApplication1.DTOs.KycReviewDTO;
import jakarta.validation.constraints.NotBlank;

public interface AdminService {
    String approveCustomer(int id);


    String rejectCustomer(int id);

    String inactiveCustomer(int id);

    String activeCustomer(int id);

    String deleteCustomer(int id);

    KycReviewDTO getKycDetails(Long customerId);

    void approveKyc(Long customerId);

    void rejectKyc(Long customerId, @NotBlank String reason);
}
