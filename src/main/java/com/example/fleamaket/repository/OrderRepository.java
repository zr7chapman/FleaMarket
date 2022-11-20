package com.example.fleamaket.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fleamaket.entity.Order;

public interface OrderRepository  extends JpaRepository<Order, Long>{

}
