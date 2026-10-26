package com.zjgsu.jzy.service;

import com.zjgsu.jzy.exception.NotFoundException;
import com.zjgsu.jzy.model.Customer;
import com.zjgsu.jzy.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * 顾客业务：用户服务边界内的数据创建与查询
 */
@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /** 注册顾客 */
    @Transactional
    public Customer register(String name, String phone) {
        Customer customer = new Customer(UUID.randomUUID().toString(), name, phone, Instant.now());
        return customerRepository.save(customer);
    }

    /** 按 ID 查询顾客，不存在抛 404（订单服务跨服务校验时调用） */
    @Transactional(readOnly = true)
    public Customer findById(String id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("顾客不存在: " + id));
    }

    /** 查询全部顾客 */
    @Transactional(readOnly = true)
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }
}
