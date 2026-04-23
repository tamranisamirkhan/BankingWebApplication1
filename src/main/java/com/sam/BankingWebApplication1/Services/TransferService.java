package com.sam.BankingWebApplication1.Services;

import com.sam.BankingWebApplication1.DTOs.TransferDTOs.TransferRequest;
import com.sam.BankingWebApplication1.Utils.ResponseModel;

public interface TransferService {
    ResponseModel toTransfer(TransferRequest transferRequest, String senderUsername);
}
