package com.sam.BankingWebApplication1.Controllers;

import com.sam.BankingWebApplication1.DTOs.CreateCustomerDTO;
import com.sam.BankingWebApplication1.Services.AdminService;
import com.sam.BankingWebApplication1.Services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:5500"})
@RestController
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
