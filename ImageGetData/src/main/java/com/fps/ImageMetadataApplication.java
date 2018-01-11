package com.fps;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fps.service.ImageExifPhotographer;
import com.fps.service.ImageExifSubjectKeyword;

@SpringBootApplication
public class ImageMetadataApplication implements CommandLineRunner {

	@Autowired
	ImageExifPhotographer image;

	@Autowired
	ImageExifSubjectKeyword subkeyword;
	

	public static void main(String[] args) {
		SpringApplication.run(ImageMetadataApplication.class, args);
	}

	@Override
	public void run(String... arg0) {
		subkeyword.subjectKeyword();
	}
}
