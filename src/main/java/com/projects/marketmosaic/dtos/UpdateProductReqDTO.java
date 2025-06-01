package com.projects.marketmosaic.dtos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
public class UpdateProductReqDTO {

	private String productName;

	private String description;

	private BigDecimal price;

	private Integer stock;

	private String rating;

	private Boolean isActive;

	private Long categoryId;

	private List<Long> tagIds;

	private List<ProductMediaUpdateDTO> mediaUpdates;

	@Data
	public static class ProductMediaUpdateDTO {

		private String id; // Optional: existing media ID

		private MultipartFile file; // Optional: new file (null implies delete if ID is
									// present)

		private String type; // Optional: for metadata or validation ("image"/"video")

	}

}
