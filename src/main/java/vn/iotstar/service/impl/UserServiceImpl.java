package vn.iotstar.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.iotstar.dto.UserDTO;
import vn.iotstar.entity.*;
import vn.iotstar.mapper.UserMapper;
import vn.iotstar.repository.*;
import vn.iotstar.service.UserService;
import java.util.List;

@Service @RequiredArgsConstructor @Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository users; private final RoleRepository roles; private final ProductRepository products;
    private final UserMapper mapper; private final PasswordEncoder encoder;
    @Override public Page<UserDTO> findAll(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page,0), Math.min(Math.max(size,1),100), Sort.by(Sort.Direction.DESC, User.PROPERTY_ID));
        return users.search(keyword == null ? "" : keyword.trim(), pageable).map(this::toDto);
    }
    private UserDTO toDto(User u) { UserDTO dto=mapper.toDto(u); dto.setProductCount(products.countByUserId(u.getId())); return dto; }
    @Override public UserDTO findById(Long id) { return toDto(get(id)); }
    @Override @Transactional public UserDTO create(UserDTO dto) {
        validateUnique(null, dto); User u=mapper.toEntity(dto); u.setRole(role(dto.getRoleName()));
        u.setPassword(encoder.encode(dto.getPassword()==null || dto.getPassword().isBlank() ? "123456" : dto.getPassword()));
        return toDto(users.save(u));
    }
    @Override @Transactional public UserDTO update(Long id, UserDTO dto) {
        User u=get(id); validateUnique(id,dto); u.setUsername(dto.getUsername()); u.setEmail(dto.getEmail()); u.setFullName(dto.getFullName());
        u.setEnabled(dto.isEnabled()); u.setRole(role(dto.getRoleName())); u.setImageUrl(dto.getImageUrl());
        if (dto.getPassword()!=null && !dto.getPassword().isBlank()) u.setPassword(encoder.encode(dto.getPassword()));
        return toDto(u);
    }
    @Override @Transactional public void delete(Long id) { users.delete(get(id)); }
    @Override public long count() { return users.count(); }
    @Override public List<UserDTO> choices() { return users.findAll(Sort.by(User.PROPERTY_USERNAME)).stream().map(this::toDto).toList(); }
    private User get(Long id) { return users.findById(id).orElseThrow(() -> new IllegalArgumentException("User không tồn tại")); }
    private Role role(String name) { return roles.findByName(name).orElseThrow(() -> new IllegalArgumentException("Role không hợp lệ")); }
    private void validateUnique(Long id, UserDTO dto) {
        users.findByUsername(dto.getUsername()).filter(u -> !u.getId().equals(id)).ifPresent(u -> { throw new IllegalArgumentException("Username đã tồn tại"); });
        users.findByEmail(dto.getEmail()).filter(u -> !u.getId().equals(id)).ifPresent(u -> { throw new IllegalArgumentException("Email đã tồn tại"); });
    }
}
