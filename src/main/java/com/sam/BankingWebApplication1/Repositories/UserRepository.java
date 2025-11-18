package com.sam.BankingWebApplication1.Repositories;

import com.sam.BankingWebApplication1.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);
    Optional<User> findByUsername(String userName);
    Optional<User>  findByEmail(String email);


}
