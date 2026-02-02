package com.sam.BankingWebApplication1.Repositories;

import com.sam.BankingWebApplication1.Entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    List<Transaction>
    findTop5ByAccountIdOrderByTransactionDateDesc(Long accountId);
}
