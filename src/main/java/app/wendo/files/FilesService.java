package app.wendo.files;

import app.wendo.users.models.ImageType;
import app.wendo.users.models.User;
import app.wendo.users.models.UserImage;
import app.wendo.users.repositories.UserImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FilesService {
    private final UserImageRepository imageRepository;
    @Value("${app.base-url}")
    private String baseUrl;
    @Value("${app.upload-dir}")
    private String uploadDir;

    public UserImage uploadImage(MultipartFile file, ImageType type, User user) {
        try {
            String fileName = generateFileName(file.getOriginalFilename());
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            String fileUrl = baseUrl + "/uploads/" + fileName;

            UserImage image = UserImage.builder()
                    .imageType(type)
                    .fileName(fileName)
                    .filePath(filePath.toString())
                    .imageUrl(fileUrl)
                    .contentType(file.getContentType())
                    .fileSize(file.getSize())
                    .user(user)
                    .build();

            return imageRepository.save(image);
        } catch (IOException e) {
            throw new RuntimeException("Could not store file", e);
        }
    }

    private String generateFileName(String originalFilename) {
        return UUID.randomUUID() + extractFileExtension(originalFilename);
    }


    private String extractFileExtension(String originalFilename) {
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }
        return fileExtension;
    }
}