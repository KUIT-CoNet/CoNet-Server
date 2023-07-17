package com.kuit.conet.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.kuit.conet.common.exception.BaseException;
import com.kuit.conet.common.exception.StorageException;
import com.kuit.conet.domain.StorageDomain;
import com.kuit.conet.dto.response.StorageImgResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.time.LocalDateTime;

import static com.kuit.conet.common.response.status.BaseExceptionResponseStatus.BAD_REQUEST;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    @Autowired
    private AmazonS3Client amazonS3Client;

    public String uploadImage(MultipartFile file, StorageDomain storage, Long id) {
        String fileName = id + "-" + storage.getStorage() + "Image-" + LocalDateTime.now();
        fileName = fileName.replace(" ", "-").replace(":", "-").replace(".", "-") + ".png";
        log.info("stored file name: {}", fileName);

        return uploadToS3(file, fileName);
    }

    private String uploadToS3(MultipartFile file, String fileName) {
        long size = file.getSize(); // 파일 크기
        ObjectMetadata objectMetaData = new ObjectMetadata();
        objectMetaData.setContentType(file.getContentType());
        objectMetaData.setContentLength(size);

        try {
            // S3에 업로드
            amazonS3Client.putObject(
                    new PutObjectRequest(bucketName, fileName, file.getInputStream(), objectMetaData)
                            .withCannedAcl(CannedAccessControlList.PublicRead)
            );
        } catch (IOException e) {
            throw new BaseException(BAD_REQUEST, e.getMessage());
        }

        // URL 가져오기
        String imgUrl = amazonS3Client.getUrl(bucketName, fileName).toString();
        log.info("AWS S3에 저장된 이미지 파일의 url: {}", imgUrl);

        return imgUrl;
    }

    public void deleteImage(String fileName) {
        try {
            amazonS3Client.deleteObject(bucketName, fileName);
        } catch (AmazonServiceException e) {
            throw new StorageException(BAD_REQUEST, e.getMessage());
        }
    }
}