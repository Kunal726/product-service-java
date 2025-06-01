package com.projects.marketmosaic.services;

import com.projects.marketmosaic.dtos.*;
import jakarta.validation.Valid;

import java.util.List;

public interface ProductService {

	BaseRespDTO addProduct(ProductDetailsDTO productDetailsDTO);

	BaseRespDTO addProducts(@Valid AddBulkProductReqDTO productDetailsDTOList);

	BaseRespDTO updateProduct(String productId, UpdateProductReqDTO updateProductReqDTO);

	BaseRespDTO updateProducts(UpdateProductBulkReqDTO updateProductBulkReqDTO);

	BaseRespDTO deleteProduct(String productId, Boolean deactivate);

	ProductRespDTO getProduct(String productId);

	ProductRespDTO getProductList(ProductFilterDTO productFilters);

	List<String> getProductSuggestions(String query);

}
