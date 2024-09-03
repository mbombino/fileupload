package com.bequits.spring.service.fileupload;

import com.bequits.spring.service.fileupload.service.FileStorageService;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication()
public class FileuploadApplication implements CommandLineRunner {
	@Resource
	FileStorageService fileStorageService;

	public static void main(String[] args) {
		SpringApplication.run(FileuploadApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		fileStorageService.init();

	}
}
