package com.sam.BankingWebApplication1.Services.ServicesImpl;

import com.sam.BankingWebApplication1.Entities.ActivationToken;
import com.sam.BankingWebApplication1.Entities.Customer;
import com.sam.BankingWebApplication1.Enums.CustomerStatus;
import com.sam.BankingWebApplication1.Exceptions.ResourceNotFoundException;
import com.sam.BankingWebApplication1.Repositories.AccountRepository;
import com.sam.BankingWebApplication1.Repositories.ActivationTokenRepository;
import com.sam.BankingWebApplication1.Repositories.CustomerRepository;
import com.sam.BankingWebApplication1.Repositories.UserRepository;
import com.sam.BankingWebApplication1.Services.AccountService;
import com.sam.BankingWebApplication1.Services.AdminService;
import com.sam.BankingWebApplication1.Services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ActivationTokenRepository activationTokenRepository;

    @Autowired
    private EmailService emailService;

    @Override
    public String approveCustomer(int id) {
        Customer customer = customerRepository.findById((long) id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // 1 Mark approved
        customer.setStatus(CustomerStatus.APPROVED);
        customerRepository.save(customer);

        // 2 Create activation token
        String token = UUID.randomUUID().toString();
        ActivationToken activationToken = new ActivationToken();
        activationToken.setToken(token);
        activationToken.setCustomer(customer);
        activationToken.setExpiryAt(LocalDateTime.now().plusHours(24));
        activationTokenRepository.save(activationToken);

        // 3 Send email
        String link = "https://smartbankofficial.netlify.app/pages/active-account.html?token=" + token;
        String subject = "SmartBank Account Approved - Set Up Your Login";
        String message = "Dear " + customer.getFullName() + ",\n\n"
                + "Your SmartBank account has been approved!\n"
                + "Click the link below to set your username and password:\n"
                + link + "\n\n"
                + "This link will expire in 24 hours.\n\n"
                + "Regards,\nSmartBank Team";

        emailService.sendMail(customer.getEmail(), subject, message);
        return "Customer approved and activation link sent!";
    }



    @Override
    public String rejectCustomer(int id) {
        Customer customer = customerRepository.findById((long)id).orElseThrow(()->
                new ResourceNotFoundException("Customer","Customer Not Found : ", +id));
        customer.setStatus(CustomerStatus.REJECTED);
        customerRepository.save(customer);
        return "Done";
    }

    @Override
    public String inactiveCustomer(int id) {
        Customer customer = customerRepository.findById((long)id).orElseThrow(()->
                new RuntimeException("Customer Not Found"));
        customer.setStatus(CustomerStatus.INACTIVE);
        customerRepository.save(customer);
        return "Done";
    }

    @Override
    public String activeCustomer(int id) {
        Customer customer = customerRepository.findById((long)id).orElseThrow(()->
                new RuntimeException("Customer Not Found"));
        customer.setStatus(CustomerStatus.ACTIVE);
        customerRepository.save(customer);
        return "Done";
    }

    @Override
    public String deleteCustomer(int id) {
      customerRepository.deleteById((long)id);
      return "Done";
    }

}
