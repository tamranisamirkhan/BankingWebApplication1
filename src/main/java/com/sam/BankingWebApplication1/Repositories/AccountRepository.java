package com.sam.BankingWebApplication1.Repositories;

import com.sam.BankingWebApplication1.Entities.Account;
import com.sam.BankingWebApplication1.Entities.User;
import jakarta.persistence.LockModeType;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long> {

    Account findByUserId(Long userId);

    boolean existsByAccountNumber(String accountNumber);

    Optional<Object> findFirstByUserId(Long id);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.accountNumber = :accountNumber")
    Optional<Account> findByAccountNumberForUpdate
            (@NotNull @Param("accountNumber") String receiversAccountNumber);
}
