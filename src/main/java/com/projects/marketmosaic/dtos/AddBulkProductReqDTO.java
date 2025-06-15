package com.projects.marketmosaic.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.projects.marketmosaic.common.dto.product.resp.ProductDetailsDTO;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddBulkProductReqDTO {

	List<ProductDetailsDTO> products;

}
