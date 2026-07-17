package com.ashram.dto;

import com.ashram.entity.MediaItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MediaUploadRequestDto {
    @NotNull(message = "Media type (PHOTO or VIDEO) is required")
    private MediaItemType type;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotBlank(message = "Cloudinary URL or YouTube video ID/embed URL is required")
    private String url;

    private String thumbnailUrl;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    public MediaUploadRequestDto() {}

    public MediaUploadRequestDto(MediaItemType type, String title, String description, String url, String thumbnailUrl, Long categoryId) {
        this.type = type;
        this.title = title;
        this.description = description;
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
        this.categoryId = categoryId;
    }

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
}
