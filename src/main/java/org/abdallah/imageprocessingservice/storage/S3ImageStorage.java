package org.abdallah.imageprocessingservice.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

@Service
@RequiredArgsConstructor
public class S3ImageStorage implements ImageStorage {

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.region}")
    private String region;

    @Value("${aws.accessKey}")
    private String accessKey;

    @Value("${aws.secretKey}")
    private String secretKey;

    @Value("${aws.baseFolder}")
    private String baseFolder;

    private S3Client client;

    private S3Client getClient() {
        if (client == null) {
            client = S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(accessKey, secretKey)
                    ))
                    .build();
        }
        return client;
    }

    @Override
    public String save(byte[] data, String fileName) {
        String key = baseFolder + "/" + fileName;

        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentLength((long) data.length)
                .build();

        getClient().putObject(req, software.amazon.awssdk.core.sync.RequestBody.fromBytes(data));

        // Return the S3 key (or full URL)
        return key;
    }

    @Override
    public byte[] load(String fileName) throws Exception {

        GetObjectRequest req = GetObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .build();

        return getClient().getObjectAsBytes(req).asByteArray();
    }

    @Override
    public void delete(String fileName) throws Exception {
        String key = baseFolder + "/" + fileName;

        DeleteObjectRequest req = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        getClient().deleteObject(req);
    }
}
