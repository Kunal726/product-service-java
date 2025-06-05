package com.projects.marketmosaic.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryDataDTO {
    private Long id;
    private String categoryName;
    private List<CategoryDataDTO> subCategories;
}
