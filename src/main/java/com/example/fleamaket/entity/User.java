package com.example.fleamaket.entity;

import java.time.ZonedDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Entity
public class User {
	// id
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// ユーザー名
	@Column(name = "name", nullable = false)
	private String name;

	// メールアドレス
	@Column(name = "email", nullable = false, unique = true)
	private String email;

	// パスワード
	@Column(name = "password", nullable = false)
	private String password;

	// ロール
	@Column(name = "roles", nullable = true)
	private String roles;

	// 有効フラグ
	@Column(name = "enable", nullable = false)
	private Boolean enable;

	// プロフィール
	@Column(name = "profile", nullable = true)
	private String profile;

	// プロフィール画像
	@Column(name = "image", nullable = true)
	private String image;

	// 作成日時
	@Column(name = "createdAt", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private ZonedDateTime createdAt;

	// 更新日時
	@Column(name = "updatedAt", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	private ZonedDateTime updatedAt;

	@OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
	private List<Item> items;

//	@ManyToMany(fetch = FetchType.EAGER)
//	@ManyToMany
//	@JoinTable(name = "orders",
//		joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
//		inverseJoinColumns = @JoinColumn(name = "item_id", referencedColumnName = "id"))
//	private Set<Item> orderedItems;
}
