package com.utils.Arquivos;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.applicationautoscaling.model.ObjectNotFoundException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;



@Service
public class AmazonClient {

    private AmazonS3 s3client;

    @Value("${amazonProperties.endpointUrl}")
    private String endpointUrl;
    @Value("${amazonProperties.bucketName}")
    private String bucketName;
    @Value("${amazonProperties.accessKey}")
    private String accessKey;
    @Value("${amazonProperties.secretKey}")
    private String secretKey;

    @PostConstruct
    private void initializeAmazon() {
        System.out.println("accessKey: " + this.accessKey);
        System.out.println("secretKey: " + this.secretKey);
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        this.s3client = new AmazonS3Client(credentials);
    }

    public String uploadFile(MultipartFile multipartFile) {
        String fileUrl = "";

        Optional<File> file = null;
        try {
            file = Optional.ofNullable(convertMultiPartToFile(multipartFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!file.isPresent()) {
            return null;
        }
        String fileName = generateFileName();
        fileUrl = endpointUrl  + bucketName + "/" + fileName;
        uploadFileTos3bucket(fileName, file.get());
        file.get().delete();

        return fileUrl;
    }


    public String uploadShapping(File file, String name){
        String fileUrl = endpointUrl + "/" + bucketName + "/" + name;
        System.out.println(fileUrl);
        uploadFileTos3bucket(name, file);
        file.delete();
        return fileUrl;
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
    	File diretory = new File( System.getProperty( "catalina.base" ) ).getAbsoluteFile();
    	File convFile = new File(diretory.getCanonicalPath() + File.separator +file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        } catch (Exception ex) {
            throw new ObjectNotFoundException(diretory.getAbsolutePath()+"--"+convFile.getAbsolutePath());
        }


        return convFile;
    }

    private String generateFileName() {
        return new Date().getTime() + "-" + UUID.randomUUID().toString();
    }

    private void uploadFileTos3bucket(String fileName, File file) {
        s3client.putObject(new PutObjectRequest(bucketName, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }

    public String deleteFileFromS3Bucket(String fileUrl) {
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        s3client.deleteObject(new DeleteObjectRequest(bucketName, fileName));
        return "Successfully deleted";
    }

}
