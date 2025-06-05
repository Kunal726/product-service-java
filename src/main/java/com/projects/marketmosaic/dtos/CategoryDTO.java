package com.projects.marketmosaic.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryDTO {

    private List<Category> categories;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Category {
        private String id;
        private String categoryName;
        private String parentCategoryId;
    }
}

