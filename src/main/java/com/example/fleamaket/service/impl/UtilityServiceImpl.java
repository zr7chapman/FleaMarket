package com.example.fleamaket.service.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.example.fleamaket.bean.Image;
import com.example.fleamaket.form.ImageForm;
import com.example.fleamaket.service.UtilityService;

@Service
public class UtilityServiceImpl implements UtilityService {
	private ResourceBundle resource;
	
	public UtilityServiceImpl() {
		this.resource = ResourceBundle.getBundle("messages");
	}

	@Override
	public void uploadImage(Image file) {
        try {
            OutputStream stream = Files.newOutputStream(file.getPath());
            stream.write(file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	@Override
	public Image getImageBean(MultipartFile file, String saveDir) {
		Image bean = new Image();
		
		try {
			BufferedImage image = ImageIO.read(file.getInputStream());
			bean.setWidth(image.getWidth());
			bean.setHeight(image.getHeight());

			String newName = RandomStringUtils.randomAlphanumeric(20) + "." + FilenameUtils.getExtension(file.getOriginalFilename());
			Path path = Paths.get(System.getProperty("user.dir"), saveDir, newName);

			bean.setPath(path);
			bean.setOriginalFileName(file.getOriginalFilename());
			bean.setSaveFileName(newName);
			bean.setMimeType(file.getContentType());
			bean.setSize((int)file.getSize());
			bean.setBytes(file.getBytes());
		} catch (IOException | NullPointerException e) {
			e.printStackTrace();
		}
		
		return bean;
	}

	@Override
	public String getMessage(String key) {
		return resource.getString(key);
	}

	@Override
	public void validateImageFile(BindingResult bindingResult, ImageForm form, List<String> targetMimes, int maxWidth,
			int maxHeight) {
		if (form.getImage().isEmpty()) {
			bindingResult.rejectValue("image", "image.notselected");
			return;
		}
		
		Image image = getImageBean(form.getImage(), "");
		
		if (image.getMimeType() == null) {
			bindingResult.rejectValue("image", "image.notimage");
			return;
		}
		
		if (!targetMimes.contains(image.getMimeType().toLowerCase())) {
			bindingResult.rejectValue("image", "image.invalid", new Object[]{mimesString(targetMimes)}, "");
		}
		if (image.getWidth() > maxWidth) {
			bindingResult.rejectValue("image", "image.overlimit.width", new Object[]{maxWidth}, "");
		}
		if (image.getHeight() > maxHeight) {
			bindingResult.rejectValue("image", "image.overlimit.height", new Object[]{maxHeight}, "");
		}
		if (image.getSize() == 0) {
			bindingResult.rejectValue("image", "image.nosize");
		}		
	}

	private String mimesString(List<String> mimes) {
		List<String> list = new ArrayList<>();
		Pattern pattern = Pattern.compile("^.+\\/(.+)$");
		for(String mime : mimes) {
			Matcher match = pattern.matcher(mime);
			if (match.find()) {
				list.add(match.group(1).toUpperCase());
			}
		}
		return String.join("か", list);
	}

}
