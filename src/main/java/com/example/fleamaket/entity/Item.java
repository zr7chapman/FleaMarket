package com.example.fleamaket.entity;

import java.time.ZonedDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "items")
@Entity
public class Item {
	// id
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// ユーザーid
	@ManyToOne(fetch = FetchType.EAGER)
	private User user;

	// 商品名
	@Column(name = "name", nullable = false)
	private String name;

	// 商品説明
	@Column(name = "description", length = 1000, nullable = false)
	private String description;

	// カテゴリーid
	@ManyToOne(fetch = FetchType.EAGER)
	private Category category;

	// 価格
	@Column(name = "price", nullable = false)
	private int price;

	// 画像
	@Column(name = "image", nullable = false)
	private String image;

	// 作成日時
	@Column(name = "createdAt", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private ZonedDateTime createdAt;

	// 更新日時
	@Column(name = "updatedAt", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	private ZonedDateTime updatedAt;
	
//	@OneToMany(mappedBy = "item", fetch = FetchType.EAGER)
	@OneToMany(mappedBy = "item")
	private List<Order> orderedList;
	
	@OneToMany(mappedBy = "item")
	private List<Like> likedList;
	
	public boolean isSoldout() {
		return orderedList.size() > 0;
	}
	
	public boolean isFavorite(long userId) {
		return likedList.stream().filter(like -> like.getUser().getId() == userId).count() > 0;
	}
}
