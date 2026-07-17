package com.ashram.service;

import com.ashram.dto.CategoryDto;
import com.ashram.dto.MediaItemDto;
import com.ashram.dto.PagedResponseDto;
import com.ashram.entity.Category;
import com.ashram.entity.MediaItem;
import com.ashram.entity.MediaItemType;
import com.ashram.exception.ResourceNotFoundException;
import com.ashram.repository.CategoryRepository;
import com.ashram.repository.MediaItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class MediaService {

    private final MediaItemRepository mediaItemRepository;
    private final CategoryRepository categoryRepository;

    public MediaService(MediaItemRepository mediaItemRepository, CategoryRepository categoryRepository) {
        this.mediaItemRepository = mediaItemRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(cat -> new CategoryDto(
                        cat.getId(),
                        cat.getName(),
                        cat.getDescription(),
                        mediaItemRepository.countByCategoryId(cat.getId())
                ))
                .collect(Collectors.toList());
    }

    public PagedResponseDto<MediaItemDto> getPublicPreview(MediaItemType type, Long categoryId) {
        // Public preview: limit to 6 items if PHOTO, 2 items if VIDEO
        int previewLimit = (type == MediaItemType.VIDEO) ? 2 : 6;
        Pageable pageable = PageRequest.of(0, previewLimit);
        Page<MediaItem> page;

        if (categoryId != null && categoryId > 0) {
            if (type != null) {
                page = mediaItemRepository.findByCategoryIdAndTypeOrderByIdDesc(categoryId, type, pageable);
            } else {
                page = mediaItemRepository.findByCategoryIdOrderByIdDesc(categoryId, pageable);
            }
        } else {
            if (type != null) {
                page = mediaItemRepository.findByTypeOrderByIdDesc(type, pageable);
            } else {
                page = mediaItemRepository.findAllByOrderByIdDesc(pageable);
            }
        }

        List<MediaItemDto> content = page.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        // Get total count across entire repo to show cutoff indicator accurately
        long totalElements;
        if (categoryId != null && categoryId > 0) {
            totalElements = (type != null) ? mediaItemRepository.countByCategoryIdAndType(categoryId, type) : mediaItemRepository.countByCategoryId(categoryId);
        } else {
            totalElements = mediaItemRepository.count();
        }

        return new PagedResponseDto<>(content, 0, previewLimit, totalElements, (int) Math.ceil((double) totalElements / previewLimit), content.size() >= totalElements);
    }

    public PagedResponseDto<MediaItemDto> getFullMedia(MediaItemType type, Long categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MediaItem> resultPage;

        if (categoryId != null && categoryId > 0) {
            if (type != null) {
                resultPage = mediaItemRepository.findByCategoryIdAndTypeOrderByIdDesc(categoryId, type, pageable);
            } else {
                resultPage = mediaItemRepository.findByCategoryIdOrderByIdDesc(categoryId, pageable);
            }
        } else {
            if (type != null) {
                resultPage = mediaItemRepository.findByTypeOrderByIdDesc(type, pageable);
            } else {
                resultPage = mediaItemRepository.findAllByOrderByIdDesc(pageable);
            }
        }

        List<MediaItemDto> content = resultPage.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return new PagedResponseDto<>(
                content,
                resultPage.getNumber(),
                resultPage.getSize(),
                resultPage.getTotalElements(),
                resultPage.getTotalPages(),
                resultPage.isLast()
        );
    }

    public MediaItemDto getMediaById(Long id) {
        MediaItem item = mediaItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Media item not found with ID: " + id));
        return mapToDto(item);
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
