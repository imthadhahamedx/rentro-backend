package com.rentro.service.impl;

import com.rentro.config.S3Properties;
import com.rentro.exception.ValidationException;
import com.rentro.service.S3StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3StorageServiceImpl implements S3StorageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/webp"
    );

    private final S3Client s3Client;
    private final S3Properties s3Properties;

    @Override
    public UploadedFile upload(MultipartFile file, String keyPrefix) {
        validate(file);

        String extension = extractExtension(file.getOriginalFilename());
        String key = "%s/%s%s".formatted(keyPrefix, UUID.randomUUID(), extension);

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(s3Properties.getBucketName())
                    .key(key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException e) {
            log.error("Failed to read uploaded file '{}' for S3 upload", file.getOriginalFilename(), e);
            throw new ValidationException("Could not read uploaded file: " + file.getOriginalFilename());
        } catch (S3Exception e) {
            log.error("S3 upload failed for key '{}'", key, e);
            throw new ValidationException("Failed to upload image to storage. Please try again.");
        }

        return new UploadedFile(
                key,
                buildPublicUrl(key),
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize()
        );
    }

    @Override
    public void delete(String key) {
        if (key == null || key.isBlank()) {
            return;
        }
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(s3Properties.getBucketName())
                    .key(key)
                    .build());
        } catch (S3Exception e) {
            // Deletion failures shouldn't block the caller (e.g. vehicle delete) - just log it.
            log.warn("Failed to delete S3 object with key '{}'", key, e);
        }
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ValidationException("Uploaded image file is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new ValidationException(
                    "Unsupported image type '%s'. Allowed types: %s".formatted(contentType, ALLOWED_CONTENT_TYPES)
            );
        }

        long maxBytes = s3Properties.getMaxFileSizeMb() * 1024 * 1024;
        if (file.getSize() > maxBytes) {
            throw new ValidationException(
                    "Image '%s' exceeds the maximum allowed size of %dMB".formatted(
                            file.getOriginalFilename(), s3Properties.getMaxFileSizeMb())
            );
        }
    }

    private String extractExtension(String originalFileName) {
        if (originalFileName == null || !originalFileName.contains(".")) {
            return "";
        }
        return originalFileName.substring(originalFileName.lastIndexOf('.')).toLowerCase();
    }

    private String buildPublicUrl(String key) {
        String base = s3Properties.getPublicUrlBase();
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return base + "/" + key;
    }

    // Kept for potential future use (e.g. bulk cleanup on vehicle delete).
    void deleteAll(List<String> keys) {
        keys.forEach(this::delete);
    }
}
