package com.sam.BankingWebApplication1.Repositories;

import com.sam.BankingWebApplication1.Entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long> {

    Account findByUserId(Long userId);

    boolean existsByAccountNumber(String accountNumber);

    Optional<Object> findFirstByUserId(Long id);
}
