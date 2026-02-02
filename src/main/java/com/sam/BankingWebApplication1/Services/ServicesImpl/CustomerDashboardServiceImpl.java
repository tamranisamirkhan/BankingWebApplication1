package com.sam.BankingWebApplication1.Services.ServicesImpl;

import com.sam.BankingWebApplication1.DTOs.CustomerDashboardDTOs.AccountDTO;
import com.sam.BankingWebApplication1.DTOs.CustomerDashboardDTOs.DashboardResponseDTO;
import com.sam.BankingWebApplication1.DTOs.CustomerDashboardDTOs.RecentTransactionDTO;
import com.sam.BankingWebApplication1.DTOs.CustomerDashboardDTOs.UserDTO;
import com.sam.BankingWebApplication1.Entities.Account;
import com.sam.BankingWebApplication1.Entities.Customer;
import com.sam.BankingWebApplication1.Entities.User;
import com.sam.BankingWebApplication1.Repositories.AccountRepository;
import com.sam.BankingWebApplication1.Repositories.CustomerRepository;
import com.sam.BankingWebApplication1.Repositories.TransactionRepository;
import com.sam.BankingWebApplication1.Security.UserPrincipal;
import com.sam.BankingWebApplication1.Services.CustomerDashboardService;
import com.sam.BankingWebApplication1.Utils.CommonResponse;
import com.sam.BankingWebApplication1.Utils.ResponseModel;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CustomerDashboardServiceImpl implements CustomerDashboardService {

    private final CustomerRepository userRepo;
    private final AccountRepository accountRepo;
    private final TransactionRepository transactionRepo;
    @Override
    public ResponseModel getDashboard(Authentication authentication) {
        // 1. Get logged in user (your security setup)
        UserPrincipal principal =
                (UserPrincipal) authentication.getPrincipal();

        User user = principal.getUser();

        // 2. Load customer (safe & clean)
        Customer customer = user.getCustomer();

        if (customer == null) {
            return CommonResponse.NOT_FOUND("Customer profile not found.");
        }

        // 3. Get primary account (based on your model)
        // You have user -> accounts
        Account account = (Account) accountRepo.findFirstByUserId(user.getId())
                .orElse(null);

        if (account == null) {
            return CommonResponse.NOT_FOUND("Account not found.");
        }

        // 4. Mask account number
        String accNo = account.getAccountNumber();
        String maskedAccount =
                "XXXX-XXXX-" + accNo.substring(accNo.length() - 4);

        // 5. Fetch last 5 transactions
        List<RecentTransactionDTO> recentTransactions =
                transactionRepo
                        .findTop5ByAccountIdOrderByTransactionDateDesc(account.getId())
                        .stream()
                        .map(tx -> new RecentTransactionDTO(
                                tx.getTransactionDate().toLocalDate(),
                                tx.getDescription(),
                                tx.getType(),
                                tx.getAmount(),
                                tx.getStatus().name()
                        ))
                        .collect(Collectors.toList());


        // 6. Build dashboard DTO
        UserDTO userDTO = new UserDTO();
        userDTO.setFullName(customer.getFullName());

        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountNumberMasked(maskedAccount);
        accountDTO.setType(account.getAccountType().name());
        accountDTO.setStatus(account.getStatus().name());
        accountDTO.setBalance(account.getBalance());
        accountDTO.setCurrency("INR");

        DashboardResponseDTO dashboard = new DashboardResponseDTO();
        dashboard.setUser(userDTO);
        dashboard.setAccount(accountDTO);
        dashboard.setRecentTransactions(recentTransactions);


        // 7. Wrap in your standard response
        return CommonResponse.OK(dashboard);
    }
}

