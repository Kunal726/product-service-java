package com.projects.marketmosaic.services;

import com.projects.marketmosaic.common.dto.product.resp.ProductDetailsDTO;
import com.projects.marketmosaic.common.dto.product.resp.ProductRespDTO;
import com.projects.marketmosaic.dtos.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {

	BaseRespDTO addProduct(ProductDetailsDTO productDetailsDTO, MultiValueMap<String, MultipartFile> mediaMap,
			HttpServletRequest request);

	BaseRespDTO addProducts(@Valid AddBulkProductReqDTO productDetailsDTOList,
			MultiValueMap<String, MultipartFile> mediaMap, HttpServletRequest request);

	BaseRespDTO updateProduct(String productId, UpdateProductReqDTO updateProductReqDTO, HttpServletRequest request);

	BaseRespDTO updateProducts(UpdateProductBulkReqDTO updateProductBulkReqDTO, HttpServletRequest request);

	BaseRespDTO deleteProduct(String productId, Boolean deactivate, HttpServletRequest request);

	ProductRespDTO getProduct(String productId, HttpServletRequest request);

	ProductRespDTO getProductList(ProductFilterDTO productFilters, HttpServletRequest request);

	List<String> getProductSuggestions(String query);

	ProductRespDTO getProducts(List<String> productIds);

}
