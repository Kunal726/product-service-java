package com.projects.marketmosaic.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductFilterDTO {

	private Long supplierId; // Optional filter for Supplier ID

	private Long categoryId; // Optional filter for Category

	private BigDecimal priceMin; // Optional filter for minimum price

	private BigDecimal priceMax; // Optional filter for maximum price

	private String rating; // Optional filter for Rating

	private String searchTerm; // Optional filter for Search

	private List<Long> tags;

}
