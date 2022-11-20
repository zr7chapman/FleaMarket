package com.example.fleamaket.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class ItemForm implements ImageForm{
	@NotBlank(groups = { Create.class, Edit.class })
	@Size(max = 255, groups = { Create.class, Edit.class })
	private String name;

	@NotBlank(groups = { Create.class, Edit.class })
	@Size(max = 1000, groups = { Create.class, Edit.class })
	private String description;

	@Positive(groups = { Create.class, Edit.class })
	private long category_id;

	@Range(min = 1, max = 1000000, groups = { Create.class, Edit.class })
	@PositiveOrZero(groups = { Create.class, Edit.class })
	private int price;

	private MultipartFile image;
}
