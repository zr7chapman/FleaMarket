package com.example.fleamaket.form;

import org.springframework.web.multipart.MultipartFile;

public interface ImageForm {
	public MultipartFile getImage();
	public void setImage(MultipartFile image);
}
