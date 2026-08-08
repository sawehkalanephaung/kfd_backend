package com.kfd.api.kfd_backend.cms.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCategoryDto {
    private UUID id;
    private String name;
    private String slug;
    private String description;
    @com.fasterxml.jackson.annotation.JsonProperty("show_in_public")
    @Default
    private boolean showInPublic = true;
    @com.fasterxml.jackson.annotation.JsonProperty("created_at")
    private java.time.OffsetDateTime createdAt;
}
