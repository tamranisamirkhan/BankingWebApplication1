package com.sam.BankingWebApplication1.Entities;

import com.sam.BankingWebApplication1.Enums.TransactionStatus;
import com.sam.BankingWebApplication1.Enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String referenceNumber;
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    private double amount;
    private LocalDateTime transactionDate = LocalDateTime.now();
    private String receiverAccountNumber;
    private String senderAccountNumber;
    private double balanceAfterTransaction;
    private String description;
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
    @Enumerated(EnumType.STRING)
    private TransactionStatus status = TransactionStatus.PENDING;
}
