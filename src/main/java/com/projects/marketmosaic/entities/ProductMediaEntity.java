package com.projects.marketmosaic.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "marketmosaic_product_media")
public class ProductMediaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String url; // or path to image storage

	private String altText;

	private String type;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id")
	private ProductEntity product;

}
