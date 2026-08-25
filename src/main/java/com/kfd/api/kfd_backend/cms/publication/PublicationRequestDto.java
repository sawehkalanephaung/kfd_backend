package com.kfd.api.kfd_backend.cms.publication;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Incoming request body for POST / and PUT /{id}.
 * The frontend sends category/department as IDs; the service resolves them to entities.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicationRequestDto {

    @NotBlank(message = "Title is required")
    private String title;

    private String summary;
    private UUID categoryId;

    @NotNull(message = "Published date is required")
    private LocalDate publishedDate;

    private String issuedBy;
    private UUID departmentId;
    private String language;
    private String referenceNo;

    @NotNull(message = "A document is required")
    private UUID documentId;

    private UUID thumbnailId;

    @NotBlank(message = "Slug is required")
    private String slug;

    private PublicationStatus status;
}
