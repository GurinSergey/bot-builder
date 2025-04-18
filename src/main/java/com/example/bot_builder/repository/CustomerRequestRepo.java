package com.example.bot_builder.repository;

import com.example.bot_builder.domain.CustomerRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRequestRepo extends JpaRepository<CustomerRequest, Long> {
}
