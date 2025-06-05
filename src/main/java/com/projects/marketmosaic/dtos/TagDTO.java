package com.projects.marketmosaic.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class TagDTO extends BaseRespDTO {

    private List<Tag> tags;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Tag {
        private String id;
        private String name;
    }
}
