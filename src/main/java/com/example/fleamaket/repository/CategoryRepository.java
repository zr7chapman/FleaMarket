package com.example.fleamaket.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fleamaket.entity.Category;

public interface CategoryRepository  extends JpaRepository<Category, Long>{

}
