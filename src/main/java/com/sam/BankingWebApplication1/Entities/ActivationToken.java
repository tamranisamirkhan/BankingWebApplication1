package com.sam.BankingWebApplication1.Entities;

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
public class ActivationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // unique token value (random UUID)
    @Column(nullable = false, unique = true)
    private String token;

    // link to the customer
    @OneToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    // when this token expires
    private LocalDateTime expiryAt;



}

