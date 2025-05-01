package com.gabeust.userservice.service.interf;

import com.gabeust.userservice.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> findAll();
    Optional<User> findById(Long id);
    User save(User user);
    void deleteByid(Long id);
    String encriptPassword(String password);
    User findUserByEmail(String email);
    boolean existsByEmail(@NotBlank @Email String email);

}
