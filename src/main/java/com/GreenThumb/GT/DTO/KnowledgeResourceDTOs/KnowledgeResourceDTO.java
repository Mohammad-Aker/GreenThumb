package com.GreenThumb.GT.DTO.KnowledgeResourceDTOs;


import com.GreenThumb.GT.models.KnowledgeResource.ResourceCategory;
import com.GreenThumb.GT.models.KnowledgeResource.ResourceType;

import java.util.Set;

public class KnowledgeResourceDTO {
    private String title;
    private String contentUrl;
    private String author;
    private ResourceType type;
    private ResourceCategory category;
    private Set<String> tags;

    // Constructors, getters, and setters

    public KnowledgeResourceDTO() {}

    public KnowledgeResourceDTO(String title, String contentUrl, String author, ResourceType type, ResourceCategory category, Set<String> tags) {
        this.title = title;
        this.contentUrl = contentUrl;
        this.author = author;
        this.type = type;
        this.category = category;
        this.tags = tags;
    }

    // Standard getters and setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public ResourceCategory getCategory() {
        return category;
    }

    public void setCategory(ResourceCategory category) {
        this.category = category;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }
}