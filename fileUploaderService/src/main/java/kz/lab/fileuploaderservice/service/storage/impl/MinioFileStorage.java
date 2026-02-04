package kz.lab.fileuploaderservice.service.storage.impl;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.annotation.PostConstruct;
import kz.lab.fileuploaderservice.service.storage.FileStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MinioFileStorage implements FileStorage {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @PostConstruct
    public void initBucket() throws Exception {
        boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder()
                        .bucket(bucket)
                        .build()
        );

        if (!exists) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucket)
                            .build()
            );
        }
    }

    @Override
    public String store(byte[] content, String filename) throws Exception {
        String objectName =
                UUID.randomUUID() + "_" + filename;

        try (InputStream is = new ByteArrayInputStream(content)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .stream(is, content.length, -1)
                            .contentType("application/octet-stream")
                            .build()
            );
        }

        return objectName;
    }
}

