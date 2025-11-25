package com.sam.BankingWebApplication1.Controllers;

import com.sam.BankingWebApplication1.DTOs.CreateCustomerDTO;
import com.sam.BankingWebApplication1.DTOs.RegisterCustomerDTO;
import com.sam.BankingWebApplication1.Services.CustomerService;
import com.sam.BankingWebApplication1.Utils.ResponseModel;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/smartBank/customer")
@CrossOrigin(origins = {
        "https://smartbankofficial.netlify.app",
        "http://localhost:5500"
})
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


}
