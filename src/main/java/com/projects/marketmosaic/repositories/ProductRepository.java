package com.projects.marketmosaic.repositories;

import com.projects.marketmosaic.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface ProductRepository extends JpaRepository<ProductEntity, Long>, CustomProductRepository {

	@Modifying
	@Query("UPDATE ProductEntity p SET p.isActive = false WHERE p.productId = :productId")
	void softDelete(String productId);

	@Query("SELECT DISTINCT p.productName FROM ProductEntity p WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :query, '%'))")
	List<String> getProductSuggestions(String query);

}
