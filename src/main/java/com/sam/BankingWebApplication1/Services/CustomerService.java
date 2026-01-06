package com.sam.BankingWebApplication1.Services;

import com.sam.BankingWebApplication1.DTOs.CreateCustomerDTO;
import com.sam.BankingWebApplication1.DTOs.RegisterCustomerDTO;
import com.sam.BankingWebApplication1.Enums.KycStatus;
import com.sam.BankingWebApplication1.Utils.ResponseModel;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


public interface CustomerService {

    ResponseModel createNewCustomer(CreateCustomerDTO customerDTO);

    List<CreateCustomerDTO> getKycQueue(KycStatus status);


    String registerCustomer(RegisterCustomerDTO customerDTO);

    List<CreateCustomerDTO> getCustomersByStatus(String upperCase);

    ResponseModel uploadKyc(String token, MultipartFile aadhaarFront, MultipartFile aadhaarBack, MultipartFile panCard) throws IOException;
}
