package com.ashram.service;

import com.ashram.dto.CategoryDto;
import com.ashram.dto.MediaItemDto;
import com.ashram.dto.MediaUploadRequestDto;
import com.ashram.entity.Category;
import com.ashram.entity.MediaItem;
import com.ashram.entity.User;
import com.ashram.exception.ResourceNotFoundException;
import com.ashram.repository.CategoryRepository;
import com.ashram.repository.MediaItemRepository;
import com.ashram.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminService {

    private final MediaItemRepository mediaItemRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public AdminService(MediaItemRepository mediaItemRepository, CategoryRepository categoryRepository, UserRepository userRepository) {
        this.mediaItemRepository = mediaItemRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public MediaItemDto createMedia(MediaUploadRequestDto request, String uploaderEmail) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId()));

        User uploader = userRepository.findByEmail(uploaderEmail).orElse(null);

        MediaItem item = new MediaItem(
                request.getType(),
                request.getTitle(),
                request.getDescription(),
                request.getUrl(),
                request.getThumbnailUrl(),
                category,
                uploader
        );

        MediaItem saved = mediaItemRepository.save(item);
        return mapToDto(saved);
    }

    public MediaItemDto updateMedia(Long id, MediaUploadRequestDto request) {
        MediaItem item = mediaItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Media item not found with ID: " + id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId()));

        item.setType(request.getType());
        item.setTitle(request.getTitle());
        item.setDescription(request.getDescription());
        item.setUrl(request.getUrl());
        if (request.getThumbnailUrl() != null) {
            item.setThumbnailUrl(request.getThumbnailUrl());
        }
        item.setCategory(category);

        MediaItem updated = mediaItemRepository.save(item);
        return mapToDto(updated);
    }

    public void deleteMedia(Long id) {
        if (!mediaItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Media item not found with ID: " + id);
        }
        mediaItemRepository.deleteById(id);
    }

    public CategoryDto createCategory(String name, String description) {
        if (categoryRepository.existsByName(name)) {
            throw new IllegalArgumentException("Category with name '" + name + "' already exists.");
        }
        Category category = new Category(name, description);
        Category saved = categoryRepository.save(category);
        return new CategoryDto(saved.getId(), saved.getName(), saved.getDescription(), 0);
    }

    public CategoryDto updateCategory(Long id, String name, String description) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));

        category.setName(name);
        category.setDescription(description);
        Category updated = categoryRepository.save(category);
        return new CategoryDto(updated.getId(), updated.getName(), updated.getDescription(), mediaItemRepository.countByCategoryId(updated.getId()));
    }

    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with ID: " + id);
        }
        long mediaCount = mediaItemRepository.countByCategoryId(id);
        if (mediaCount > 0) {
            throw new IllegalStateException("Cannot delete category containing active media items. Please reassign or delete media first.");
        }
        categoryRepository.deleteById(id);
    }

    private MediaItemDto mapToDto(MediaItem item) {
        return new MediaItemDto(
                item.getId(),
                item.getType(),
                item.getTitle(),
                item.getDescription(),
                item.getUrl(),
                item.getThumbnailUrl(),
                item.getCategory() != null ? item.getCategory().getId() : null,
                item.getCategory() != null ? item.getCategory().getName() : "Uncategorized",
                item.getUploadedBy() != null ? item.getUploadedBy().getName() : "Sanctuary Sevak",
                item.getCreatedAt()
        );
    }
}
