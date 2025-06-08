package com.projects.marketmosaic.services;

import com.projects.marketmosaic.common.dto.resp.BaseRespDTO;
import com.projects.marketmosaic.dtos.Category;
import com.projects.marketmosaic.dtos.CategoryDTO;
import com.projects.marketmosaic.dtos.CategoryRespDTO;

public interface CategoryService {
    BaseRespDTO addCategories(CategoryDTO categoryDTO);

    BaseRespDTO deleteCategory(Category category);

    BaseRespDTO updateCategory(Category category);

    CategoryRespDTO getCategories();

}
