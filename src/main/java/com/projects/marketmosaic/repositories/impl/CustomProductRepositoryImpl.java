package com.projects.marketmosaic.repositories.impl;

import com.projects.marketmosaic.dtos.ProductFilterDTO;
import com.projects.marketmosaic.entities.CategoryEntity;
import com.projects.marketmosaic.entities.ProductEntity;
import com.projects.marketmosaic.entities.TagEntity;
import com.projects.marketmosaic.repositories.CategoryRepository;
import com.projects.marketmosaic.repositories.CustomProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Repository
@RequiredArgsConstructor
public class CustomProductRepositoryImpl implements CustomProductRepository {

	// Using Criteria Api
	private final EntityManager entityManager;

	private final CategoryRepository categoryRepository;

	@Override
	public List<ProductEntity> findByFilters(ProductFilterDTO filters, String role) {

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder(); // getting
																				// The
																				// builder
		CriteriaQuery<ProductEntity> criteriaQuery = criteriaBuilder.createQuery(ProductEntity.class); // creating
																										// query
		Root<ProductEntity> productEntityRoot = criteriaQuery.from(ProductEntity.class); // getting
																							// root
		List<Predicate> predicates = new ArrayList<>(); // List of the filters to add

		if (StringUtils.isNotBlank(filters.getSearchTerm())) {
			String searchTerm = "%" + filters.getSearchTerm().toLowerCase(Locale.ROOT) + "%";
			Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(productEntityRoot.get("productName")),
					searchTerm);
			Predicate descriptionPredicate = criteriaBuilder
					.like(criteriaBuilder.lower(productEntityRoot.get("description")), searchTerm);
			predicates.add(criteriaBuilder.or(namePredicate, descriptionPredicate));
		}

		if (filters.getSupplierId() != null) {
			predicates.add(criteriaBuilder.equal(productEntityRoot.get("supplier").get("id"), filters.getSupplierId()));
		}
		if (filters.getPriceMin() != null && filters.getPriceMax() != null) {
			predicates.add(criteriaBuilder.between(productEntityRoot.get("price"), filters.getPriceMin(),
					filters.getPriceMax()));
		}
		else if (filters.getPriceMin() != null) {
			predicates.add(criteriaBuilder.ge(productEntityRoot.get("price"), filters.getPriceMin()));
		}
		else if (filters.getPriceMax() != null) {
			predicates.add(criteriaBuilder.le(productEntityRoot.get("price"), filters.getPriceMax()));
		}
		// Apply category filter if present
		if (filters.getCategoryId() != null) {
			// Fetch the category and its subcategories
			CategoryEntity category = categoryRepository.findById(filters.getCategoryId())
					.orElseThrow(() -> new RuntimeException("Category not found"));

			// Get all subcategories recursively (this can be done through a helper method
			// or query)
			List<CategoryEntity> allCategories = getAllSubcategories(category);

			// Add a predicate to filter by category and its subcategories
			List<Long> categoryIds = allCategories.stream().map(CategoryEntity::getCategoryId).toList();
			predicates.add(productEntityRoot.get("category").get("categoryId").in(categoryIds));
		}

		if (StringUtils.isNotBlank(filters.getRating())) {
			Predicate ratingPredicate = criteriaBuilder.equal(productEntityRoot.get("rating"), filters.getRating());
			predicates.add(ratingPredicate);
		}

		if (filters.getTags() != null && !filters.getTags().isEmpty()) {
			Join<ProductEntity, TagEntity> tagsJoin = productEntityRoot.join("tags");
			predicates.add(tagsJoin.get("tagId").in(filters.getTags()));
		}

		if ("USER".equalsIgnoreCase(role)) {
			predicates.add(criteriaBuilder.equal(productEntityRoot.get("isActive"), true));
		}

		if (predicates.isEmpty()) {
			return entityManager.createQuery(criteriaQuery).getResultList();
		}
		criteriaQuery.where(predicates.toArray(new Predicate[0]));

		return entityManager.createQuery(criteriaQuery).getResultList();
	}

	private List<CategoryEntity> getAllSubcategories(CategoryEntity category) {
		List<CategoryEntity> allCategories = new ArrayList<>();
		allCategories.add(category);

		if (category.getSubCategories() != null) {
			for (CategoryEntity subCategory : category.getSubCategories()) {
				allCategories.addAll(getAllSubcategories(subCategory)); // Recursively add
																		// subcategories
			}
		}

		return allCategories;
	}

}
