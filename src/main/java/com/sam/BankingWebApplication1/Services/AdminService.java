package com.sam.BankingWebApplication1.Services;

public interface AdminService {
    String approveCustomer(int id);


    String rejectCustomer(int id);

    String inactiveCustomer(int id);

    String activeCustomer(int id);

    String deleteCustomer(int id);
}
