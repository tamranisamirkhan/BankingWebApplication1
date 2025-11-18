package com.sam.BankingWebApplication1.Repositories;

import com.sam.BankingWebApplication1.Entities.ActivationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActivationTokenRepository extends JpaRepository<ActivationToken,Long> {
    Optional<ActivationToken> findByToken(String token);
    void deleteByToken(String token);
}
