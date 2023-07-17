package com.kuit.conet.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.kuit.conet.dto.response.StorageImgResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    @Autowired
    private AmazonS3Client amazonS3Client;

    public StorageImgResponse uploadProfileImage(MultipartFile file, Long userId) throws IOException {
        String fileName = userId + "-profileImage-" + LocalDateTime.now();
        fileName = fileName.replace(" ", "-").replace(":", "-").replace(".", "-");
        log.info("stored file name: {}", fileName);

        return uploadToS3(file, fileName);
    }

    public StorageImgResponse uploadTeamImage(MultipartFile file, Long teamId) throws IOException {
        String fileName = teamId + "-teamImage-" + LocalDateTime.now();
        fileName = fileName.replace(" ", "-").replace(":", "-").replace(".", "-");
        log.info("stored file name: {}", fileName);

        return uploadToS3(file, fileName);
    }

    private StorageImgResponse uploadToS3(MultipartFile file, String fileName) throws IOException {
        long size = file.getSize(); // 파일 크기
        ObjectMetadata objectMetaData = new ObjectMetadata();
        objectMetaData.setContentType(file.getContentType());
        objectMetaData.setContentLength(size);

        // S3에 업로드
        amazonS3Client.putObject(
                new PutObjectRequest(bucketName, fileName, file.getInputStream(), objectMetaData)
                        .withCannedAcl(CannedAccessControlList.PublicRead)
        );

        // URL 가져오기
        String imgUrl = amazonS3Client.getUrl(bucketName, fileName).toString();
        log.info("AWS S3에 저장된 이미지 파일의 url: {}", imgUrl);

        return new StorageImgResponse(fileName, imgUrl);
    }
}