package com.projects.marketmosaic.services.impl;

import com.projects.marketmosaic.dtos.*;
import com.projects.marketmosaic.entities.ProductEntity;
import com.projects.marketmosaic.entities.ProductMediaEntity;
import com.projects.marketmosaic.exception.ProductException;
import com.projects.marketmosaic.repositories.ProductRepository;
import com.projects.marketmosaic.services.FileStorageService;
import com.projects.marketmosaic.services.ProductService;
import com.projects.marketmosaic.utils.ProductUtils;
import com.projects.marketmosaic.utils.UserUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;

	private final ProductUtils productUtils;

	private final FileStorageService fileStorageService;

	private final UserUtils userUtils;

	@Override
	public BaseRespDTO addProduct(ProductDetailsDTO productDetailsDTO, MultiValueMap<String, MultipartFile> mediaMap, HttpServletRequest request) {
		BaseRespDTO respDTO = new BaseRespDTO();
		try {
			if (productDetailsDTO != null) {

				if(mediaMap != null && !mediaMap.isEmpty() && productDetailsDTO.getProductMedia() != null && mediaMap.size() == productDetailsDTO.getProductMedia().size()) {
					productDetailsDTO.getProductMedia().forEach(productMedia -> {
						String key = productMedia.getMediaKey();
						if(StringUtils.isNotBlank(key) && mediaMap.containsKey(key)) {
							MultipartFile file = mediaMap.getFirst(key);
							productMedia.setMedia(file);
							productMedia.setType(file.getContentType());
						}
					});
				} else {
					productDetailsDTO.setProductMedia(null);
				}

				ProductEntity productEntity = productUtils.mapProductEntity(productDetailsDTO, request);
				productRepository.saveAndFlush(productEntity);
				respDTO.setStatus(true);
				respDTO.setCode(HttpStatus.OK.value());
				respDTO.setMessage("Product Added");
			}
		}
		catch (ProductException e) {
			log.error(e.getMessage());
			respDTO.setMessage(e.getMessage());
			respDTO.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			throw e;
		}
		return respDTO;
	}

	@Override
	public BaseRespDTO addProducts(@Valid AddBulkProductReqDTO addBulkProductReqDTO, MultiValueMap<String, MultipartFile> mediaMap, HttpServletRequest request) {
		BaseRespDTO respDTO = new BaseRespDTO();
		try {
			if (addBulkProductReqDTO.getProducts() != null && !addBulkProductReqDTO.getProducts().isEmpty()) {
				List<ProductEntity> productEntityList = addBulkProductReqDTO.getProducts().stream()
						.map(productDetailsDTO -> {
							if(mediaMap != null && !mediaMap.isEmpty() && productDetailsDTO.getProductMedia() != null) {
								productDetailsDTO.getProductMedia().forEach(productMedia -> {
									String key = productMedia.getMediaKey();
									if(StringUtils.isNotBlank(key) && mediaMap.containsKey(key)) {
										MultipartFile file = mediaMap.getFirst(key);
										productMedia.setMedia(file);
										productMedia.setType(file.getContentType());
									}
								});
							} else {
								productDetailsDTO.setProductMedia(null);
							}

							return productUtils.mapProductEntity(productDetailsDTO, request);
						}).toList();
				productRepository.saveAllAndFlush(productEntityList);
				respDTO.setStatus(true);
				respDTO.setCode(HttpStatus.OK.value());
				respDTO.setMessage("Product Added");
			}
			else {
				throw new ProductException("Product List Cannot be Empty or null", HttpStatus.BAD_REQUEST.value());
			}
		}
		catch (ProductException e) {
			log.error(e.getMessage());
			respDTO.setMessage(e.getMessage());
			respDTO.setCode(e.getErrorCode());
			throw e;
		}
		catch (RuntimeException e) {
			log.error(e.getMessage());
			respDTO.setMessage(e.getMessage());
			respDTO.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			throw e;
		}
		return respDTO;
	}

	@Override
	public BaseRespDTO updateProduct(String productId, UpdateProductReqDTO updateProductReqDTO,
			HttpServletRequest request) {
		BaseRespDTO respDTO = new BaseRespDTO();

		if (StringUtils.isNotBlank(productId) && updateProductReqDTO != null) {
			ProductEntity productEntity = productRepository.findById(Long.valueOf(productId))
					.orElseThrow(() -> new ProductException("Product Not Found", HttpStatus.NOT_FOUND.value()));

			productUtils.updateProduct(productEntity, updateProductReqDTO, request);

			productUtils.updateProductMedia(productEntity, updateProductReqDTO.getMediaUpdates());

			productRepository.save(productEntity);
			respDTO.setStatus(true);
			respDTO.setCode(HttpStatus.OK.value());
			respDTO.setMessage("Product Updated");
		}

		return respDTO;
	}

	@Override
	public BaseRespDTO updateProducts(UpdateProductBulkReqDTO updateProductBulkReqDTO, HttpServletRequest request) {
		BaseRespDTO respDTO = new BaseRespDTO();

		if (updateProductBulkReqDTO.getProducts().isEmpty())
			throw new ProductException("Request map empty", HttpStatus.BAD_REQUEST.value());

		Map<Long, UpdateProductReqDTO> productMap = updateProductBulkReqDTO.getProducts().stream()
				.collect(Collectors.toMap(data -> Long.valueOf(data.getProductId()),
						UpdateProductBulkReqDTO.UpdateProductData::getProduct));
		List<Long> productIds = productMap.keySet().stream().toList();
		List<ProductEntity> productsToUpdate = productRepository.findAllById(productIds);

		productsToUpdate.forEach(productEntity -> {
			UpdateProductReqDTO updateProductReqDTO = productMap.get(productEntity.getProductId());
			productUtils.updateProduct(productEntity, updateProductReqDTO, request);
			productUtils.updateProductMedia(productEntity, updateProductReqDTO.getMediaUpdates());
		});

		productRepository.saveAll(productsToUpdate);

		// Return success response
		respDTO.setStatus(true);
		respDTO.setCode(HttpStatus.OK.value());
		respDTO.setMessage("All products updated successfully");

		return respDTO;
	}

	@Override
	@Transactional
	public BaseRespDTO deleteProduct(String productId, Boolean deactivate, HttpServletRequest request) {
		BaseRespDTO baseRespDTO = new BaseRespDTO();
		baseRespDTO.setMessage("ProductId is empty");

		if (StringUtils.isNotBlank(productId)) {
			String role = userUtils.getRole(request);
			Long userId = userUtils.getUserId(request);

			ProductEntity product = productRepository.findById(Long.valueOf(productId))
					.orElseThrow(() -> new ProductException("Product Not Found", HttpStatus.NOT_FOUND.value()));

			if (!product.getSupplier().getId().equals(userId) && !"ADMIN".equals(role)) {
				throw new ProductException("Unauthorized to delete this Product", HttpStatus.UNAUTHORIZED.value());
			}

			if (deactivate == null || !deactivate) {
				List<String> mediaPaths = product.getProductMedia().stream().map(ProductMediaEntity::getUrl).toList();
				fileStorageService.deleteFiles(mediaPaths);
				productRepository.deleteById(Long.valueOf(productId));
			}
			else {
				productRepository.softDelete(productId);
			}
			baseRespDTO.setStatus(true);
			baseRespDTO.setCode(HttpStatus.OK.value());
			baseRespDTO.setMessage("Successfully Deleted");
		}
		return baseRespDTO;
	}

	@Override
	public ProductRespDTO getProduct(String productId, HttpServletRequest request) {
		ProductRespDTO respDTO = new ProductRespDTO();

		if (StringUtils.isNotBlank(productId)) {

			ProductEntity productEntity = productRepository.findById(Long.valueOf(productId))
					.orElseThrow(() -> new ProductException(HttpStatus.NOT_FOUND.value(), "Product Not Found"));

			if(userUtils.getRole(request).equalsIgnoreCase("USER") && !productEntity.getIsActive()) {
				throw new ProductException(HttpStatus.OK.value(), "Product Not Available");
			}
			ProductDetailsDTO productDetailsDTO = productUtils.mapProductDetails(productEntity);
			respDTO.setProduct(productDetailsDTO);

			respDTO.setStatus(true);
			respDTO.setCode(HttpStatus.OK.value());
			respDTO.setMessage("Product Found");
		}
		else {
			throw new ProductException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Product Id cannot be empty");
		}

		return respDTO;
	}

	@Override
	public ProductRespDTO getProductList(ProductFilterDTO productFilters, HttpServletRequest request) {
		ProductRespDTO respDTO = new ProductRespDTO();

		List<ProductEntity> productEntities = productRepository.findByFilters(productFilters, userUtils.getRole(request));

		List<ProductDetailsDTO> productDetailsList = productEntities.stream().map(productUtils::mapProductDetails)
				.toList();
		respDTO.setProductList(productDetailsList);

		respDTO.setStatus(true);
		respDTO.setCode(HttpStatus.OK.value());
		respDTO.setMessage("Product List : Entries " + productDetailsList.size());

		return respDTO;
	}

	@Override
	public List<String> getProductSuggestions(String query) {
		return productRepository.getProductSuggestions(query);
	}

}
