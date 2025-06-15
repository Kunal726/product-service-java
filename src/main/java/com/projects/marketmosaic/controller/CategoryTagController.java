package com.projects.marketmosaic.controller;

import com.projects.marketmosaic.common.dto.resp.BaseRespDTO;
import com.projects.marketmosaic.dtos.*;
import com.projects.marketmosaic.services.CategoryService;
import com.projects.marketmosaic.services.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CategoryTagController {

	private final CategoryService categoryService;

	private final TagService tagService;

	@PostMapping(path = "/categories/add-category")
	ResponseEntity<BaseRespDTO> addCategory(@RequestBody CategoryDTO categoryDTO) {
		log.info("Add Categories ReqDTO {}", categoryDTO);
		BaseRespDTO response = categoryService.addCategories(categoryDTO);
		log.info("Add Categories RespDTO {}", response);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping(path = "/categories/delete-category")
	ResponseEntity<BaseRespDTO> deleteCategory(@RequestBody Category category) {
		log.info("Delete Category ReqDTO {}", category);
		BaseRespDTO response = categoryService.deleteCategory(category);
		log.info("Delete Category RespDTO {}", response);
		return ResponseEntity.ok(response);
	}

	@PatchMapping(path = "/categories/update-category")
	ResponseEntity<BaseRespDTO> updateCategory(@RequestBody Category category) {
		log.info("Update Category ReqDTO {}", category);
		BaseRespDTO response = categoryService.updateCategory(category);
		log.info("Update Category RespDTO {}", response);
		return ResponseEntity.ok(response);
	}

	@GetMapping(path = "/categories")
	ResponseEntity<CategoryRespDTO> getAllCategories() {
		CategoryRespDTO categories = categoryService.getCategories();
		log.debug("Category Resp DTO {}", categories);
		return ResponseEntity.ok(categories);
	}

	@PostMapping(path = "/tags/add-tags")
	ResponseEntity<BaseRespDTO> addtag(@RequestBody TagDTO tagDTO) {
		log.info("Add Tags ReqDTO {}", tagDTO);
		BaseRespDTO response = tagService.addTags(tagDTO);
		log.info("Add Tags RespDTO {}", response);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping(path = "/tags/delete-tag")
	ResponseEntity<BaseRespDTO> deleteTag(@RequestBody Tag tag) {
		log.info("Delete Tag ReqDTO {}", tag);
		BaseRespDTO response = tagService.deleteTag(tag);
		log.info("Delete Tag RespDTO {}", response);
		return ResponseEntity.ok(response);
	}

	@PatchMapping(path = "/tags/update-tag")
	ResponseEntity<BaseRespDTO> addTag(@RequestBody Tag tag) {
		log.info("Update Tag ReqDTO {}", tag);
		BaseRespDTO response = tagService.updateTag(tag);
		log.info("Update Tag RespDTO {}", response);
		return ResponseEntity.ok(response);
	}

	@GetMapping(path = "/tags")
	ResponseEntity<TagDTO> getAllTags() {
		TagDTO tagDTO = tagService.getTags();
		log.debug("Tag Resp DTO {}", tagDTO);
		return ResponseEntity.ok(tagDTO);
	}

}
