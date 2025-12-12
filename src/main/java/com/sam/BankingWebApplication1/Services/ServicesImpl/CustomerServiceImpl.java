package com.sam.BankingWebApplication1.Services.ServicesImpl;

import com.sam.BankingWebApplication1.DTOs.CreateCustomerDTO;
import com.sam.BankingWebApplication1.DTOs.RegisterCustomerDTO;
import com.sam.BankingWebApplication1.Entities.Customer;
import com.sam.BankingWebApplication1.Enums.CustomerStatus;
import com.sam.BankingWebApplication1.Enums.KycStatus;
import com.sam.BankingWebApplication1.Exceptions.DuplicateResourceFoundException;
import com.sam.BankingWebApplication1.Exceptions.ResourceNotFoundException;
import com.sam.BankingWebApplication1.Repositories.CustomerRepository;
import com.sam.BankingWebApplication1.Services.CustomerService;
import com.sam.BankingWebApplication1.Services.EmailService;
import com.sam.BankingWebApplication1.Utils.CommonResponse;
import com.sam.BankingWebApplication1.Utils.ResponseModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private EmailService emailService;

    @Override
    public ResponseModel createNewCustomer(CreateCustomerDTO customerDTO){

        if (customerRepo.existsByEmail(customerDTO.getEmail())) {
            throw new DuplicateResourceFoundException("Email already exists");
        }
        if (customerRepo.existsByPhoneNumber(customerDTO.getPhoneNumber())) {
            throw new DuplicateResourceFoundException("Phone number already exists");
        }
        if (customerRepo.existsByAadharNumber(customerDTO.getAadharNumber())) {
            throw new DuplicateResourceFoundException("Aadhar number already exists");
        }
        if (customerRepo.existsByPanNumber(customerDTO.getPanNumber())) {
            throw new DuplicateResourceFoundException("PAN number already exists");
        }
        try {
            Customer customer = mapper.map(customerDTO,Customer.class);
            customer.setStatus(CustomerStatus.PENDING);
            Customer savedCustomer =  customerRepo.save(customer);
            String subject = "SmartBank - Application Received";
            String message = "Dear " + customer.getFullName() + ",\n\n"
                    + "Thank you for registering with SmartBank.\n"
                    + "Your application has been received successfully.\n"
                    + "Our admin team will review your details and notify you once approved.\n\n"
                    + "Best regards,\nSmartBank Team";

            emailService.sendMail(customer.getEmail(), subject, message);

            return CommonResponse.CREATED(savedCustomer.getId());
        } catch (Exception e) {
            return CommonResponse.BAD_REQUEST("Customer is not saved");
        }
    }

    @Override
    public List<CreateCustomerDTO> getCustomersByStatus(String status) {
        CustomerStatus enumStatus = CustomerStatus.valueOf(status.toUpperCase());

        List<Customer> customers = customerRepo.findByStatus(enumStatus);

        return customers.stream()
                .map(customer -> mapper.map(customer, CreateCustomerDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public ResponseModel uploadKyc(Long customerId, MultipartFile aadhaarFront, MultipartFile aadhaarBack, MultipartFile panCard) {

        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer","Customer Not Found : ", customerId));

        String basePath = "/home/ubuntu/BankingWebApplication1/uploads/kyc/" + customerId;
        File directory = new File(basePath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String frontPath = basePath + "/aadhaar_front.jpg";
        String backPath = basePath + "/aadhaar_back.jpg";
        String panPath = basePath + "/pan.jpg";

        try {
            aadhaarFront.transferTo(new File(frontPath));
            aadhaarBack.transferTo(new File(backPath));
            panCard.transferTo(new File(panPath));
        } catch (IOException e) {
            throw new RuntimeException("Error uploading KYC documents", e);
        }

        customer.setAadhaarFrontPath(frontPath);
        customer.setAadhaarBackPath(backPath);
        customer.setPanPath(panPath);
        customer.setKycStatus(KycStatus.PENDING);

        customerRepo.save(customer);

        return CommonResponse.OK("KYC documents uploaded successfully. Pending verification.");
    }

    @Override
    public String registerCustomer(RegisterCustomerDTO customerDTO) {
        return "";
    }


}
