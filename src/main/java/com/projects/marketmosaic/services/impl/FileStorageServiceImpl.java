package com.projects.marketmosaic.services.impl;

import com.projects.marketmosaic.exception.ProductException;
import com.projects.marketmosaic.services.FileStorageService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

	@Value("${file.storage.location}")
	private String storageLocation;

	@PostConstruct
	public void init() {
		File dir = new File(storageLocation);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	@Override
	public String saveFile(MultipartFile file, String productName, Long id) throws ProductException {
		try {
			String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
			String fileName = UUID.randomUUID() + "_" + productName + "_" + id + "_" + originalFilename;

			Path destination = Paths.get(storageLocation).resolve(fileName);
			Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

			return "/uploads/" + fileName; // or just return `fileName` if storing only
											// name
		}
		catch (IOException e) {
			throw new ProductException("Unable to Save File", e.getCause());
		}
	}

	@Override
	public void deleteFiles(List<String> filePaths) throws ProductException {
		for (String filePath : filePaths) {
			this.deleteFile(filePath);
		}
	}

	@Override
	public void deleteFile(String filePath) {
		try {
			File file = new File(filePath);
			if (file.exists()) {
				boolean deleted = file.delete();
				if (!deleted) {
					throw new ProductException("Failed to delete file: " + filePath);
				}
			}
			else {
				throw new ProductException("File does not exist: " + filePath);
			}
		}
		catch (Exception e) {
			throw new ProductException("Error deleting file: " + filePath, e);
		}
	}

}
