package com.sam.BankingWebApplication1.Services.ServicesImpl;

import com.sam.BankingWebApplication1.DTOs.CreateCustomerDTO;
import com.sam.BankingWebApplication1.DTOs.RegisterCustomerDTO;
import com.sam.BankingWebApplication1.Entities.Customer;
import com.sam.BankingWebApplication1.Entities.KycToken;
import com.sam.BankingWebApplication1.Enums.CustomerStatus;
import com.sam.BankingWebApplication1.Enums.KycStatus;
import com.sam.BankingWebApplication1.Exceptions.DuplicateResourceFoundException;
import com.sam.BankingWebApplication1.Exceptions.TokenExpiredException;
import com.sam.BankingWebApplication1.Repositories.CustomerRepository;
import com.sam.BankingWebApplication1.Repositories.KycTokenRepository;
import com.sam.BankingWebApplication1.Services.CustomerService;
import com.sam.BankingWebApplication1.Services.EmailService;
import com.sam.BankingWebApplication1.Utils.CommonResponse;
import com.sam.BankingWebApplication1.Utils.ResponseModel;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private KycTokenRepository kycTokenRepository;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JWTService jwtService;

    @Override
    @Transactional
    public ResponseModel createNewCustomer(CreateCustomerDTO customerDTO) {

        System.out.println("=== CONTROLLER HIT ===");


        // 1. Duplicate validations
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

        // 2. Create and save customer
        Customer customer = mapper.map(customerDTO, Customer.class);
        customer.setStatus(CustomerStatus.PENDING);
        customer.setKycStatus(KycStatus.NOT_SUBMITTED);

        Customer savedCustomer = customerRepo.save(customer);

        // 3. Generate secure KYC token (24 hours)
        String token = UUID.randomUUID().toString();

        KycToken kycToken = new KycToken();
        kycToken.setToken(token);
        kycToken.setCustomer(savedCustomer);
        kycToken.setExpiresAt(LocalDateTime.now().plusHours(24));
        kycToken.setUsed(false);

        kycTokenRepository.save(kycToken);

        // 4. Send KYC completion email
        String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);

        String kycLink =
                "https://smartbankofficial.netlify.app/pages/kyc-upload.html?token=" + encodedToken;


        String subject = "Complete Your SmartBank KYC (Valid for 24 Hours)";
        String message =
                "Dear " + savedCustomer.getFullName() + ",\n\n"
                        + "Thank you for registering with SmartBank.\n"
                        + "Your application has been submitted successfully.\n\n"
                        + "Please complete your KYC within 24 hours using the link below:\n"
                        + kycLink + "\n\n"
                        + "If you did not initiate this request, please ignore this email.\n\n"
                        + "Regards,\n"
                        + "SmartBank Team";

        emailService.sendMail(savedCustomer.getEmail(), subject, message);

        // 5. Response to frontend (NO token exposed)
        return CommonResponse.CREATED(Map.of(
                "message",
                "Application submitted successfully. "
                        + "A KYC link has been sent to your registered email address. "
                        + "Please complete KYC within 24 hours."

        ));
    }

    @Override
    public List<CreateCustomerDTO> getKycQueue(KycStatus status) {
        List<Customer> customers =
                customerRepo.findByKycStatus(status);

        return customers.stream()
                .map(customer -> mapper.map(customer,CreateCustomerDTO.class))
                .toList();
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
    @Transactional
    public ResponseModel uploadKyc(
            String token,
            MultipartFile aadhaarFront,
            MultipartFile aadhaarBack,
            MultipartFile panCard
    ) throws IOException {

        KycToken kycToken = kycTokenRepository
                .findByTokenAndUsedFalse(token)
                .orElseThrow(() ->
                        new TokenExpiredException("Invalid or expired KYC link")
                );

        if (kycToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("KYC link expired");
        }

        Customer customer = kycToken.getCustomer();

        if (customer.getKycStatus() != KycStatus.NOT_SUBMITTED) {
            throw new RuntimeException("KYC already submitted");
        }

        // 📁 File storage
        String basePath = "/home/ubuntu/BankingWebApplication1/uploads/kyc/" + customer.getId();
        File directory = new File(basePath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String frontPath = basePath + "/aadhaar_front.jpg";
        String backPath = basePath + "/aadhaar_back.jpg";
        String panPath = basePath + "/pan.jpg";

        aadhaarFront.transferTo(new File(frontPath));
        aadhaarBack.transferTo(new File(backPath));
        panCard.transferTo(new File(panPath));

        customer.setAadhaarFrontPath(frontPath);
        customer.setAadhaarBackPath(backPath);
        customer.setPanPath(panPath);
        customer.setKycStatus(KycStatus.SUBMITTED);

        customerRepo.save(customer);

        kycToken.setUsed(true);
        kycTokenRepository.save(kycToken);

        emailService.sendMail(
                customer.getEmail(),
                "SmartBank – KYC Documents Received",
                "Dear " + customer.getFullName() + ",\n\n"
                        + "We have successfully received your KYC documents.\n\n"
                        + "Our team will review your application shortly.\n"
                        + "You will be notified once the review is completed.\n\n"
                        + "Thank you for choosing SmartBank.\n\n"
                        + "Regards,\n"
                        + "SmartBank Team"
        );


        return CommonResponse.OK(
                "KYC documents uploaded successfully. Pending verification."
        );
    }


    @Override
    public String registerCustomer(RegisterCustomerDTO customerDTO) {
        return "";
    }


}
