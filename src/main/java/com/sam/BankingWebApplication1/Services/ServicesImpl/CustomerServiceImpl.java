package com.sam.BankingWebApplication1.Services.ServicesImpl;

import com.sam.BankingWebApplication1.DTOs.CreateCustomerDTO;
import com.sam.BankingWebApplication1.DTOs.RegisterCustomerDTO;
import com.sam.BankingWebApplication1.Entities.Customer;
import com.sam.BankingWebApplication1.Enums.CustomerStatus;
import com.sam.BankingWebApplication1.Exceptions.DuplicateResourceFoundException;
import com.sam.BankingWebApplication1.Repositories.CustomerRepository;
import com.sam.BankingWebApplication1.Services.CustomerService;
import com.sam.BankingWebApplication1.Services.EmailService;
import com.sam.BankingWebApplication1.Utils.CommonResponse;
import com.sam.BankingWebApplication1.Utils.ResponseModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
            customerRepo.save(customer);
            String subject = "SmartBank - Application Received";
            String message = "Dear " + customer.getFullName() + ",\n\n"
                    + "Thank you for registering with SmartBank.\n"
                    + "Your application has been received successfully.\n"
                    + "Our admin team will review your details and notify you once approved.\n\n"
                    + "Best regards,\nSmartBank Team";

            emailService.sendMail(customer.getEmail(), subject, message);

            return CommonResponse.CREATED("Customer saved successfully and confirmation email sent!");
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
    public String registerCustomer(RegisterCustomerDTO customerDTO) {
        return "";
    }


}
