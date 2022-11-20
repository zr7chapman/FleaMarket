package com.example.fleamaket.service;

import java.util.List;
import java.util.Optional;

import org.springframework.validation.BindingResult;

import com.example.fleamaket.entity.User;
import com.example.fleamaket.form.UserForm;

public interface UserService {
	// ユーザー一覧の取得
    List<User> findAll();
    
    // ユーザーの取得
    Optional<User> findById(long id);
    
	void register(String name, String email, String password, String[] roles);
	
	User update(long id, UserForm form);
	
	void updateImage(long id, UserForm form);
	
	void validateUserId(BindingResult bindingResult, long id); 
	
	void validatePassword(BindingResult bindingResult, UserForm userForm);
	
	void validateEmail(BindingResult bindingResult, UserForm userForm);
	
	void copyToForm(User user, UserForm form);
}
