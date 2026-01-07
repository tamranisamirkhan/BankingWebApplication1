package com.sam.BankingWebApplication1.Controllers;

import com.sam.BankingWebApplication1.DTOs.CreateCustomerDTO;
import com.sam.BankingWebApplication1.DTOs.KycReviewDTO;
import com.sam.BankingWebApplication1.DTOs.RejectKycDTO;
import com.sam.BankingWebApplication1.Enums.KycStatus;
import com.sam.BankingWebApplication1.Services.AdminService;
import com.sam.BankingWebApplication1.Services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/smartBank/admin")
public class AdminController {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private AdminService adminService;

    @GetMapping("/getAllCustomer")
    public List<CreateCustomerDTO> getAllCustomers(@RequestParam String status) {
        return customerService.getCustomersByStatus(status.toUpperCase());
    }
    @GetMapping("/kyc")
    public ResponseEntity<List<CreateCustomerDTO>> getKycQueue(
            @RequestParam KycStatus status
    ) {
        return ResponseEntity.ok(
                customerService.getKycQueue(status)
        );
    }

    @GetMapping("/kyc/{customerId}")
    public KycReviewDTO getKycDetails(@PathVariable Long customerId) {
        return adminService.getKycDetails(customerId);
    }

    @PostMapping("/kyc/{customerId}/approve")
    public ResponseEntity<?> approveKyc(@PathVariable Long customerId) {
        adminService.approveKyc(customerId);
        return ResponseEntity.ok("KYC approved");
    }

    @PostMapping("/kyc/{customerId}/reject")
    public ResponseEntity<?> rejectKyc(
            @PathVariable Long customerId,
            @RequestBody RejectKycDTO dto
    ) {
        adminService.rejectKyc(customerId, dto.getReason());
        return ResponseEntity.ok("KYC rejected");
    }




    @PostMapping("/approveCustomer/{id}")
    public String createUser(@PathVariable int id){
        return adminService.approveCustomer(id);
    }
    @PostMapping("/rejectCustomer/{id}")
    public String rejectCustomer(@PathVariable int id){
        return adminService.rejectCustomer(id);
    }
    @PostMapping("/inactiveCustomer/{id}")
    public String inactiveCustomer(@PathVariable int id){
        return adminService.inactiveCustomer(id);
    }
    @PostMapping("/activateCustomer/{id}")
    public String activeCustomer(@PathVariable int id){
        return adminService.activeCustomer(id);
    }
    @DeleteMapping("/deleteCustomer/{id}")
    public String deleteCustomer(@PathVariable int id){
        return adminService.deleteCustomer(id);
    }






}
