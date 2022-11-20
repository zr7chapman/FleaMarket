package com.example.fleamaket.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.fleamaket.entity.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
	// 指定されたユーザーID以外の商品リストを取得
	List<Item> findByUserIdNotOrderByCreatedAtDesc(long userId);

	// 未使用
	@Query(value = "SELECT i.* FROM items i INNER JOIN likes l ON i.id = l.item_id WHERE l.user_id = ?1 ORDER BY l.created_at DESC", nativeQuery = true)
	List<Item> getLikedItems(long userId);
	
	@Query(value = "SELECT i.* FROM items i INNER JOIN orders o ON i.id = o.item_id WHERE o.user_id = ?1 ORDER BY o.created_at DESC", nativeQuery = true)
	List<Item> getOrderedItems(long userId);
}
