package com.zjgsu.jzy.repository;

import com.zjgsu.jzy.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, String> {
}
