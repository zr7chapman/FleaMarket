package com.example.fleamaket.service;

import java.util.List;

import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.example.fleamaket.bean.Image;
import com.example.fleamaket.form.ImageForm;

public interface UtilityService {
	public void uploadImage(Image file);
	
	public Image getImageBean(MultipartFile file, String dir);
	
	public String getMessage(String key);
	
	public void validateImageFile(BindingResult bindingResult, ImageForm form, List<String> targetMimes, int maxWidth, int maxHeight);
	
}
