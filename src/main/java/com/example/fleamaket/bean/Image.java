package com.example.fleamaket.bean;

import java.nio.file.Path;

import lombok.Data;

@Data
public class Image {
	private Path path;
	private String originalFileName;
	private String saveFileName;
	private String mimeType;
	private int width;
	private int height;
	private int size;
	private byte[] bytes;
}
