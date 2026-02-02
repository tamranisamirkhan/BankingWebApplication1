package com.sam.BankingWebApplication1.Services;

import com.sam.BankingWebApplication1.Utils.ResponseModel;
import org.springframework.security.core.Authentication;

public interface CustomerDashboardService {
    ResponseModel getDashboard(Authentication authentication);
}
