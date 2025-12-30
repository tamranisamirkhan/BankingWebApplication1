package com.sam.BankingWebApplication1.Repositories;

import com.sam.BankingWebApplication1.Entities.KycToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KycTokenRepository extends JpaRepository<KycToken, Long> {

    Optional<KycToken> findByTokenAndUsedFalse(String token);
}

