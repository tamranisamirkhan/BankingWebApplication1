package com.sam.BankingWebApplication1.Services.ServicesImpl;

import com.sam.BankingWebApplication1.DTOs.ActivationRequest;
import com.sam.BankingWebApplication1.Entities.Account;
import com.sam.BankingWebApplication1.Entities.ActivationToken;
import com.sam.BankingWebApplication1.Entities.Customer;
import com.sam.BankingWebApplication1.Entities.User;
import com.sam.BankingWebApplication1.Enums.AccountType;
import com.sam.BankingWebApplication1.Enums.CustomerStatus;
import com.sam.BankingWebApplication1.Exceptions.TokenExpiredException;
import com.sam.BankingWebApplication1.Repositories.AccountRepository;
import com.sam.BankingWebApplication1.Repositories.ActivationTokenRepository;
import com.sam.BankingWebApplication1.Repositories.CustomerRepository;
import com.sam.BankingWebApplication1.Repositories.UserRepository;
import com.sam.BankingWebApplication1.Services.AccountService;
import com.sam.BankingWebApplication1.Services.ActivationService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ActivationServiceImpl implements ActivationService {

    @Autowired
    private ActivationTokenRepository tokenRepo;

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private AccountService accountService;

    @Transactional
    public void activateAccount(ActivationRequest req) {

        ActivationToken token = tokenRepo.findByToken(req.getToken())
                .orElseThrow(() ->
                        new IllegalStateException("Invalid or expired token"));

        // 1️⃣ Validate token
        if (token.isUsed()) {
            throw new IllegalStateException("Activation link already used");
        }

        if (token.getExpiryAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Activation link expired");
        }

        // 2️⃣ Validate passwords
        if (!req.getPassword().equals(req.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        if (!req.getTransactionPin().equals(req.getConfirmTransactionPin())) {
            throw new IllegalArgumentException("Transaction PINs do not match");
        }

        Customer customer = token.getCustomer();

        // 3️⃣ Create USER
        User user = new User();
        user.setUsername(customer.getEmail()); // auto username
        user.setPassword(encoder.encode(req.getPassword()));
        user.setTransactionPin(encoder.encode(req.getTransactionPin()));
        user.setRole("CUSTOMER");
        user.setCustomer(customer);
        user.setActive(true);

        userRepo.save(user);

        // 4️⃣ Create ACCOUNT
        Account account = new Account();
        account.setAccountNumber(accountService.generateUniqueAccountNumber());
        account.setAccountType(AccountType.SAVINGS);
        account.setBalance(0.0);
        account.setCustomer(customer);
        account.setUser(user);

        accountRepo.save(account);

        // 5️⃣ Activate customer
        customer.setUser(user);
        customer.setStatus(CustomerStatus.ACTIVE);
        customerRepo.save(customer);

        // 6️⃣ Mark token as used
        token.setUsed(true);
        tokenRepo.save(token);
    }
    }





