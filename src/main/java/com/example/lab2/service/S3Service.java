package com.example.lab2.service;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class S3Service {

    private final S3Client s3Client;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    // === Работа с бакетами ===

    public List<String> listBuckets() {
        return s3Client.listBuckets().buckets().stream()
                .map(Bucket::name)
                .collect(Collectors.toList());
    }

    public void createBucket(String bucketName) {
        try {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
        } catch (S3Exception e) {
            throw new RuntimeException("Ошибка создания бакета: " + e.getMessage(), e);
        }
    }

    public void deleteBucket(String bucketName) {
        try {
            // Удаляем все объекты из бакета перед удалением самого бакета
            listObjects(bucketName).forEach(key -> s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build()));
            s3Client.deleteBucket(DeleteBucketRequest.builder().bucket(bucketName).build());
        } catch (S3Exception e) {
            throw new RuntimeException("Ошибка удаления бакета: " + e.getMessage(), e);
        }
    }

    // === Работа с файлами ===

    public List<String> listObjects(String bucketName) {
        try {
            ListObjectsV2Response response = s3Client.listObjectsV2(ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .build());

            return response.contents().stream()
                    .map(S3Object::key)
                    .collect(Collectors.toList());
        } catch (S3Exception e) {
            throw new RuntimeException("Ошибка получения списка файлов: " + e.getMessage(), e);
        }
    }

    public void uploadFile(String bucketName, String key, MultipartFile file) {
        try {
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException | S3Exception e) {
            throw new RuntimeException("Ошибка загрузки файла: " + e.getMessage(), e);
        }
    }


    public void deleteFile(String bucketName, String key) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());
        } catch (S3Exception e) {
            throw new RuntimeException("Ошибка удаления файла: " + e.getMessage(), e);
        }
    }
}
