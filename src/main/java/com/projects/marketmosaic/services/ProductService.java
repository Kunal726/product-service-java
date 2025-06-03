package com.projects.marketmosaic.services;

import com.projects.marketmosaic.dtos.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.List;

public interface ProductService {

	BaseRespDTO addProduct(ProductDetailsDTO productDetailsDTO, HttpServletRequest request);

	BaseRespDTO addProducts(@Valid AddBulkProductReqDTO productDetailsDTOList, HttpServletRequest request);

	BaseRespDTO updateProduct(String productId, UpdateProductReqDTO updateProductReqDTO, HttpServletRequest request);

	BaseRespDTO updateProducts(UpdateProductBulkReqDTO updateProductBulkReqDTO, HttpServletRequest request);

	BaseRespDTO deleteProduct(String productId, Boolean deactivate, HttpServletRequest request);

	ProductRespDTO getProduct(String productId);

	ProductRespDTO getProductList(ProductFilterDTO productFilters);

	List<String> getProductSuggestions(String query);

}
