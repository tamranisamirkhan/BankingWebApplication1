package com.sam.BankingWebApplication1.Services;

import com.sam.BankingWebApplication1.DTOs.CreateCustomerDTO;
import com.sam.BankingWebApplication1.DTOs.RegisterCustomerDTO;
import com.sam.BankingWebApplication1.Utils.ResponseModel;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CustomerService {

    ResponseModel createNewCustomer(CreateCustomerDTO customerDTO);



    String registerCustomer(RegisterCustomerDTO customerDTO);

    List<CreateCustomerDTO> getCustomersByStatus(String upperCase);
}
