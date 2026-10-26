package com.zjgsu.jzy.repository;

import com.zjgsu.jzy.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {
}
