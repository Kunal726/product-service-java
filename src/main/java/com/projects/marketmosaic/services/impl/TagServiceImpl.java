package com.projects.marketmosaic.services.impl;

import com.projects.marketmosaic.common.dto.resp.BaseRespDTO;
import com.projects.marketmosaic.constants.Constants;
import com.projects.marketmosaic.dtos.TagDTO;
import com.projects.marketmosaic.entities.TagEntity;
import com.projects.marketmosaic.exception.ProductException;
import com.projects.marketmosaic.repositories.TagRepository;
import com.projects.marketmosaic.services.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public BaseRespDTO addTags(TagDTO tagDTO) {
        BaseRespDTO respDTO = new BaseRespDTO();

        try {
            if(tagDTO != null && tagDTO.getTags() != null) {
                List<TagEntity> tagEntityList = new ArrayList<>();

                tagDTO.getTags().forEach(tag -> {
                    TagEntity tagEntity = new TagEntity();
                    tagEntity.setTagName(tag.getName());
                    tagEntityList.add(tagEntity);
                });

                if(!tagEntityList.isEmpty()) {
                    tagRepository.saveAll(tagEntityList);
                }

                respDTO.setStatus(true);
                respDTO.setMessage("Tags Added Successfully");
                respDTO.setCode("200");
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e.getMessage());
            throw new ProductException(e.getCause(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return respDTO;
    }

    @Override
    public BaseRespDTO deleteTag(TagDTO.Tag tag) {
        BaseRespDTO respDTO = new BaseRespDTO();

        try {
            if(tag != null && StringUtils.isNotBlank(tag.getId())) {
                tagRepository.deleteById(Long.valueOf(tag.getId()));

                respDTO.setMessage("Tag Deleted Successfully");
                respDTO.setStatus(true);
                respDTO.setCode("200");
            }

        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e.getMessage());
            throw new ProductException(e.getCause(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return respDTO;
    }

    @Override
    public BaseRespDTO updateTag(TagDTO.Tag tag) {
        BaseRespDTO respDTO = new BaseRespDTO();

        try {
            if(tag != null && StringUtils.isNotBlank(tag.getId())) {

                TagEntity tagEntity = tagRepository.findById(Long.valueOf(tag.getId()))
                                .orElseThrow(() -> new ProductException("Tag Not Found", HttpStatus.NOT_FOUND.value()));

                if(StringUtils.isNotBlank(tagEntity.getTagName())) {
                    tagEntity.setTagName(tagEntity.getTagName());

                    tagRepository.save(tagEntity);

                    respDTO.setMessage("Tag Updated Successfully");
                    respDTO.setStatus(true);
                    respDTO.setCode("200");
                }
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e.getMessage());
            throw new ProductException(e.getCause(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return respDTO;
    }

    @Override
    public TagDTO getTags() {
        TagDTO tags = new TagDTO();

        try {
            List<TagEntity> tagsList = tagRepository.findAll();

            if(!tagsList.isEmpty()) {
                List<TagDTO.Tag> tagList =tagsList.stream()
                                .map(tagEntity -> {
                                    TagDTO.Tag tag = new TagDTO.Tag();
                                    tag.setId(String.valueOf(tagEntity.getTagId()));
                                    tag.setName(tagEntity.getTagName());
                                    return tag;
                                }).toList();

                tags.setTags(tagList);
                tags.setMessage("Tags Fetched Successfully");
            } else {
                tags.setMessage("Tags Not Available");
            }

            tags.setStatus(true);
            tags.setCode(HttpStatus.OK.value());

        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e.getMessage());
            throw new ProductException(e.getCause(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return tags;
    }
}
