package com.ashram.dto;

public class CategoryDto {
    private Long id;
    private String name;
    private String description;
    private long mediaCount;

    public CategoryDto() {}

    public CategoryDto(Long id, String name, String description, long mediaCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.mediaCount = mediaCount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public long getMediaCount() { return mediaCount; }
    public void setMediaCount(long mediaCount) { this.mediaCount = mediaCount; }
}
