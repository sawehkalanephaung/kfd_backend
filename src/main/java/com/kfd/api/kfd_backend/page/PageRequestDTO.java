package com.kfd.api.kfd_backend.page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageRequestDTO {
    private String slug;
    private String title;
    private String content;
    private UUID heroImageId;
    private List<UUID> sliderImageIds;
    private String status;
}
