package com.sam.BankingWebApplication1.Controllers;

import com.sam.BankingWebApplication1.DTOs.CreateCustomerDTO;
import com.sam.BankingWebApplication1.DTOs.RegisterCustomerDTO;
import com.sam.BankingWebApplication1.Security.KycPrincipal;
import com.sam.BankingWebApplication1.Services.CustomerService;
import com.sam.BankingWebApplication1.Utils.ResponseModel;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/smartBank/customer")

public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @PostMapping("/createCustomer")
    public ResponseModel createCustomer(@Valid @RequestBody CreateCustomerDTO customerDTO){
       return customerService.createNewCustomer(customerDTO);
    }


    @PostMapping("/registerCustomer")
    public String registerCustomer(@RequestBody RegisterCustomerDTO customerDTO){
        return customerService.registerCustomer(customerDTO);
    }
    @PostMapping(
            value = "/upload/kyc",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("hasRole('KYC_PENDING')")
    public ResponseModel uploadKyc(
            Authentication authentication,
            @RequestParam MultipartFile aadhaarFront,
            @RequestParam MultipartFile aadhaarBack,
            @RequestParam MultipartFile panCard
    ) throws IOException {

        // 🔐 Identity comes from JWT, not request
        KycPrincipal principal = (KycPrincipal) authentication.getPrincipal();
        Long customerId = principal.getCustomerId();

        return customerService.uploadKyc(customerId,
                aadhaarFront,
                aadhaarBack,
                panCard
        );
    }


}
