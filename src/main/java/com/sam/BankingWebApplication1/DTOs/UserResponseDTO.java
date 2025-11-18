package com.sam.BankingWebApplication1.DTOs;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class UserResponseDTO {
    private long id;
    private String username;
    private String email;
    private String phoneNumber;
    private String role;

}
