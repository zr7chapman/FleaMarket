package com.example.fleamaket.service.impl;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import com.example.fleamaket.bean.Image;
import com.example.fleamaket.entity.Category;
import com.example.fleamaket.entity.Item;
import com.example.fleamaket.entity.Like;
import com.example.fleamaket.entity.Order;
import com.example.fleamaket.entity.User;
import com.example.fleamaket.form.ItemForm;
import com.example.fleamaket.repository.CategoryRepository;
import com.example.fleamaket.repository.ItemRepository;
import com.example.fleamaket.repository.LikeRepository;
import com.example.fleamaket.repository.OrderRepository;
import com.example.fleamaket.service.ItemService;
import com.example.fleamaket.service.UtilityService;

@Service
public class ItemServiceImpl implements ItemService {
	private ItemRepository itemRepository;
	private LikeRepository likeRepository;
	private CategoryRepository categoryRepository;
	private OrderRepository orderRepository;
	private UtilityService utilityService;
	
	private final String IMAGE_DIR;
	
	@Autowired
	public ItemServiceImpl(
		ItemRepository itemRepository,
		LikeRepository likeRepository,
		CategoryRepository categoryRepository,
		OrderRepository orderRepository,
		Environment environment,
		UtilityService utilityService
	) {
		this.itemRepository = itemRepository;
		this.likeRepository = likeRepository;
		this.categoryRepository = categoryRepository;
		this.orderRepository = orderRepository;
		this.utilityService = utilityService;
		this.IMAGE_DIR = environment.getProperty("images.imagedir");
	}
	
	@Override
	public List<Item> getOthersList(long id) {
		return itemRepository.findByUserIdNotOrderByCreatedAtDesc(id);
	}

	@Override
	public Optional<Item> findById(long id) {
		return itemRepository.findById(id);
	}

	@Override
	public Item register(User user, ItemForm form) {
		Image image = utilityService.getImageBean(form.getImage(), IMAGE_DIR);
        utilityService.uploadImage(image);
        
        Category category = categoryRepository.findById(form.getCategory_id()).orElseThrow();
 
        Item item = new Item(null, user, form.getName(), form.getDescription(), category, form.getPrice(), image.getSaveFileName(), null, null, null, null);
        return itemRepository.saveAndFlush(item);
	}

	@Override
	public void delete(long id) {
		Item item = itemRepository.findById(id).orElseThrow();
		itemRepository.delete(item);
	}

	@Override
	public Item update(long id, ItemForm form) {
		Item item = itemRepository.findById(id).orElseThrow();
		Category category = categoryRepository.findById(form.getCategory_id()).orElseThrow();
		
		item.setName(form.getName());
		item.setDescription(form.getDescription());
		item.setPrice(form.getPrice());
		item.setCategory(category);
		return itemRepository.saveAndFlush(item);
	}

	@Override
	public void updateImage(long id, ItemForm form) {
		Item item = itemRepository.findById(id).orElseThrow();

		File oldFile = Paths.get(System.getProperty("user.dir"), IMAGE_DIR, item.getImage()).toFile();
		if (oldFile.exists()) {
			oldFile.delete();
		}
		
		Image image = utilityService.getImageBean(form.getImage(), IMAGE_DIR);
        utilityService.uploadImage(image);
        
		item.setImage(image.getSaveFileName());
		itemRepository.saveAndFlush(item);
	}

	@Override
	public List<Category> getCategories() {
		return categoryRepository.findAll();
	}

	@Override
	public void validateCategory(BindingResult bindingResult, ItemForm form) {
		try {
			categoryRepository.findById(form.getCategory_id()).orElseThrow();
		} catch (Exception e) {
			bindingResult.rejectValue("category_id", "category.notexist");
		}
	}

	@Override
	public void validateItemId(BindingResult bindingResult, long id) {
		try {
			itemRepository.findById(id).orElseThrow();
		} catch (Exception e) {
			bindingResult.reject("id.notexist");
		}
	}

	@Override
	public void copyToForm(Item item, ItemForm form) {
		form.setName(item.getName());
		form.setDescription(item.getDescription());
		form.setPrice(item.getPrice());
		form.setCategory_id(item.getCategory().getId());
	}

	@Override
	public void purchase(User user, Item item) {
		orderRepository.saveAndFlush(new Order(null, user, item, null, null));
	}

	@Override
	public List<Item> getExhibitions(User user) {
		List<Item> list = user.getItems();
		Collections.sort(list, new Comparator<Item>() {
			@Override
			public int compare(Item o1, Item o2) {
				return o2.getCreatedAt().compareTo(o1.getCreatedAt());
			}
		});
		return list;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Item> getFavoriteItems(User user) {
		List<Like> list = likeRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
		return list.stream().map(like -> like.getItem()).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<Item> getOrderedItems(User user) {
		return itemRepository.getOrderedItems(user.getId());
	}

	@Override
	public void addLike(User user, Item item) {
		Like like = new Like(null, user, item, null, null);
		likeRepository.saveAndFlush(like);
	}

	@Override
	public void removeLike(User user, Item item) {
		Like like = likeRepository.findFirstByUserIdAndItemId(user.getId(), item.getId());
		likeRepository.delete(like);		
	}
}
