package com.example.fleamaket.entity;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
@Entity
public class Order {
	// id
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// ユーザーid
	@ManyToOne(fetch = FetchType.EAGER)
	private User user;

	// 商品id
	@ManyToOne(fetch = FetchType.EAGER)
	private Item item;

	// 作成日時
	@Column(name = "createdAt", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private ZonedDateTime createdAt;

	// 更新日時
	@Column(name = "updatedAt", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	private ZonedDateTime updatedAt;
}
