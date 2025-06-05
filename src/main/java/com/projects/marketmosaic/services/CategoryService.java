package com.projects.marketmosaic.services;

import com.projects.marketmosaic.common.dto.resp.BaseRespDTO;
import com.projects.marketmosaic.dtos.CategoryDTO;
import com.projects.marketmosaic.dtos.CategoryRespDTO;

public interface CategoryService {
    BaseRespDTO addCategories(CategoryDTO categoryDTO);

    BaseRespDTO deleteCategory(CategoryDTO.Category category);

    BaseRespDTO updateCategory(CategoryDTO.Category category);

    CategoryRespDTO getCategories();

}
