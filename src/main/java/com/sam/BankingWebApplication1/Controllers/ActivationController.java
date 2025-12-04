package com.sam.BankingWebApplication1.Controllers;

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
import com.sam.BankingWebApplication1.Utils.CommonResponse;
import com.sam.BankingWebApplication1.Utils.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/smartBank/user")
public class ActivationController {

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

    @PostMapping("/activate")
    public ResponseModel activate(@RequestBody ActivationRequest req) {

        ActivationToken token = tokenRepo.findByToken(req.getToken())
                .orElseThrow(() -> new TokenExpiredException("Invalid or expired token"));

        if (token.getExpiryAt().isBefore(LocalDateTime.now())) {
            tokenRepo.delete(token);
            throw new TokenExpiredException("Activation link expired!");
        }
        Customer customer = token.getCustomer();

        // Create user
        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setEmail(customer.getEmail());
        user.setPhoneNumber(customer.getPhoneNumber());
        user.setRole("CUSTOMER");
        user.setCustomer(customer);
        userRepo.save(user);

        // Create account
        Account account = new Account();
        account.setAccountNumber(accountService.generateUniqueAccountNumber());
        account.setAccountType(AccountType.SAVINGS);
        account.setCustomer(customer);
        account.setBalance(0.0);
        accountRepo.save(account);

        // Link and activate
        customer.setUser(user);
        customer.setStatus(CustomerStatus.ACTIVE);
        customerRepo.save(customer);

        tokenRepo.delete(token);

        return CommonResponse.CREATED("Account activated successfully!");
    }


}


