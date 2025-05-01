package com.projects.marketmosaic.utils;

import com.projects.marketmosaic.dtos.ProductDetailsDTO;
import com.projects.marketmosaic.entities.CategoryEntity;
import com.projects.marketmosaic.entities.ProductEntity;
import com.projects.marketmosaic.entities.TagEntity;
import com.projects.marketmosaic.entities.UserEntity;
import com.projects.marketmosaic.repositories.CategoryRepository;
import com.projects.marketmosaic.repositories.TagRepository;
import com.projects.marketmosaic.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductUtils {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    public ProductDetailsDTO mapProductDetails(ProductEntity productEntity) {
        ProductDetailsDTO productDetailsDTO = new ProductDetailsDTO();

        productDetailsDTO.setProductId(productEntity.getProductId());
        productDetailsDTO.setProductName(productEntity.getProductName());
        productDetailsDTO.setCategoryId(productEntity.getCategory().getCategoryId());
        productDetailsDTO.setPrice(productEntity.getPrice());
        productDetailsDTO.setDescription(productEntity.getDescription());
        productDetailsDTO.setImageUrl(productEntity.getImageUrl());
        productDetailsDTO.setIsActive(productEntity.getIsActive());
        productDetailsDTO.setStockQuantity(productEntity.getStockQuantity());
        productDetailsDTO.setSupplierId(productEntity.getSupplier().getId());
        productDetailsDTO.setTagIds(
                Optional.ofNullable(productEntity.getTags())
                        .filter(tags -> !tags.isEmpty())
                        .map(tagEntities -> tagEntities.stream()
                                .map(TagEntity::getTagId)
                                .toList()
                        )
                        .orElse(new ArrayList<>())
        );
        return productDetailsDTO;
    }

    public ProductEntity mapProductEntity(ProductDetailsDTO productDetailsDTO) {
        UserEntity userEntity = userRepository.findById(productDetailsDTO.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier Not Found : " + productDetailsDTO.getSupplierId()));

        CategoryEntity categoryEntity = categoryRepository.findById(productDetailsDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        ProductEntity productEntity = new ProductEntity();
        productEntity.setProductName(productDetailsDTO.getProductName());
        productEntity.setCategory(categoryEntity);
        productEntity.setPrice(productDetailsDTO.getPrice());
        productEntity.setDescription(productDetailsDTO.getDescription());
        productEntity.setDateAdded(LocalDateTime.now());
        productEntity.setIsActive(productDetailsDTO.getIsActive());
        productEntity.setImageUrl(productDetailsDTO.getImageUrl());
        productEntity.setStockQuantity(productDetailsDTO.getStockQuantity());
        productEntity.setSupplier(userEntity);
        productEntity.setDateAdded(LocalDateTime.now());

        if (productDetailsDTO.getTagIds() != null && !productDetailsDTO.getTagIds().isEmpty()) {
            List<TagEntity> tagEntityList = tagRepository.findAllById(productDetailsDTO.getTagIds());
            if (tagEntityList.isEmpty()) {
                log.error("Tags Not Found {}", productDetailsDTO.getTagIds());
            }
            productEntity.setTags(tagEntityList);
        }

        return productEntity;
    }
}
