package com.example.bot_builder.service;

import com.example.bot_builder.domain.CustomerRequest;
import com.example.bot_builder.repository.CustomerRequestRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerRequestService {
    private final CustomerRequestRepo customerRequestRepo;

    @Autowired
    public CustomerRequestService(CustomerRequestRepo customerRequestRepo) {
        this.customerRequestRepo = customerRequestRepo;
    }

    public CustomerRequest save(CustomerRequest customerRequest) {
        return customerRequestRepo.save(customerRequest);
    }
}
