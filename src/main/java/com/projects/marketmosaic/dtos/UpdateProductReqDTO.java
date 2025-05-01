package com.projects.marketmosaic.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class UpdateProductReqDTO {

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
    private Double price;

    @Min(value = 0, message = "Stock must be greater than or equal to 0")
    private Integer stock;

    @Size(max = 5, message = "Rating should be at most 5 characters")
    private String rating;  // This could be a rating code, like "excellent"
}

