package vn.iotstar.service;
import org.springframework.web.multipart.MultipartFile;
import vn.iotstar.dto.CloudinaryUploadResult;
public interface CloudinaryService {
    CloudinaryUploadResult upload(MultipartFile file);
    void delete(String publicId);
}
