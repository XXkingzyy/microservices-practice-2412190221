package com.zjgsu.jzy.repository;

import com.zjgsu.jzy.model.Dish;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DishRepository extends JpaRepository<Dish, String> {
}
