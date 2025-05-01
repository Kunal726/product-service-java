package com.projects.marketmosaic.services.impl;

import com.projects.marketmosaic.dtos.*;
import com.projects.marketmosaic.entities.ProductEntity;
import com.projects.marketmosaic.exception.ProductException;
import com.projects.marketmosaic.repositories.ProductRepository;
import com.projects.marketmosaic.repositories.UserRepository;
import com.projects.marketmosaic.services.ProductService;
import com.projects.marketmosaic.utils.ProductUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductUtils productUtils;

    @Override
    public BaseRespDTO addProduct(ProductDetailsDTO productDetailsDTO) {
        BaseRespDTO respDTO = new BaseRespDTO();
        try {
            if (productDetailsDTO != null) {
                ProductEntity productEntity = productUtils.mapProductEntity(productDetailsDTO);

                productRepository.saveAndFlush(productEntity);
                respDTO.setStatus(true);
                respDTO.setCode(HttpStatus.OK.value());
                respDTO.setMessage("Product Added");
            }
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            respDTO.setMessage(e.getMessage());
            respDTO.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return respDTO;
    }

    @Override
    public BaseRespDTO addProducts(List<ProductDetailsDTO> productDetailsDTOList) {
        BaseRespDTO respDTO = new BaseRespDTO();
        try {
            if (productDetailsDTOList != null && !productDetailsDTOList.isEmpty()) {
                List<ProductEntity> productEntityList = productDetailsDTOList.stream()
                        .map(productUtils::mapProductEntity).toList();
                productRepository.saveAllAndFlush(productEntityList);
                respDTO.setStatus(true);
                respDTO.setCode(HttpStatus.OK.value());
                respDTO.setMessage("Product Added");
            } else {
                throw new ProductException("Product List Cannot be Empty or null", HttpStatus.BAD_REQUEST.value());
            }
        } catch (ProductException e) {
            log.error(e.getMessage());
            respDTO.setMessage(e.getMessage());
            respDTO.setCode(e.getErrorCode());
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            respDTO.setMessage(e.getMessage());
            respDTO.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return respDTO;
    }

    @Override
    public BaseRespDTO updateProduct(String productId, UpdateProductReqDTO updateProductReqDTO) {
        BaseRespDTO respDTO = new BaseRespDTO();
        try {
            if (StringUtils.isNotBlank(productId) && updateProductReqDTO != null) {
                ProductEntity productEntity = productRepository.findById(Long.valueOf(productId))
                        .orElseThrow(() -> new ProductException("Product Not Found", HttpStatus.NOT_FOUND.value()));

                if (updateProductReqDTO.getPrice() != null && updateProductReqDTO.getPrice() > 0) {
                    productEntity.setPrice(BigDecimal.valueOf(updateProductReqDTO.getPrice()));
                }
                if (updateProductReqDTO.getStock() != null) {
                    productEntity.setStockQuantity(updateProductReqDTO.getStock());
                }
                productRepository.save(productEntity);
                respDTO.setStatus(true);
                respDTO.setCode(HttpStatus.OK.value());
                respDTO.setMessage("Product Updated");
            }
        } catch (ProductException e) {
            log.error(e.getMessage());
            respDTO.setMessage(e.getMessage());
            respDTO.setCode(e.getErrorCode());
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            respDTO.setMessage(e.getMessage());
            respDTO.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return respDTO;
    }

    @Override
    public BaseRespDTO updateProducts(Map<Long, UpdateProductReqDTO> productReqDTOMap) {
        BaseRespDTO respDTO = new BaseRespDTO();
        try {
            if (productReqDTOMap.isEmpty())
                throw new ProductException("Request map empty", HttpStatus.BAD_REQUEST.value());
            List<Long> productIds = productReqDTOMap.keySet().stream().toList();

            List<ProductEntity> productsToUpdate = productRepository.findAllById(productIds);

            productsToUpdate.forEach(
                    productEntity -> {
                        UpdateProductReqDTO updateProductReqDTO = productReqDTOMap.get(productEntity.getProductId());
                        if (updateProductReqDTO.getPrice() != null && updateProductReqDTO.getPrice() > 0) {
                            productEntity.setPrice(BigDecimal.valueOf(updateProductReqDTO.getPrice()));
                        }
                        if (updateProductReqDTO.getStock() != null) {
                            productEntity.setStockQuantity(updateProductReqDTO.getStock());
                        }
                    }
            );

            productRepository.saveAll(productsToUpdate);

            // Return success response
            respDTO.setStatus(true);
            respDTO.setCode(HttpStatus.OK.value());
            respDTO.setMessage("All products updated successfully");
        } catch (ProductException e) {
            log.error(e.getMessage());
            respDTO.setMessage(e.getMessage());
            respDTO.setCode(e.getErrorCode());
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            respDTO.setMessage(e.getMessage());
            respDTO.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return respDTO;
    }

    @Override
    public BaseRespDTO deleteProduct(String productId, Boolean deactivate) {
        BaseRespDTO baseRespDTO = new BaseRespDTO();
        baseRespDTO.setMessage("ProductId is empty");
        if (StringUtils.isNotBlank(productId)) {
            if (deactivate != null && !deactivate) {
                productRepository.deleteById(Long.valueOf(productId));
            } else {
                productRepository.softDelete(productId);
            }
            baseRespDTO.setStatus(true);
            baseRespDTO.setCode(HttpStatus.OK.value());
            baseRespDTO.setMessage("Successfully Deleted");
        }
        return baseRespDTO;
    }

    @Override
    public ProductRespDTO getProduct(String productId) {
        ProductRespDTO respDTO = new ProductRespDTO();
        try {
            if (StringUtils.isNotBlank(productId)) {

                ProductEntity productEntity = productRepository.findById(Long.valueOf(productId))
                        .orElseThrow(() -> new ProductException(HttpStatus.NOT_FOUND.value(), "Product Not Found"));

                ProductDetailsDTO productDetailsDTO = productUtils.mapProductDetails(productEntity);
                respDTO.setProduct(productDetailsDTO);

                respDTO.setStatus(true);
                respDTO.setCode(HttpStatus.OK.value());
                respDTO.setMessage("Product Found");
            } else {
                throw new ProductException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Product Id cannot be empty");
            }
        } catch (ProductException e) {
            log.error(e.getMessage());
            respDTO.setMessage(e.getMessage());
            respDTO.setCode(e.getErrorCode());
        } catch (Exception e) {
            log.error(e.getMessage());
            respDTO.setMessage(e.getMessage());
            respDTO.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return respDTO;
    }

    @Override
    public ProductRespDTO getProductList(ProductFilterDTO productFilters) {
        ProductRespDTO respDTO = new ProductRespDTO();

        List<ProductEntity> productEntities = productRepository.findByFilters(productFilters);

        List<ProductDetailsDTO> productDetailsList = productEntities.stream()
                .map(productUtils::mapProductDetails).toList();
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
