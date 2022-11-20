package com.example.fleamaket.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.fleamaket.entity.Item;
import com.example.fleamaket.entity.User;
import com.example.fleamaket.form.Create;
import com.example.fleamaket.form.Edit;
import com.example.fleamaket.form.EditImage;
import com.example.fleamaket.form.ItemForm;
import com.example.fleamaket.service.ItemService;
import com.example.fleamaket.service.UserService;
import com.example.fleamaket.service.UtilityService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
//@RequestMapping({"/", "/items"})
public class ItemController {
	private ItemService itemService;
	private UserService userService;
	private UtilityService utilityService;
	
	@Value("${items.image.maxwidth}")
	private int maxWidth;
	@Value("${items.image.maxheight}")
	private int maxHeight;
	@Value("${items.image.mimetype}")
	private List<String> mimeType;
	
	@Autowired
	public ItemController(
		ItemService itemService,
		UserService userService,
		UtilityService utilityService
	) {
		this.itemService = itemService;
		this.userService = userService;
		this.utilityService = utilityService;
	}
	
	@GetMapping({"/", "/items/index"})
	public String index(
		@AuthenticationPrincipal(expression = "user") User user,
		Model model
	) {
		log.debug("indexハンドラ");
		model.addAttribute("items", itemService.getOthersList(user.getId()));
		model.addAttribute("main", "items/index::main");
		return "layouts/logged_in";
	}
	
	@GetMapping("/items/exhibitions/{id}")
	public String exhibitions(
        @PathVariable("id") Long id,
		Model model
	) {
		User user = userService.findById(id).orElseThrow();
		List<Item> list = itemService.getExhibitions(user);
		model.addAttribute("user", user);
		model.addAttribute("items", list);
		model.addAttribute("main", "items/exhibitions::main");
		return "layouts/logged_in";
	}
	
	@GetMapping({"/items/favorites"})
	public String favorites(
		@AuthenticationPrincipal(expression = "user") User user,
		Model model
	) {
		List<Item> list = itemService.getFavoriteItems(user);
		model.addAttribute("items", list);
		model.addAttribute("main", "items/favorites::main");
		return "layouts/logged_in";
	}
	
	@GetMapping("/items/detail/{id}")    
    public String detail(
        @PathVariable("id") Long id,
        @ModelAttribute ItemForm itemForm,
        Model model
	) {
		Item item = itemService.findById(id).orElseThrow();
        model.addAttribute("item", item);
        model.addAttribute("main", "items/detail::main");
        return "layouts/logged_in";    
    }
	
	@GetMapping("/items/confirm/{id}")    
    public String confirm(
        @PathVariable("id") Long id,
        @ModelAttribute ItemForm itemForm,
        Model model
	) {
		Item item = itemService.findById(id).orElseThrow();
        model.addAttribute("item", item);
        model.addAttribute("main", "items/confirm::main");
        return "layouts/logged_in";    
    }
	
	@PostMapping("/items/purchase/{id}")
    public String purchase(
        @PathVariable("id") Long id,
        @ModelAttribute ItemForm itemForm,
        @AuthenticationPrincipal(expression = "user") User user,
        Model model
	) {
		Item item = itemService.findById(id).orElseThrow();
		itemService.purchase(user, item);
		
        model.addAttribute("item", item);
        model.addAttribute("main", "items/finish::main");
        return "layouts/logged_in";    
    }
	
	@GetMapping("/items/create")
	public String create(
		@ModelAttribute ItemForm itemForm,
		Model model
	) {
		model.addAttribute("categories", itemService.getCategories());
		model.addAttribute("main", "items/create::main");
		return "layouts/logged_in";
	}
	
