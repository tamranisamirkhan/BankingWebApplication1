package com.sam.BankingWebApplication1.Repositories;

import com.sam.BankingWebApplication1.Entities.Customer;
import com.sam.BankingWebApplication1.Enums.CustomerStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {
    List<Customer> findByStatus(CustomerStatus status);

    boolean existsByEmail(@NotEmpty(message = "Email is required") @Email(message = "Invalid email address") String email);

    boolean existsByPhoneNumber(@NotEmpty(message = "Phone number is required") @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits") String phoneNumber);

    boolean existsByAadharNumber(@NotEmpty(message = "Aadhar number is required") @Pattern(regexp = "^[0-9]{12}$", message = "Aadhar number must be 12 digits") String aadharNumber);

    boolean existsByPanNumber(@NotEmpty(message = "PAN number is required") @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$", message = "Invalid PAN format (e.g. ABCDE1234F)") String panNumber);


}
