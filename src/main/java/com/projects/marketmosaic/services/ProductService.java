package com.projects.marketmosaic.services;

import com.projects.marketmosaic.dtos.*;

import java.util.List;
import java.util.Map;

public interface ProductService {
    BaseRespDTO addProduct(ProductDetailsDTO productDetailsDTO);

    BaseRespDTO addProducts(List<ProductDetailsDTO> productDetailsDTOList);

    BaseRespDTO updateProduct(String productId, UpdateProductReqDTO updateProductReqDTO);

    BaseRespDTO updateProducts(Map<Long, UpdateProductReqDTO> productReqDTOMap);

    BaseRespDTO deleteProduct(String productId, Boolean deactivate);

    ProductRespDTO getProduct(String productId);

    ProductRespDTO getProductList(ProductFilterDTO productFilters);

    List<String> getProductSuggestions(String query);
}

