package com.projects.marketmosaic.utils;

import com.projects.marketmosaic.common.dto.product.resp.ProductDetailsDTO;
import com.projects.marketmosaic.common.utils.UserUtils;
import com.projects.marketmosaic.dtos.UpdateProductReqDTO;
import com.projects.marketmosaic.entities.*;
import com.projects.marketmosaic.exception.ProductException;
import com.projects.marketmosaic.repositories.CategoryRepository;
import com.projects.marketmosaic.repositories.TagRepository;
import com.projects.marketmosaic.repositories.UserRepository;
import com.projects.marketmosaic.services.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductUtils {

	private final UserRepository userRepository;

	private final CategoryRepository categoryRepository;

	private final TagRepository tagRepository;

	private final FileStorageService fileStorageService;

	private final UserUtils userUtils;

	public ProductDetailsDTO mapProductDetails(ProductEntity productEntity) {
		ProductDetailsDTO productDetailsDTO = new ProductDetailsDTO();

		productDetailsDTO.setProductId(productEntity.getProductId());
		productDetailsDTO.setProductName(productEntity.getProductName());
		productDetailsDTO.setCategory(new ProductDetailsDTO.IdNameDto(productEntity.getCategory().getCategoryId(),
				productEntity.getCategory().getCategoryName()));
		productDetailsDTO.setPrice(productEntity.getPrice());
		productDetailsDTO.setDescription(productEntity.getDescription());
		productDetailsDTO.setIsActive(productEntity.getIsActive());
		productDetailsDTO.setStockQuantity(productEntity.getStockQuantity());
		productDetailsDTO.setSupplierName(productEntity.getSupplier().getName());
		productDetailsDTO
				.setTags(Optional
						.ofNullable(
								productEntity.getTags())
						.filter(tags -> !tags.isEmpty())
						.map(tagEntities -> tagEntities.stream()
								.map(tagEntity -> new ProductDetailsDTO.IdNameDto(tagEntity.getTagId(),
										tagEntity.getTagName()))
								.toList())
						.orElse(new ArrayList<>()));

		List<ProductDetailsDTO.ProductMedia> productMediaList = productEntity.getProductMedia().stream()
				.map(productMediaEntity -> {
					ProductDetailsDTO.ProductMedia productMedia = new ProductDetailsDTO.ProductMedia();
					productMedia.setId(String.valueOf(productMediaEntity.getId()));
					productMedia.setUrl(productMediaEntity.getUrl());
					productMedia.setAltText(productMediaEntity.getAltText());
					productMedia.setType(productMediaEntity.getType());
					productMedia.setDefault(productMediaEntity.isDefault());
					return productMedia;
				}).toList();

		productDetailsDTO.setProductMedia(productMediaList);
		productDetailsDTO.setRating(productEntity.getRating());
		return productDetailsDTO;
	}

	@Transactional(rollbackFor = RuntimeException.class)
	public ProductEntity mapProductEntity(ProductDetailsDTO productDetailsDTO, HttpServletRequest request) {
		UserEntity userEntity = userRepository.findById(userUtils.getUserId(request))
				.orElseThrow(() -> new RuntimeException("Supplier Not Found"));

		CategoryEntity categoryEntity = categoryRepository.findById(productDetailsDTO.getCategory().getId())
				.orElseThrow(() -> new RuntimeException("Category not found"));

		ProductEntity productEntity = new ProductEntity();
		productEntity.setProductName(productDetailsDTO.getProductName());
		productEntity.setCategory(categoryEntity);
		productEntity.setPrice(productDetailsDTO.getPrice());
		productEntity.setDescription(productDetailsDTO.getDescription());
		productEntity.setDateAdded(LocalDateTime.now());
		productEntity.setIsActive(productDetailsDTO.getIsActive() != null ? productDetailsDTO.getIsActive() : true);
		productEntity.setStockQuantity(productDetailsDTO.getStockQuantity());
		productEntity.setSupplier(userEntity);
		productEntity.setDateAdded(LocalDateTime.now());

		if (productDetailsDTO.getTags() != null && !productDetailsDTO.getTags().isEmpty()) {
			List<Long> tagIds = productDetailsDTO.getTags().stream().map(ProductDetailsDTO.IdNameDto::getId).toList();
			List<TagEntity> tagEntityList = tagRepository.findAllById(tagIds);
			if (tagEntityList.isEmpty()) {
				log.error("Tags Not Found {}", tagIds);
			}
			productEntity.setTags(tagEntityList);
		}

		List<String> savedPaths = new ArrayList<>();

		try {
			saveProductMedia(productDetailsDTO.getProductMedia(), productEntity, savedPaths,
					productDetailsDTO.getProductName());
		}
		catch (ProductException e) {
			fileStorageService.deleteFiles(savedPaths);
		}

		return productEntity;
	}

	private void saveProductMedia(List<ProductDetailsDTO.ProductMedia> productMediaList, ProductEntity productEntity,
			List<String> savedPaths, String productName) throws ProductException {
		boolean isDefault = false;

		if (productMediaList != null) {
			for (ProductDetailsDTO.ProductMedia productMedia : productMediaList) {
				ProductMediaEntity productMediaEntity = new ProductMediaEntity();
				String url = fileStorageService.saveFile(productMedia.getMedia(), productEntity.getProductName(),
						productEntity.getSupplier().getId());
				savedPaths.add(url);
				productMediaEntity.setUrl(url);
				productMediaEntity.setAltText(productName + productMedia.getMedia().getOriginalFilename());
				productMediaEntity.setProduct(productEntity);
				productMediaEntity.setType(productMedia.getType());
				productMediaEntity.setDefault(productMedia.isDefault());

				if(productMedia.isDefault()) {
					if(isDefault) {
						throw new ProductException(HttpStatus.CONFLICT.value(), "Only single product media can be default");
					}
				}

				productEntity.getProductMedia().add(productMediaEntity);
				isDefault = productMedia.isDefault();
			}
		}

	}

	public void updateProductMedia(ProductEntity product,
			List<UpdateProductReqDTO.ProductMediaUpdateDTO> mediaUpdates) {
		if (mediaUpdates == null) {
			return; // Nothing to update
		}

		Map<String, ProductMediaEntity> existingMediaMap = product.getProductMedia().stream()
				.collect(Collectors.toMap(pm -> pm.getId().toString(), pm -> pm));

		for (UpdateProductReqDTO.ProductMediaUpdateDTO mediaDTO : mediaUpdates) {
			String mediaId = mediaDTO.getId();
			MultipartFile file = mediaDTO.getFile();

			if (mediaId == null) {
				// Add new media
				String url = fileStorageService.saveFile(file, product.getProductName(), product.getSupplier().getId());

				ProductMediaEntity newMedia = new ProductMediaEntity();
				newMedia.setUrl(url);
				newMedia.setAltText(product.getProductName() + file.getOriginalFilename());
				newMedia.setProduct(product);
				newMedia.setType(mediaDTO.getType());
				product.getProductMedia().add(newMedia);

			}
			else {
				// Update existing media
				ProductMediaEntity existingMedia = existingMediaMap.get(mediaId);
				if (existingMedia == null) {
					throw new RuntimeException("Media with id " + mediaId + " not found");
				}

				if (file != null) {
					// Replace file
					fileStorageService.deleteFile(existingMedia.getUrl());
					String url = fileStorageService.saveFile(file, product.getProductName(),
							product.getSupplier().getId());
					existingMedia.setUrl(url);
					existingMedia.setAltText(product.getProductName() + file.getOriginalFilename());
					existingMedia.setType(mediaDTO.getType());
				}
				else {
					product.getProductMedia().removeIf(pm -> {
						if (Objects.equals(pm.getId(), Long.valueOf(mediaId))) {
							fileStorageService.deleteFile(pm.getUrl());
							return true;
						}
						return false;
					});
				}
			}
		}
	}

	public void updateProduct(ProductEntity productEntity, UpdateProductReqDTO updateProductReqDTO,
			HttpServletRequest request) {

		Long userId = userUtils.getUserId(request);
		String role = userUtils.getRole(request);
		if (!productEntity.getSupplier().getId().equals(userId) && !"ADMIN".equals(role)) {
			throw new ProductException("Unauthorized to edit this Product", HttpStatus.UNAUTHORIZED.value());
		}

		if (StringUtils.isNotBlank(updateProductReqDTO.getProductName())) {
			if (updateProductReqDTO.getProductName().length() < 3
					|| updateProductReqDTO.getProductName().length() > 100) {
				throw new IllegalArgumentException("Product name must be between 3 and 100 characters");
			}
			productEntity.setProductName(updateProductReqDTO.getProductName());
		}

		if (StringUtils.isNotBlank(updateProductReqDTO.getDescription())) {
			if (updateProductReqDTO.getDescription().length() > 500) {
				throw new IllegalArgumentException("Description cannot exceed 500 characters");
			}
			productEntity.setDescription(updateProductReqDTO.getDescription());
		}

		if (updateProductReqDTO.getPrice() != null) {
			if (updateProductReqDTO.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
				throw new IllegalArgumentException("Price must be positive");
			}
			productEntity.setPrice(updateProductReqDTO.getPrice());
		}

		if (updateProductReqDTO.getStock() != null) {
			if (updateProductReqDTO.getStock() < 0) {
				throw new IllegalArgumentException("Stock must be greater than or equal to 0");
			}
			productEntity.setStockQuantity(updateProductReqDTO.getStock());
		}

		if (StringUtils.isNotBlank(updateProductReqDTO.getRating())) {
			if (updateProductReqDTO.getRating().length() > 5) {
				throw new IllegalArgumentException("Rating should be at most 5 characters");
			}
			productEntity.setRating(updateProductReqDTO.getRating());
		}

		if (updateProductReqDTO.getIsActive() != null) {
			productEntity.setIsActive(updateProductReqDTO.getIsActive());
		}

		if (updateProductReqDTO.getCategoryId() != null) {
			CategoryEntity categoryEntity = categoryRepository.findById(updateProductReqDTO.getCategoryId())
					.orElseThrow(() -> new RuntimeException("Category not found"));
			productEntity.setCategory(categoryEntity);
		}

		if (updateProductReqDTO.getTagIds() != null && !updateProductReqDTO.getTagIds().isEmpty()) {
			List<TagEntity> tagEntityList = tagRepository.findAllById(updateProductReqDTO.getTagIds());
			if (tagEntityList.isEmpty()) {
				log.warn("Tags not found for IDs: {}", updateProductReqDTO.getTagIds());
			}
			productEntity.setTags(tagEntityList);
		}
	}

}
