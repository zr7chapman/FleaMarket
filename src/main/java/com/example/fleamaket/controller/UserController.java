package com.example.fleamaket.controller;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.fleamaket.entity.Item;
import com.example.fleamaket.entity.User;
import com.example.fleamaket.form.Create;
import com.example.fleamaket.form.Edit;
import com.example.fleamaket.form.EditImage;
import com.example.fleamaket.form.UserForm;
import com.example.fleamaket.service.ItemService;
import com.example.fleamaket.service.UserService;
import com.example.fleamaket.service.UtilityService;

import lombok.extern.slf4j.Slf4j;

@RequestMapping("/users")
@Slf4j
@Controller
public class UserController {
	private UserService userService;
	private ItemService itemService;
	private UtilityService utilityService;
	
	@Value("${users.image.maxwidth}")
	private int maxWidth;
	@Value("${users.image.maxheight}")
	private int maxHeight;
	@Value("${users.image.mimetype}")
	private List<String> mimeType;
	
	@Autowired
	public UserController(
		UserService userService,
		ItemService itemService,
		UtilityService utilityService
	) {
		this.userService = userService;
		this.itemService = itemService;
		this.utilityService = utilityService;
	}
	
	@GetMapping("/signup")
	public String signup(
		@ModelAttribute UserForm userForm,
		Model model
	) {
		model.addAttribute("main", "users/signup::main");
		return "layouts/not_logged_in";
	}
	
	@PostMapping("/signup")
    public String signupProcess(
        @Validated(Create.class) UserForm form,
        BindingResult bindingResult,	// 必ずバリデーション用formの下に定義する
        RedirectAttributes redirectAttributes,
        Model model,
        HttpServletRequest request
	){
		log.debug(form.toString());
		
		userService.validatePassword(bindingResult, form);
		userService.validateEmail(bindingResult, form);
    	if (bindingResult.hasErrors()) {
			return signup(form, model);
		}
    	
        String[] roles = {"ROLE_USER", "ROLE_ADMIN"};
        try { 
	        userService.register(
	            form.getName(),
	            form.getEmail(),
	            form.getPassword(),
	            roles);
        } catch(Exception e) {
        	bindingResult.reject("signup.failedToRegister");
        	return signup(form, model);
        }
        
        try {
        	request.login(form.getEmail(), form.getPassword());
        } catch (ServletException e) {
        	e.printStackTrace();
        }
        return "redirect:/";        
    }
	
	@GetMapping("/login")
	public String login(
		@ModelAttribute UserForm userForm,
		Model model
	) {
		model.addAttribute("main", "users/login::main");
		return "layouts/not_logged_in";
	}
	
	@GetMapping("/detail/{id}")
	public String detail(
        @PathVariable("id") Long id,
        @ModelAttribute UserForm userForm,
        Model model
	) {
		User user = userService.findById(id).orElseThrow();
		List<Item> list = itemService.getOrderedItems(user);
        model.addAttribute("user", user);
        model.addAttribute("orderedItems", list);
        model.addAttribute("main", "users/detail::main");
        return "layouts/logged_in";    
    }
	
	@GetMapping("/edit/{id}")
	public String edit(
        @PathVariable("id") Long id,
		@ModelAttribute UserForm userForm,
		Model model
	) {
		User user = userService.findById(id).orElseThrow();
		userService.copyToForm(user, userForm);
		
		model.addAttribute("user", user);
		model.addAttribute("main", "users/edit::main");
		return "layouts/logged_in";
	}
	
	@PostMapping("/edit/{id}")
	public String update(
	    @PathVariable("id") Long id,
		@Validated(Edit.class) UserForm form,
		BindingResult bindingResult,	// 必ずバリデーション用formの下に定義する
		RedirectAttributes redirectAttributes,
		Model model
	) {
		userService.validateUserId(bindingResult, id);
		if (bindingResult.hasErrors()) {
			return edit(id, form, model);
		}
		
		userService.update(id, form);
		redirectAttributes.addFlashAttribute("successMessage", utilityService.getMessage("users.edit.success"));
		return "redirect:/users/detail/" + id;
	}
	
	@GetMapping("/editimage/{id}")
	public String editImage(
        @PathVariable("id") Long id,
		@ModelAttribute UserForm userForm,
		Model model
	) {
		User user = userService.findById(id).orElseThrow();
		
		model.addAttribute("user", user);
		model.addAttribute("main", "users/edit_image::main");
		return "layouts/logged_in";
	}
	
	@PostMapping("/editimage/{id}")
	public String updateImage(
	    @PathVariable("id") Long id,
		@Validated(EditImage.class) UserForm form,
		BindingResult bindingResult,	// 必ずバリデーション用formの下に定義する
		RedirectAttributes redirectAttributes,
		Model model
	) {
		userService.validateUserId(bindingResult, id);
		utilityService.validateImageFile(bindingResult, form, mimeType, maxWidth, maxHeight);
		if (bindingResult.hasErrors()) {
			return editImage(id, form, model);
		}
		
		userService.updateImage(id, form);
		redirectAttributes.addFlashAttribute("successMessage", utilityService.getMessage("users.editimage.success"));
		return "redirect:/users/detail/" + id;
	}
}
