package com.projects.marketmosaic.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "marketmosaic_category")
@Data
public class CategoryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "category_id")
	private Long categoryId;

	@Column(name = "category_name", nullable = false)
	private String categoryName;

	// Self-referencing relationship to support hierarchical categories
	@ManyToOne
	@JoinColumn(name = "parent_id")
	private CategoryEntity parentCategory;

	@OneToMany(mappedBy = "parentCategory")
	private List<CategoryEntity> subCategories;

}
