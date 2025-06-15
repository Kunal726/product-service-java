package com.projects.marketmosaic.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDetailsDTO {

	private Long productId;

	@NotBlank(message = "Product name cannot be blank")
	@Size(min = 3, max = 100, message = "Product name must be between 3 and 100 characters")
	private String productName;

	@Size(max = 500, message = "Description cannot exceed 500 characters")
	private String description;

	@NotNull(message = "Price cannot be null")
	@DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
	private BigDecimal price;

	@NotNull(message = "Category cannot be null")
	private IdNameDto category;

	@Min(value = 0, message = "Stock quantity must be greater than or equal to 0")
	private Integer stockQuantity;

	private String supplierName;

	private Boolean isActive;

	private List<IdNameDto> tags;

	private List<ProductMedia> productMedia;

	private String rating;

	@Data
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class ProductMedia {

		private String id;

		private String mediaKey;

		private String url;

		private String altText;

		private String type;

		private MultipartFile media;

	}

	@Data
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@AllArgsConstructor
	public static class IdNameDto {

		private Long id;

		private String name;

	}

}
