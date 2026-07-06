package com.kfd.api.kfd_backend.cms.tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagDto {
    private UUID id;
    private String name;
    private String slug;
    @com.fasterxml.jackson.annotation.JsonProperty("created_at")
    private java.time.OffsetDateTime createdAt;
}
