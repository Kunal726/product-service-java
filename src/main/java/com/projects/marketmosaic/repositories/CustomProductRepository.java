package com.projects.marketmosaic.repositories;

import com.projects.marketmosaic.dtos.ProductFilterDTO;
import com.projects.marketmosaic.entities.ProductEntity;

import java.util.List;

public interface CustomProductRepository {

	List<ProductEntity> findByFilters(ProductFilterDTO productFilterDTO, String role);

}
