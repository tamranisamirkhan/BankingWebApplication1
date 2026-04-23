package com.sam.BankingWebApplication1.Controllers;

import com.sam.BankingWebApplication1.DTOs.TransferDTOs.TransferRequest;
import com.sam.BankingWebApplication1.Services.TransferService;
import com.sam.BankingWebApplication1.Utils.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public class TransferController {
    @Autowired
    private TransferService transferService;

    @PostMapping("/toTransfer")
    public ResponseModel toTransfer(@RequestBody TransferRequest transferRequest,
                                    Authentication authentication){

        String senderUsername = authentication.getName();
        return transferService.toTransfer(transferRequest,senderUsername);
    }
}
