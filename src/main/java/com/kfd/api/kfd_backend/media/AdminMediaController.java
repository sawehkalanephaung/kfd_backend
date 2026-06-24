package com.kfd.api.kfd_backend.media;

import com.kfd.api.kfd_backend.global.exception.ApiDataResponse;
import com.kfd.api.kfd_backend.global.exception.ApiMessageResponse;
import com.kfd.api.kfd_backend.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/media")
@RequiredArgsConstructor
public class AdminMediaController {

        private final MediaAssetRepository mediaAssetRepository;
        private final StorageService storageService;

        private MediaResponseDTO toDto(MediaAsset asset) {
                return MediaResponseDTO.builder()
                                .id(asset.getId())
                                .fileName(asset.getFileName())
                                .fileUrl(asset.getFileUrl())
                                .fileType(asset.getFileType())
                                .fileSizeKb(asset.getFileSizeKb())
                                .mediaCategory(asset.getMediaCategory())
                                .language(asset.getLanguage())
                                .departmentId(asset.getDepartmentId())
                                .uploadedBy(asset.getUploadedBy())
                                .createdAt(asset.getCreatedAt())
                                .build();
        }

        @PostMapping("/upload")
        public ResponseEntity<ApiDataResponse<MediaResponseDTO>> uploadFile(
                        @RequestParam("file") MultipartFile file,
                        @RequestParam(value = "category", required = false, defaultValue = "general") String category,
                        @RequestParam(value = "departmentId", required = false) UUID departmentId) {

                // 1. Store the file physically
                String fileUrl = storageService.upload(file);

                // 2. Save metadata in DB
                MediaAsset asset = MediaAsset.builder()
                                .fileName(file.getOriginalFilename())
                                .fileUrl(fileUrl)
                                .fileType(file.getContentType())
                                .fileSizeKb((int) (file.getSize() / 1024))
                                .mediaCategory(category)
                                .language("English") // Default language or take from param
                                .departmentId(departmentId)
                                // uploadedBy is now populated automatically via JPA Auditing +
                                // SecurityContextHolder
                                .build();

                MediaAsset savedAsset = mediaAssetRepository.save(asset);

                // 3. Return DTO
                MediaResponseDTO dto = toDto(savedAsset);
                return ResponseEntity.status(HttpStatus.CREATED).body(
                                new ApiDataResponse<>(
                                                HttpStatus.CREATED.value(),
                                                "File uploaded successfully",
                                                dto));
        }

        @GetMapping
        public ResponseEntity<Page<MediaResponseDTO>> getAllMedia(Pageable pageable) {
                Page<MediaResponseDTO> page = mediaAssetRepository.findAll(pageable).map(this::toDto);
                return ResponseEntity.ok(page);
        }

        @GetMapping("/{id}")
        public ResponseEntity<ApiDataResponse<MediaResponseDTO>> getMediaById(@PathVariable UUID id) {
                MediaAsset asset = mediaAssetRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("MediaAsset", "id", id));
                return ResponseEntity.ok(
                                new ApiDataResponse<>(
                                                HttpStatus.OK.value(),
                                                "Media retrieved successfully",
                                                toDto(asset)));
        }

        @PutMapping("/{id}")
        public ResponseEntity<ApiDataResponse<MediaResponseDTO>> updateMedia(
                        @PathVariable UUID id,
                        @RequestParam(value = "category", required = false) String category,
                        @RequestParam(value = "departmentId", required = false) UUID departmentId,
                        @RequestParam(value = "file", required = false) MultipartFile file) {
                MediaAsset asset = mediaAssetRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("MediaAsset", "id", id));

                if (category != null)
                        asset.setMediaCategory(category);
                if (departmentId != null)
                        asset.setDepartmentId(departmentId);
                        
                if (file != null && !file.isEmpty()) {
                        // Delete old file
                        if (asset.getFileUrl() != null) {
                                storageService.delete(asset.getFileUrl());
                        }
                        
                        // Upload new file
                        String newFileUrl = storageService.upload(file);
                        
                        // Update asset metadata
                        asset.setFileName(file.getOriginalFilename());
                        asset.setFileUrl(newFileUrl);
                        asset.setFileType(file.getContentType());
                        asset.setFileSizeKb((int) (file.getSize() / 1024));
                }

                MediaAsset updatedAsset = mediaAssetRepository.save(asset);
                return ResponseEntity.ok(
                                new ApiDataResponse<>(
                                                HttpStatus.OK.value(),
                                                "Media updated successfully",
                                                toDto(updatedAsset)));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<ApiMessageResponse> deleteMedia(@PathVariable UUID id) {
                MediaAsset asset = mediaAssetRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("MediaAsset", "id", id));

                // 1. Delete the DB record first (ensures no FK constraints are violated before losing the file)
                mediaAssetRepository.delete(asset);

                // 2. Delete the physical file
                if (asset.getFileUrl() != null) {
                        try {
                                storageService.delete(asset.getFileUrl());
                        } catch (Exception e) {
                                // Log error but don't fail the request since DB record is already deleted
                                e.printStackTrace();
                        }
                }

                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                                new ApiMessageResponse(HttpStatus.NO_CONTENT.value(), "File deleted successfully"));
        }
}
