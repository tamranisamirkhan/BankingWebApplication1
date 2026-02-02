package com.sam.BankingWebApplication1.Services.ServicesImpl;

import com.sam.BankingWebApplication1.DTOs.KycReviewDTO;
import com.sam.BankingWebApplication1.Entities.ActivationToken;
import com.sam.BankingWebApplication1.Entities.Customer;
import com.sam.BankingWebApplication1.Enums.CustomerStatus;
import com.sam.BankingWebApplication1.Enums.KycStatus;
import com.sam.BankingWebApplication1.Exceptions.ResourceNotFoundException;
import com.sam.BankingWebApplication1.Repositories.AccountRepository;
import com.sam.BankingWebApplication1.Repositories.ActivationTokenRepository;
import com.sam.BankingWebApplication1.Repositories.CustomerRepository;
import com.sam.BankingWebApplication1.Repositories.UserRepository;
import com.sam.BankingWebApplication1.Services.AccountService;
import com.sam.BankingWebApplication1.Services.AdminService;
import com.sam.BankingWebApplication1.Services.EmailService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
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

    @Autowired
    private ModelMapper mapper;

    @Deprecated
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


    @Deprecated
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

    @Override
    public KycReviewDTO getKycDetails(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer", "id", customerId));

        if (customer.getKycStatus() == KycStatus.NOT_SUBMITTED) {
            throw new IllegalStateException("KYC not submitted yet");
        }

        return mapper.map(customer,KycReviewDTO.class);
    }

    @Override
    @Transactional
    public void approveKyc(Long customerId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer", "id", customerId));

        // 🔒 Safety check: prevent double approval
        if (customer.getKycStatus() != KycStatus.SUBMITTED) {
            throw new IllegalStateException("KYC already processed");
        }

        // 1️⃣ Update KYC + customer status
        customer.setKycStatus(KycStatus.VERIFIED);
        customer.setStatus(CustomerStatus.APPROVED);
        customerRepository.save(customer);

        // 2️⃣ Generate activation token
        String token = UUID.randomUUID().toString();

        ActivationToken activationToken = new ActivationToken();
        activationToken.setToken(token);
        activationToken.setCustomer(customer);
        activationToken.setExpiryAt(LocalDateTime.now().plusHours(24));
        activationToken.setUsed(false);

        activationTokenRepository.save(activationToken);

        // 3️⃣ Build activation link
        String activationLink =
                "https://smartbankofficial.netlify.app/pages/active-account.html?token=" + token;

        // 4️⃣ Send combined approval + activation email
        String subject = "SmartBank – KYC Approved | Activate Your Account";

        String message =
                "Dear " + customer.getFullName() + ",\n\n" +
                        "We are pleased to inform you that your KYC has been successfully verified " +
                        "and your SmartBank account has been approved.\n\n" +
                        "To activate your account and set your login credentials, please click the " +
                        "secure link below:\n\n" +
                        activationLink + "\n\n" +
                        "This link will expire in 24 hours.\n\n" +
                        "For security reasons, please do not share this link with anyone.\n\n" +
                        "Regards,\n" +
                        "SmartBank Team";

        emailService.sendMail(customer.getEmail(), subject, message);
    }

    @Override
    @Transactional
    public void rejectKyc(Long customerId, String reason) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer", "id", customerId));

        // 🔒 Safety check
        if (customer.getKycStatus() != KycStatus.SUBMITTED) {
            throw new IllegalStateException("KYC already processed");
        }

        customer.setKycStatus(KycStatus.REJECTED);
        customer.setKycRejectionReason(reason); // add column if missing

        customerRepository.save(customer);

        // 📧 Rejection email
        emailService.sendMail(
                customer.getEmail(),
                "SmartBank – KYC Rejected",
                "Dear " + customer.getFullName() + ",\n\n" +
                        "Your KYC was rejected for the following reason:\n" +
                        reason + "\n\n" +
                        "Please re-apply with correct documents.\n\n" +
                        "Regards,\nSmartBank Team"
        );
    }


}
