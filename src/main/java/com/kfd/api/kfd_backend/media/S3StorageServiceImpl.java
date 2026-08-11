package com.kfd.api.kfd_backend.media;

import com.kfd.api.kfd_backend.global.exception.StorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

/**
 * Amazon S3 storage implementation — active ONLY in the 'prod' profile.
 * In local dev, LocalStorageServiceImpl is used instead.
 *
 * Files are stored in the configured S3 bucket and served via the public S3 URL.
 * The bucket must be configured with a public-read bucket policy for media serving.
 */
@Profile("prod")
@Service
public class S3StorageServiceImpl implements StorageService {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    public S3StorageServiceImpl(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    /**
     * Uploads a file to S3 and returns its public HTTPS URL.
     *
     * @param file the multipart file to upload
     * @return full public URL: https://<bucket>.s3.<region>.amazonaws.com/<uuid>.<ext>
     */
    @Override
    public String upload(MultipartFile file) {
        if (file.isEmpty()) {
            throw new StorageException("Failed to store empty file.");
        }

        String originalFilename = StringUtils.cleanPath(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown_file"
        );

        // Extract extension
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex).toLowerCase();
        }

        // Generate a unique S3 key
        String s3Key = "uploads/" + UUID.randomUUID() + extension;

        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromBytes(file.getBytes()));
        } catch (IOException e) {
            throw new StorageException("Failed to upload file to S3.", e);
        }

        // Return the public S3 URL
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, s3Key);
    }

    /**
     * Deletes an object from S3 given its full URL.
     *
     * @param fileUrl the full S3 URL of the file to delete
     */
    @Override
    public void delete(String fileUrl) {
        // Extract the S3 key from the full URL
        // URL format: https://<bucket>.s3.<region>.amazonaws.com/<key>
        String prefix = String.format("https://%s.s3.%s.amazonaws.com/", bucketName, region);

        String s3Key;
        if (fileUrl.startsWith(prefix)) {
            s3Key = fileUrl.substring(prefix.length());
        } else if (fileUrl.startsWith("/uploads/")) {
            // Graceful fallback for any old local-style URLs still in DB
            s3Key = "uploads/" + fileUrl.substring("/uploads/".length());
        } else {
            // Unknown format — skip deletion to avoid errors
            return;
        }

        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();
            s3Client.deleteObject(deleteRequest);
        } catch (Exception e) {
            throw new StorageException("Failed to delete file from S3: " + s3Key, e);
        }
    }
}
