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

        // Dummy user ID for now until Spring Security is wired
        private static final UUID MOCK_ADMIN_ID = UUID.fromString("d4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f80");

        private MediaResponseDTO toDto(MediaAsset asset) {
                return MediaResponseDTO.builder()
                                .id(asset.getId())
                                .fileName(asset.getFileName())
                                .fileUrl(asset.getFileUrl())
                                .fileType(asset.getFileType())
                                .fileSizeKb(asset.getFileSizeKb())
                                .mediaCategory(asset.getMediaCategory())
                                .uploadedBy(asset.getUploadedBy())
                                .createdAt(asset.getCreatedAt())
                                .build();
        }

        @PostMapping("/upload")
        public ResponseEntity<ApiDataResponse<MediaResponseDTO>> uploadFile(
                        @RequestParam("file") MultipartFile file,
                        @RequestParam(value = "category", required = false, defaultValue = "general") String category) {

                // 1. Store the file physically
                String fileUrl = storageService.upload(file);

                // 2. Save metadata in DB
                MediaAsset asset = MediaAsset.builder()
                                .fileName(file.getOriginalFilename())
                                .fileUrl(fileUrl)
                                .fileType(file.getContentType())
                                .fileSizeKb((int) (file.getSize() / 1024))
                                .mediaCategory(category)
                                .uploadedBy(MOCK_ADMIN_ID)
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

        @DeleteMapping("/{id}")
        public ResponseEntity<ApiMessageResponse> deleteMedia(@PathVariable UUID id) {
                MediaAsset asset = mediaAssetRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("MediaAsset", "id", id));

                // 1. Delete the physical file
                storageService.delete(asset.getFileUrl());

                // 2. Delete the DB record
                mediaAssetRepository.delete(asset);

                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                                new ApiMessageResponse(HttpStatus.NO_CONTENT.value(), "File deleted successfully"));
        }
}
