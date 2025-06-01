package com.projects.marketmosaic.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "marketmosaic_product")
@Data
public class ProductEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment for the primary
														// key
	@Column(name = "product_id")
	private Long productId;

	@Column(name = "product_name", nullable = false)
	private String productName;

	@Column(name = "description")
	private String description;

	@Column(name = "price", nullable = false)
	private BigDecimal price;

	@Column(name = "stock_quantity")
	private Integer stockQuantity;

	@ManyToOne
	@JoinColumn(name = "supplier_id") // Foreign key relation to the Supplier table
	private UserEntity supplier;

	@Column(name = "date_added", nullable = false, updatable = false)
	private LocalDateTime dateAdded;

	@Column(name = "is_active", nullable = false)
	private Boolean isActive;

	@Column(name = "rating")
	private String rating;

	@ManyToMany // creates intermediate table to map productid to tagid
	@JoinTable(name = "product_tags", joinColumns = @JoinColumn(name = "product_id"),
			inverseJoinColumns = @JoinColumn(name = "tag_id"))
	private List<TagEntity> tags;

	@ManyToOne
	@JoinColumn(name = "category_id") // Foreign key to Category
	private CategoryEntity category;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProductMediaEntity> productMedia = new ArrayList<>();

}
