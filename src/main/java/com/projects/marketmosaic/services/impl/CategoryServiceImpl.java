package com.projects.marketmosaic.services.impl;

import com.projects.marketmosaic.common.dto.resp.BaseRespDTO;
import com.projects.marketmosaic.constants.Constants;
import com.projects.marketmosaic.dtos.CategoryDTO;
import com.projects.marketmosaic.dtos.CategoryDataDTO;
import com.projects.marketmosaic.dtos.CategoryRespDTO;
import com.projects.marketmosaic.entities.CategoryEntity;
import com.projects.marketmosaic.exception.ProductException;
import com.projects.marketmosaic.repositories.CategoryRepository;
import com.projects.marketmosaic.services.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public BaseRespDTO addCategories(CategoryDTO categoryDTO) {
        BaseRespDTO respDTO = new BaseRespDTO();

        try {

            if (categoryDTO != null && categoryDTO.getCategories() != null) {

                List<CategoryEntity> categories = new ArrayList<>();

                categoryDTO.getCategories().forEach(category -> {
                    CategoryEntity categoryEntity = new CategoryEntity();
                    categoryEntity.setCategoryName(category.getCategoryName());

                    if (StringUtils.isNotBlank(category.getParentCategoryId())) {
                        Long parentId = Long.valueOf(category.getParentCategoryId());
                        CategoryEntity parent = categoryRepository.findById(parentId)
                                .orElseThrow(() -> new ProductException("Parent Category not found " + parentId, HttpStatus.NOT_FOUND.value()));

                        categoryEntity.setParentCategory(parent);
                    }

                    categories.add(categoryEntity);
                });

                categoryRepository.saveAll(categories);
                respDTO.setCode("200");
                respDTO.setStatus(true);
                respDTO.setMessage("Categories Added Successfully");

                if (categories.size() == 1) {
                    respDTO.setMessage("Category Added Successfully");
                }

            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e.getMessage());
            throw new ProductException(e.getCause(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return respDTO;
    }

    @Override
    public BaseRespDTO deleteCategory(CategoryDTO.Category category) {
        BaseRespDTO respDTO = new BaseRespDTO();

        try {
            if(category != null && StringUtils.isNotBlank(category.getId())) {
                Long categoryId = Long.parseLong(category.getId());
                categoryRepository.deleteById(categoryId);

                respDTO.setMessage("Category Deleted Successfully");
                respDTO.setCode("200");
                respDTO.setStatus(true);
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e.getMessage());
            throw new ProductException(e.getCause(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return respDTO;
    }

    @Override
    public BaseRespDTO updateCategory(CategoryDTO.Category category) {
        BaseRespDTO respDTO = new BaseRespDTO();

        try {
            if(category != null && StringUtils.isNotBlank(category.getId())) {
                Long categoryId = Long.parseLong(category.getId());
                CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new ProductException("Category Not Found", HttpStatus.NOT_FOUND.value()));

                if(StringUtils.isNotBlank(category.getCategoryName())) {
                    categoryEntity.setCategoryName(category.getCategoryName());
                }

                if(StringUtils.isNotBlank(category.getParentCategoryId())) {
                    Long parentCategoryId = Long.parseLong(category.getParentCategoryId());
                    CategoryEntity parentCategory = categoryRepository.findById(parentCategoryId)
                            .orElseThrow(() -> new ProductException("Parent Category Not Found", HttpStatus.NOT_FOUND.value()));

                    categoryEntity.setParentCategory(parentCategory);

                } else if(categoryEntity.getParentCategory() != null) {
                    categoryEntity.setParentCategory(null);
                }

                categoryRepository.save(categoryEntity);

                respDTO.setMessage("Category Updated Successfully");
                respDTO.setCode("200");
                respDTO.setStatus(true);
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e.getMessage());
            throw new ProductException(e.getCause(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return respDTO;
    }

    @Override
    public CategoryRespDTO getCategories() {
        CategoryRespDTO categoryRespDTO = new CategoryRespDTO();

        try {
            List<CategoryEntity> categoryList = categoryRepository.findAll();

            if(!categoryList.isEmpty()) {
                Map<Long, CategoryDataDTO> categoryDataDTOMap = new HashMap<>();
                List<CategoryDataDTO> categoryDataDTOList = new ArrayList<>();

                categoryList.forEach(category -> {
                    CategoryDataDTO categoryDataDTO = new CategoryDataDTO();
                    categoryDataDTO.setId(category.getCategoryId());
                    categoryDataDTO.setCategoryName(category.getCategoryName());
                    categoryDataDTO.setSubCategories(new ArrayList<>());
                    categoryDataDTOMap.put(category.getCategoryId(), categoryDataDTO);
                });

                categoryList.forEach(category -> {
                    CategoryDataDTO categoryDataDTO = categoryDataDTOMap.get(category.getCategoryId());
                    CategoryEntity parent = category.getParentCategory();

                    if(parent != null) {
                        CategoryDataDTO parentCategory = categoryDataDTOMap.get(parent.getCategoryId());
                        parentCategory.getSubCategories().add(categoryDataDTO);
                    } else {
                        categoryDataDTOList.add(categoryDataDTO);
                    }
                });

                categoryRespDTO.setCategories(categoryDataDTOList);
                categoryRespDTO.setMessage("Categories fetched Successfully");

            } else {
                categoryRespDTO.setMessage("No Categories Found");
            }

            categoryRespDTO.setStatus(true);
            categoryRespDTO.setCode(HttpStatus.OK.value());


        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e.getMessage());
            throw new ProductException(e.getCause(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return categoryRespDTO;
    }
}
