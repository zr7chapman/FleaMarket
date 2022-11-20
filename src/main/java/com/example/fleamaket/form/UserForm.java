package com.example.fleamaket.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserForm implements ImageForm{
	@NotBlank(groups = { Create.class, Edit.class })
	@Size(max = 255, groups = { Create.class, Edit.class })
	private String name;

	@NotBlank(groups = { Create.class, Login.class })
	@Size(min = 8, groups = { Create.class, Login.class })
	private String password;

	@NotBlank(groups = Create.class)
	private String password_confirmation;

	@NotBlank(groups = { Create.class, Login.class })
	@Email(groups = { Create.class, Login.class })
	@Size(max = 255, groups = { Create.class, Login.class })
	private String email;

	@Size(max = 1000, groups = Edit.class)
	private String profile;

	private MultipartFile image;
}
