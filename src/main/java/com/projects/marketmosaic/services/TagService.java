package com.projects.marketmosaic.services;

import com.projects.marketmosaic.common.dto.resp.BaseRespDTO;
import com.projects.marketmosaic.dtos.Tag;
import com.projects.marketmosaic.dtos.TagDTO;

public interface TagService {
    BaseRespDTO addTags(TagDTO tagDTO);

    BaseRespDTO deleteTag(Tag tag);

    BaseRespDTO updateTag(Tag tag);

    TagDTO getTags();
}
