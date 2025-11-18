package com.sam.BankingWebApplication1.Controllers;


import com.sam.BankingWebApplication1.DTOs.CreateCustomerDTO;
import com.sam.BankingWebApplication1.DTOs.RegisterCustomerDTO;
import com.sam.BankingWebApplication1.Services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/smartBank/account")
@CrossOrigin(origins = {"http://localhost:5500"})
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/createAccount")
    public ResponseEntity<?> createAccount(@RequestBody CreateCustomerDTO request) {
       return null;
    }

    @PostMapping("/registerCustomer")
    public ResponseEntity<?>registerUser(@RequestBody RegisterCustomerDTO request){
        return null;
    }


}

