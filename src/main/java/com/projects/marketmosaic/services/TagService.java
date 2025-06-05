package com.projects.marketmosaic.services;

import com.projects.marketmosaic.common.dto.resp.BaseRespDTO;
import com.projects.marketmosaic.dtos.TagDTO;

public interface TagService {
    BaseRespDTO addTags(TagDTO tagDTO);

    BaseRespDTO deleteTag(TagDTO.Tag tag);

    BaseRespDTO updateTag(TagDTO.Tag tag);

    TagDTO getTags();
}
