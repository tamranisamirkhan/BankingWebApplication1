package com.sam.BankingWebApplication1.Repositories;

import com.sam.BankingWebApplication1.Entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    List<Transaction>
    findTop5ByAccountIdOrderByTransactionDateDesc(Long accountId);
}
