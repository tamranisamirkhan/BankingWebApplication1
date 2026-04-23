package com.sam.BankingWebApplication1.Services.ServicesImpl;

import com.sam.BankingWebApplication1.DTOs.TransferDTOs.TransferRequest;
import com.sam.BankingWebApplication1.Entities.Account;
import com.sam.BankingWebApplication1.Entities.Transaction;
import com.sam.BankingWebApplication1.Entities.User;
import com.sam.BankingWebApplication1.Enums.TransactionStatus;
import com.sam.BankingWebApplication1.Enums.TransactionType;
import com.sam.BankingWebApplication1.Repositories.AccountRepository;
import com.sam.BankingWebApplication1.Repositories.TransactionRepository;
import com.sam.BankingWebApplication1.Repositories.UserRepository;
import com.sam.BankingWebApplication1.Services.TransferService;
import com.sam.BankingWebApplication1.Utils.CommonResponse;
import com.sam.BankingWebApplication1.Utils.ResponseModel;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

@Service
public class TransferServiceImpl implements TransferService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public TransferServiceImpl(UserRepository userRepository,
                               AccountRepository accountRepository,
                               TransactionRepository transactionRepository,
                               BCryptPasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public ResponseModel toTransfer(TransferRequest transferRequest, String senderUsername) {

        String referenceNumber = generateReferenceNumber();

        Account senderAccount = null;

        Transaction senderTransaction = new Transaction();
        senderTransaction.setReferenceNumber(referenceNumber);
        senderTransaction.setAmount(transferRequest.getAmount());
        senderTransaction.setTransactionDate(LocalDateTime.now());
        senderTransaction.setType(TransactionType.DEBIT);
        senderTransaction.setDescription("Transfer Attempt");

        try {

            // 1️⃣ Find sender
            User sender = userRepository.findByUsername(senderUsername)
                    .orElseThrow(() ->
                            new RuntimeException("User not found"));

            // 2️⃣ Validate transaction pin
            if (!passwordEncoder.matches(
                    transferRequest.getPin(),
                    sender.getTransactionPin())) {

                throw new RuntimeException("Invalid transaction PIN");
            }

            // 3️⃣ Sender account
            senderAccount = sender.getAccount();

            if (senderAccount == null)
                throw new RuntimeException("Sender account not found");

            senderTransaction.setAccount(senderAccount);

            // 4️⃣ Validate amount
            if (transferRequest.getAmount() <= 0)
                throw new RuntimeException("Invalid transfer amount");

            // 5️⃣ Find receiver
            Account receiverAccount = accountRepository
                    .findByAccountNumberForUpdate(transferRequest.getReceiversAccountNumber())
                    .orElseThrow(() ->
                            new RuntimeException("Receiver account not found"));

            // 6️⃣ Prevent self-transfer
            if (senderAccount.getAccountNumber()
                    .equals(receiverAccount.getAccountNumber())) {

                throw new RuntimeException("Cannot transfer to same account");
            }

            /*
             Deadlock prevention:
             Always lock accounts in same order
            */

            Account firstLock;
            Account secondLock;

            if (senderAccount.getId() < receiverAccount.getId()) {
                firstLock = accountRepository
                        .findByAccountNumberForUpdate(senderAccount.getAccountNumber())
                        .orElseThrow();
                secondLock = receiverAccount;
            } else {
                firstLock = receiverAccount;
                secondLock = accountRepository
                        .findByAccountNumberForUpdate(senderAccount.getAccountNumber())
                        .orElseThrow();
            }

            Account lockedSender =
                    senderAccount.getId() == firstLock.getId() ? firstLock : secondLock;

            Account lockedReceiver =
                    receiverAccount.getId() == firstLock.getId() ? firstLock : secondLock;

            // 7️⃣ Balance check
            if (lockedSender.getBalance() < transferRequest.getAmount())
                throw new RuntimeException("Insufficient balance");

            // 8️⃣ Update balances
            lockedSender.setBalance(
                    lockedSender.getBalance() - transferRequest.getAmount());

            lockedReceiver.setBalance(
                    lockedReceiver.getBalance() + transferRequest.getAmount());

            accountRepository.save(lockedSender);
            accountRepository.save(lockedReceiver);

            // 9️⃣ Sender transaction
            senderTransaction.setReceiverAccountNumber(
                    lockedReceiver.getAccountNumber());

            senderTransaction.setStatus(TransactionStatus.SUCCESS);

            senderTransaction.setBalanceAfterTransaction(
                    lockedSender.getBalance());

            senderTransaction.setDescription(
                    "Transfer to " + lockedReceiver.getAccountNumber());

            transactionRepository.save(senderTransaction);

            // 🔟 Receiver transaction
            Transaction receiverTransaction = new Transaction();

            receiverTransaction.setReferenceNumber(referenceNumber);
            receiverTransaction.setTransactionDate(LocalDateTime.now());
            receiverTransaction.setAmount(transferRequest.getAmount());
            receiverTransaction.setType(TransactionType.CREDIT);

            receiverTransaction.setAccount(lockedReceiver);

            receiverTransaction.setReceiverAccountNumber(
                    lockedSender.getAccountNumber());

            receiverTransaction.setBalanceAfterTransaction(
                    lockedReceiver.getBalance());

            receiverTransaction.setDescription(
                    "Received from " + lockedSender.getAccountNumber());

            receiverTransaction.setStatus(TransactionStatus.SUCCESS);

            transactionRepository.save(receiverTransaction);

            return CommonResponse.OK("Transfer successful");

        } catch (Exception exception) {

            // Only sender transaction saved on failure

            if (senderAccount != null) {

                senderTransaction.setStatus(TransactionStatus.FAILED);
                senderTransaction.setDescription(exception.getMessage());
                senderTransaction.setBalanceAfterTransaction(
                        senderAccount.getBalance());

                transactionRepository.save(senderTransaction);
            }

            throw exception;
        }
    }

    private String generateReferenceNumber() {

        return "TXN-" + System.currentTimeMillis();
    }
}