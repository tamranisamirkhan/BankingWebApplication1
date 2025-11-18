package com.sam.BankingWebApplication1.Services.ServicesImpl;

import com.sam.BankingWebApplication1.DTOs.AccountResponseDTO;
import com.sam.BankingWebApplication1.Entities.Account;
import com.sam.BankingWebApplication1.Repositories.AccountRepository;
import com.sam.BankingWebApplication1.Repositories.UserRepository;
import com.sam.BankingWebApplication1.Services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;


import java.util.Random;

@Service
public class AccountServiceImp implements AccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;


    public String generateUniqueAccountNumber() {
        String prefix = "BNK";
        String year = String.valueOf(LocalDate.now().getYear());
        String randomDigits;
        String accountNumber;

        do {
            randomDigits = String.format("%08d", new Random().nextInt(100_000_000));
            accountNumber = prefix + year + randomDigits;
        } while (accountRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }

    public AccountResponseDTO getAccountsByUser(Long userId) {
        Account account =  accountRepository.findByUserId(userId);
        return new AccountResponseDTO(account.getAccountNumber(),account.getAccountType(),
                account.getStatus().toString(),account.getBalance());
    }
}
