package com.rentro.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Abstraction over AWS S3 for storing/deleting vehicle image files.
 */
public interface S3StorageService {

    /**
     * Uploads a file under the given key prefix (folder) and returns the
     * resulting object key and public URL.
     *
     * @param file       the multipart file coming from the request
     * @param keyPrefix  logical folder, e.g. "vehicles/{vehicleId}"
     * @return metadata describing where the file was stored
     */
    UploadedFile upload(MultipartFile file, String keyPrefix);

    /**
     * Deletes the object identified by its S3 key. Safe to call even if the
     * key no longer exists.
     */
    void delete(String key);

    record UploadedFile(String key, String url, String originalFileName, String contentType, long sizeBytes) {
    }
}
