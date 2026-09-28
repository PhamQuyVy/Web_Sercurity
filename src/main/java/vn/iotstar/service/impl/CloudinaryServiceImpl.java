package vn.iotstar.service.impl;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.iotstar.dto.CloudinaryUploadResult;
import vn.iotstar.service.CloudinaryService;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    @Override
    public CloudinaryUploadResult upload(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Chỉ cho phép file hình ảnh");
        }

        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    Map.of("folder", "security-shop/products",
                           "resource_type", "image"));

            return new CloudinaryUploadResult(
                    String.valueOf(result.get("secure_url")),
                    String.valueOf(result.get("public_id")));

        } catch (IOException e) {
            log.error("Cloudinary upload lỗi", e);
            throw new IllegalStateException(
                    "Không thể upload ảnh lên Cloudinary: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String publicId) {
        if (publicId == null || publicId.isBlank()) return;

        try {
            cloudinary.uploader().destroy(publicId, Map.of("resource_type", "image"));
        } catch (IOException e) {
            log.error("Cloudinary xóa ảnh lỗi", e);
            throw new IllegalStateException(
                    "Không thể xóa ảnh Cloudinary: " + e.getMessage(), e);
        }
    }
}
