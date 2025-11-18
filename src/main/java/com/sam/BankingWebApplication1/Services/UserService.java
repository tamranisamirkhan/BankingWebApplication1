package com.sam.BankingWebApplication1.Services;

import com.sam.BankingWebApplication1.DTOs.LoginRequestDTO;
import com.sam.BankingWebApplication1.DTOs.RegisterCustomerDTO;
import com.sam.BankingWebApplication1.DTOs.UserResponseDTO;
import com.sam.BankingWebApplication1.Entities.User;

import java.util.List;

public interface UserService {

    String validateUser(LoginRequestDTO request);
}
