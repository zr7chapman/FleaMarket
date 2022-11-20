package com.example.fleamaket.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fleamaket.entity.Like;

public interface LikeRepository  extends JpaRepository<Like, Long>{
	List<Like> findByUserIdOrderByCreatedAtDesc(long userId);
	
	Like findFirstByUserIdAndItemId(long userId, long itemId);
}
