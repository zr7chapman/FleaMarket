package com.example.fleamaket.service;

import java.util.List;
import java.util.Optional;

import org.springframework.validation.BindingResult;

import com.example.fleamaket.entity.Category;
import com.example.fleamaket.entity.Item;
import com.example.fleamaket.entity.User;
import com.example.fleamaket.form.ItemForm;

public interface ItemService {
	// 指定されたユーザーID以外の商品リストを取得
	List<Item> getOthersList(long id);
	
	Optional<Item> findById(long id);
	
	Item register(User user, ItemForm form);
	
	void delete(long id);
	
	Item update(long id, ItemForm form);
	
	void updateImage(long id, ItemForm form);
	
	void purchase(User user, Item item);
	
	List<Category> getCategories();
	
	void validateCategory(BindingResult bindingResult, ItemForm form);
	
	void validateItemId(BindingResult bindingResult, long id);
	
	void copyToForm(Item item, ItemForm form);
	
	List<Item> getExhibitions(User user);
	
	List<Item> getFavoriteItems(User user);

	List<Item> getOrderedItems(User user);
	
	void addLike(User user, Item item);
	
	void removeLike(User user, Item item);
}
