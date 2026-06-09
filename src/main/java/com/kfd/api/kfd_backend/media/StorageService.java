package com.kfd.api.kfd_backend.media;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    /**
     * Stores a file and returns its access URL or path.
     *
     * @param file the file to upload
     * @return the unique URL or path where the file can be accessed
     */
    String upload(MultipartFile file);

    /**
     * Deletes a file from storage given its URL or path.
     *
     * @param fileUrl the URL or path of the file to delete
     */
    void delete(String fileUrl);
}
