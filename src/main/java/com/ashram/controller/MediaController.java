package com.ashram.controller;

import com.ashram.dto.CategoryDto;
import com.ashram.dto.MediaItemDto;
import com.ashram.dto.PagedResponseDto;
import com.ashram.entity.MediaItemType;
import com.ashram.service.MediaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        return ResponseEntity.ok(mediaService.getAllCategories());
    }

    @GetMapping("/media/preview")
    public ResponseEntity<PagedResponseDto<MediaItemDto>> getPublicPreview(
            @RequestParam(required = false) MediaItemType type,
            @RequestParam(required = false) Long categoryId) {
        return ResponseEntity.ok(mediaService.getPublicPreview(type, categoryId));
    }

    @GetMapping("/media")
    public ResponseEntity<PagedResponseDto<MediaItemDto>> getFullMedia(
            @RequestParam(required = false) MediaItemType type,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(mediaService.getFullMedia(type, categoryId, page, size));
    }

    @GetMapping("/media/category/{id}")
    public ResponseEntity<PagedResponseDto<MediaItemDto>> getMediaByCategory(
            @PathVariable Long id,
            @RequestParam(required = false) MediaItemType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(mediaService.getFullMedia(type, id, page, size));
    }

    @GetMapping("/media/{id}")
    public ResponseEntity<MediaItemDto> getMediaById(@PathVariable Long id) {
        return ResponseEntity.ok(mediaService.getMediaById(id));
    }
}
