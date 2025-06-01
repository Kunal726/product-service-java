package com.projects.marketmosaic.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateProductBulkReqDTO {

	@Data
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class UpdateProductData {

		private String productId;

		private UpdateProductReqDTO product;

	}

	private List<UpdateProductData> products;

}