	@PostMapping("/items/create")
	public String store(
		@Validated(Create.class) ItemForm form,
		BindingResult bindingResult,	// 必ずバリデーション用formの下に定義する
		RedirectAttributes redirectAttributes,
		@AuthenticationPrincipal(expression = "user") User user,
		Model model
	) {
		itemService.validateCategory(bindingResult, form);
		utilityService.validateImageFile(bindingResult, form, mimeType, maxWidth, maxHeight);
		if (bindingResult.hasErrors()) {
			return create(form, model);
		}
		
		Item item = itemService.register(user, form);
		redirectAttributes.addFlashAttribute("successMessage", utilityService.getMessage("items.create.success"));
		return "redirect:/items/detail/" + item.getId();
	}
	
	@GetMapping("/items/edit/{id}")
	public String edit(
        @PathVariable("id") Long id,
		@ModelAttribute ItemForm itemForm,
		Model model
	) {
		Item item = itemService.findById(id).orElseThrow();
		itemService.copyToForm(item, itemForm);
		
		model.addAttribute("item", item);
		model.addAttribute("categories", itemService.getCategories());
		model.addAttribute("main", "items/edit::main");
		return "layouts/logged_in";
	}
	
	@PostMapping("/items/edit/{id}")
	public String update(
	    @PathVariable("id") Long id,
		@Validated(Edit.class) ItemForm form,
		BindingResult bindingResult,	// 必ずバリデーション用formの下に定義する
		RedirectAttributes redirectAttributes,
		@AuthenticationPrincipal(expression = "user") User user,
		Model model
	) {
		itemService.validateItemId(bindingResult, id);
		itemService.validateCategory(bindingResult, form);
		if (bindingResult.hasErrors()) {
			return edit(id, form, model);
		}
		
		itemService.update(id, form);
		redirectAttributes.addFlashAttribute("successMessage", utilityService.getMessage("items.edit.success"));
		return "redirect:/items/detail/" + id;
	}
	
	@GetMapping("/items/editimage/{id}")
	public String editImage(
        @PathVariable("id") Long id,
		@ModelAttribute ItemForm itemForm,
		Model model
	) {
		Item item = itemService.findById(id).orElseThrow();
		
		model.addAttribute("item", item);
		model.addAttribute("main", "items/edit_image::main");
		return "layouts/logged_in";
	}
	
	@PostMapping("/items/editimage/{id}")
	public String updateImage(
	    @PathVariable("id") Long id,
		@Validated(EditImage.class) ItemForm form,
		BindingResult bindingResult,	// 必ずバリデーション用formの下に定義する
		RedirectAttributes redirectAttributes,
		@AuthenticationPrincipal(expression = "user") User user,
		Model model
	) {
		itemService.validateItemId(bindingResult, id);
		utilityService.validateImageFile(bindingResult, form, mimeType, maxWidth, maxHeight);
		if (bindingResult.hasErrors()) {
			return editImage(id, form, model);
		}
		
		itemService.updateImage(id, form);
		redirectAttributes.addFlashAttribute("successMessage", utilityService.getMessage("items.editimage.success"));
		return "redirect:/items/detail/" + id;
	}

	@PostMapping("/items/delete/{id}")    
    public String delete(
        @PathVariable("id") Integer id,
        RedirectAttributes redirectAttributes,
        @AuthenticationPrincipal(expression = "user") User user,
        Model model
	) {
        itemService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", utilityService.getMessage("items.delete.success"));
        return "redirect:/items/exhibitions/" + user.getId();  
    }
	
	@PostMapping("/items/toggleLike/{id}")
	public String toggleLike(
	        @PathVariable("id") Integer id,
	        RedirectAttributes redirectAttributes,
	        @AuthenticationPrincipal(expression = "user") User user,
	        Model model
	) {
		Item item = itemService.findById(id).orElseThrow();
		
		if (item.isFavorite(user.getId())) {
			itemService.removeLike(user, item);
	        redirectAttributes.addFlashAttribute("successMessage", utilityService.getMessage("items.favorites.remove"));
		} else {
			itemService.addLike(user, item);
	        redirectAttributes.addFlashAttribute("successMessage", utilityService.getMessage("items.favorites.add"));
		}
        return "redirect:/items/index";
	}
}
