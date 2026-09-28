package vn.iotstar.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.iotstar.dto.*;
import vn.iotstar.entity.*;
import vn.iotstar.mapper.ProductMapper;
import vn.iotstar.repository.*;
import vn.iotstar.service.*;

@Service @RequiredArgsConstructor @Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {
    private final ProductRepository products; private final UserRepository users; private final ProductMapper mapper; private final CloudinaryService cloudinary;
    @Override public Page<ProductDTO> findAll(String keyword, int page, int size) {
        Pageable pageable=PageRequest.of(Math.max(page,0), Math.min(Math.max(size,1),100), Sort.by("id").descending());
        return products.search(keyword==null ? "" : keyword.trim(), pageable).map(mapper::toDto);
    }
    @Override public ProductDTO findById(Long id) { return mapper.toDto(get(id)); }
    @Override @Transactional public ProductDTO create(ProductDTO dto, MultipartFile image) {
        Product p=mapper.toEntity(dto); p.setUser(user(dto.getUserId())); setImage(p,image); return mapper.toDto(products.save(p));
    }
    @Override @Transactional public ProductDTO update(Long id, ProductDTO dto, MultipartFile image) {
        Product p=get(id); p.setName(dto.getName()); p.setDescription(dto.getDescription()); p.setPrice(dto.getPrice()); p.setUser(user(dto.getUserId())); setImage(p,image); return mapper.toDto(p);
    }
    @Override @Transactional public void delete(Long id) { Product p=get(id); deleteImage(p.getImageUrl()); products.delete(p); }
    @Override public long count() { return products.count(); }
    private Product get(Long id) { return products.findById(id).orElseThrow(() -> new IllegalArgumentException("Product không tồn tại")); }
    private User user(Long id) { return users.findById(id).orElseThrow(() -> new IllegalArgumentException("User không tồn tại")); }
    private void setImage(Product p, MultipartFile image) {
        if (image==null || image.isEmpty()) return;
        CloudinaryUploadResult result=cloudinary.upload(image); deleteImage(p.getImageUrl()); p.setImageUrl(result.url()+"|"+result.publicId());
    }
    private void deleteImage(String stored) { if (stored!=null && stored.contains("|")) cloudinary.delete(stored.substring(stored.indexOf('|')+1)); }
}
