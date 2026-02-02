package com.sam.BankingWebApplication1.Controllers;

import com.sam.BankingWebApplication1.DTOs.ActivationRequest;
import com.sam.BankingWebApplication1.Services.ActivationService;
import com.sam.BankingWebApplication1.Utils.CommonResponse;
import com.sam.BankingWebApplication1.Utils.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/smartBank/user")
public class ActivationController {
    @Autowired
    private ActivationService activationService;

    @PostMapping("/activate")
    public ResponseModel activate(@RequestBody ActivationRequest req) {
        activationService.activateAccount(req);
        return CommonResponse.CREATED("Account activated successfully!");
    }

}


