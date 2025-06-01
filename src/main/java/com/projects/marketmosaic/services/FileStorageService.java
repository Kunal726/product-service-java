package com.projects.marketmosaic.services;

import com.projects.marketmosaic.exception.ProductException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileStorageService {

	String saveFile(MultipartFile multipartFile, String productName, Long id) throws ProductException;

	void deleteFiles(List<String> filePaths) throws ProductException;

	void deleteFile(String url);

}
