package com.example.fleamaket.service.impl;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import com.example.fleamaket.bean.Image;
import com.example.fleamaket.entity.User;
import com.example.fleamaket.form.UserForm;
import com.example.fleamaket.repository.UserRepository;
import com.example.fleamaket.service.UserService;
import com.example.fleamaket.service.UtilityService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final MessageSource message;
	private final UserRepository repository;
	private final PasswordEncoder passwordEncoder;
	private final UtilityService utilityService;
	
	private final String IMAGE_DIR;
	
	@Autowired
	public UserServiceImpl(
		MessageSource messageSource,
		UserRepository userRepository,
		PasswordEncoder passwordEncoder,
		Environment environment,
		UtilityService utilityService
	) {
		this.message = messageSource;
		this.repository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.utilityService = utilityService;
		this.IMAGE_DIR = environment.getProperty("images.imagedir");
	}
    @Transactional(readOnly = true)
    @Override
    public List<User> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findById(long id) {
        return repository.findById(id);
    }
	
	@Override
	@Transactional
	public void register(String name, String email, String password, String[] roles) {
		if (repository.findByEmail(email).isPresent()) {
            throw new RuntimeException(message.getMessage("mailAddress.duplicated", null, Locale.getDefault()));
        }
        // パスワードを暗号化
        String encodedPassword = passwordEncode(password);
        // ユーザー権限の配列を文字列にコンバート
        String joinedRoles = joinRoles(roles);
 
        // User エンティティの生成
        User user = new User(null, name, email, encodedPassword, joinedRoles, true, null, null, null, null, null);
 
        // ユーザー登録
        repository.saveAndFlush(user);

	}

	// パスワードを暗号化
    private String passwordEncode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
 
    // ユーザー権限の配列を文字列にコンバート
    private String joinRoles(String[] roles) {
        if (roles == null || roles.length == 0) {
            return "";
        }
        return Stream.of(roles)
            .map(String::trim)
            .map(String::toUpperCase)
            .collect(Collectors.joining(","));
    }
    
    @Override
	public void validateUserId(BindingResult bindingResult, long id) {
		try {
			repository.findById(id).orElseThrow();
		} catch (Exception e) {
			bindingResult.reject("id.notexist");
		}
	}

	@Override
	public void validatePassword(BindingResult bindingResult, UserForm userForm) {
		if (!userForm.getPassword().equals(userForm.getPassword_confirmation())) {
        	bindingResult.rejectValue("password", "password.unmatch");
    	}		
	}

	@Override
	public void validateEmail(BindingResult bindingResult, UserForm userForm) {
		try {
			repository.findByEmail(userForm.getEmail()).orElseThrow();
			bindingResult.rejectValue("email", "email.duplicated");
		} catch (Exception e) {}		
	}

	@Override
	public void copyToForm(User user, UserForm form) {
		form.setName(user.getName());
		form.setEmail(user.getEmail());
		form.setProfile(user.getProfile());
	}

	@Override
	public User update(long id, UserForm form) {
		User user= repository.findById(id).orElseThrow();
		
		user.setName(form.getName());
		user.setProfile(form.getProfile());
		return repository.saveAndFlush(user);
	}

	@Override
	public void updateImage(long id, UserForm form) {
		User user = repository.findById(id).orElseThrow();

		if (user.getImage() != null && !user.getImage().isEmpty()) {
			File oldFile = Paths.get(System.getProperty("user.dir"), IMAGE_DIR, user.getImage()).toFile();
			if (oldFile.exists()) {
				oldFile.delete();
			}
		}
		
		Image image = utilityService.getImageBean(form.getImage(), IMAGE_DIR);
        utilityService.uploadImage(image);
        
		user.setImage(image.getSaveFileName());
		repository.saveAndFlush(user);
	}
}
