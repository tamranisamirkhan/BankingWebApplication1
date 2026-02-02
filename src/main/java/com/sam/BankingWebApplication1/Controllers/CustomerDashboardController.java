package com.sam.BankingWebApplication1.Controllers;

import com.sam.BankingWebApplication1.Services.CustomerDashboardService;
import com.sam.BankingWebApplication1.Utils.ResponseModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/smartBank/customer")
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerDashboardController {

    private final CustomerDashboardService dashboardService;

    public CustomerDashboardController(CustomerDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }
    @GetMapping("/dashboard")
    public ResponseModel getDashboard(Authentication authentication) {
        return dashboardService.getDashboard(authentication);
    }
}
