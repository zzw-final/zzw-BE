package com.zzw.zzw_final.Service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploaderService {

    private final UploadService uploadService;

    public String uploadImage(MultipartFile file){
        String filename = createFileName(file.getOriginalFilename());
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        try(InputStream inputStream = file.getInputStream()){
            uploadService.uploadFile(inputStream, objectMetadata, filename);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return uploadService.getFileUrl(filename);
    }


    private String createFileName(String fileName){
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    private String getFileExtension(String filename){
        try {
            return filename.substring(filename.lastIndexOf("."));
        }catch (StringIndexOutOfBoundsException e){
            throw new IllegalArgumentException(String.format("잘못된 형식의 파일 (%s) 입니다", filename));
        }
    }

}