package com.sam.BankingWebApplication1.Entities;

import java.time.LocalDateTime;

public class Transaction {
    private long id;
    private String type;
    private double amount;
    private LocalDateTime transactionDate = LocalDateTime.now();
    private String receiverAccountNumber;
    private double balanceAfterTransaction;
    private String description;
}
