package com.ashram.dto;

import com.ashram.entity.MediaItemType;
import java.time.LocalDateTime;

public class MediaItemDto {
    private Long id;
    private MediaItemType type;
    private String title;
    private String description;
    private String url;
    private String thumbnailUrl;
    private Long categoryId;
    private String categoryName;
    private String uploadedByName;
    private LocalDateTime createdAt;

    public MediaItemDto() {}

    public MediaItemDto(Long id, MediaItemType type, String title, String description, String url, String thumbnailUrl, Long categoryId, String categoryName, String uploadedByName, LocalDateTime createdAt) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.description = description;
        this.url = url;
        this.thumbnailUrl = thumbnailUrl != null ? thumbnailUrl : url;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.uploadedByName = uploadedByName != null ? uploadedByName : "Sanctuary Sevak";
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public MediaItemType getType() { return type; }
    public void setType(MediaItemType type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getUploadedByName() { return uploadedByName; }
    public void setUploadedByName(String uploadedByName) { this.uploadedByName = uploadedByName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
