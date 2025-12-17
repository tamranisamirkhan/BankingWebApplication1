package com.sam.BankingWebApplication1.Entities;

import com.sam.BankingWebApplication1.Enums.CustomerStatus;
import com.sam.BankingWebApplication1.Enums.KycStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String fullName;
    @Temporal(TemporalType.DATE)
    private Date bod;
    private String gender;
    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false, unique = true)
    private String email;
    private String address;
    private String city;
    private String state;
    private String country;
    private String pincode;

    @Column(nullable = false, unique = true)
    private String aadharNumber;
    @Column(nullable = false,unique = true)
    private String panNumber;
    @Enumerated(EnumType.STRING)
    private CustomerStatus status = CustomerStatus.KYC_PENDING;
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToOne(mappedBy = "customer",cascade = CascadeType.ALL)
    private User user;

    @OneToOne(mappedBy = "customer" , cascade = CascadeType.ALL)
    private Account account;

    private String aadhaarFrontPath;
    private String aadhaarBackPath;
    private String panPath;

    @Enumerated(EnumType.STRING)
    private KycStatus kycStatus = KycStatus.NOT_SUBMITTED;

}
