package com.projects.marketmosaic.controller;

import com.projects.marketmosaic.common.dto.product.resp.ProductDetailsDTO;
import com.projects.marketmosaic.dtos.*;
import com.projects.marketmosaic.common.dto.product.resp.ProductRespDTO;
import com.projects.marketmosaic.services.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;

	@PostMapping(path = "/seller/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	ResponseEntity<BaseRespDTO> addProduct(@RequestPart("product") @Valid ProductDetailsDTO productDetailsDTO,
			@RequestParam(required = false) MultiValueMap<String, MultipartFile> mediaMap, HttpServletRequest request) {
		log.info("AddProduct Request :: {} ", productDetailsDTO);

		BaseRespDTO respDTO = productService.addProduct(productDetailsDTO, mediaMap, request);
		log.info("AddProduct Response :: {} ", respDTO);
		return ResponseEntity.ok(respDTO);
	}

	@PostMapping(path = "/seller/products/bulk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	ResponseEntity<BaseRespDTO> addProducts(
			@RequestPart("bulkProducts") @Valid AddBulkProductReqDTO addBulkProductReqDTO,
			@RequestParam(required = false) MultiValueMap<String, MultipartFile> mediaMap, HttpServletRequest request) {
		log.info("AddProducts Request :: {} ", addBulkProductReqDTO.toString());
		BaseRespDTO respDTO = productService.addProducts(addBulkProductReqDTO, mediaMap, request);
		log.info("AddProducts Response :: {} ", respDTO);
		return ResponseEntity.ok(respDTO);
	}

	@PutMapping(path = "/seller/products/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	ResponseEntity<BaseRespDTO> updateProduct(@PathVariable("productId") String productId,
			@ModelAttribute @Valid UpdateProductReqDTO updateProductReqDTO, HttpServletRequest request) {
		log.info("updateProduct Request :: {} ", updateProductReqDTO);
		BaseRespDTO respDTO = productService.updateProduct(productId, updateProductReqDTO, request);
		log.info("updateProduct Response :: {} ", respDTO);
		return ResponseEntity.ok(respDTO);
	}

	@PostMapping(path = "/seller/products/bulk-update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	ResponseEntity<BaseRespDTO> updateProducts(@ModelAttribute UpdateProductBulkReqDTO updateProductBulkReqDTO,
			HttpServletRequest request) {
		log.info("updateProducts Request :: {} ", updateProductBulkReqDTO);
		BaseRespDTO respDTO = productService.updateProducts(updateProductBulkReqDTO, request);
		log.info("updateProducts Response :: {} ", respDTO);
		return ResponseEntity.ok(respDTO);
	}

	@DeleteMapping(path = "/seller/products/{productId}")
	ResponseEntity<BaseRespDTO> deleteProduct(@PathVariable("productId") String productId,
			@RequestParam(required = false) Boolean deactivate, HttpServletRequest request) {
		log.info("DeleteProduct Request :: {} ", productId);
		BaseRespDTO respDTO = productService.deleteProduct(productId, deactivate, request);
		log.info("DeleteProduct Response :: {} ", respDTO);
		return ResponseEntity.ok(respDTO);
	}

	@GetMapping("/products/{productId}")
	ResponseEntity<ProductRespDTO> getProduct(@PathVariable("productId") String productId, HttpServletRequest request) {
		log.info("getProduct Request :: {} ", productId);
		ProductRespDTO respDTO = productService.getProduct(productId, request);
		log.info("getProduct Response :: {} ", respDTO);
		return ResponseEntity.ok(respDTO);
	}

	@GetMapping("/products")
	ResponseEntity<ProductRespDTO> getProductList(@ModelAttribute ProductFilterDTO productFilterDTO,
			HttpServletRequest request) {
		log.info("getProductList Request : Filters :: {}", productFilterDTO);
		ProductRespDTO respDTO = productService.getProductList(productFilterDTO, request);
		log.info("getProductList Response :: {} ", respDTO);
		return ResponseEntity.ok(respDTO);
	}

	@GetMapping("/products/suggestions")
	public ResponseEntity<List<String>> getSuggestions(@RequestParam String query) {
		List<String> suggestions = productService.getProductSuggestions(query);
		return ResponseEntity.ok(suggestions);
	}

}
