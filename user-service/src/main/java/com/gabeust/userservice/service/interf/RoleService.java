package com.gabeust.userservice.service.interf;

import com.gabeust.userservice.entity.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    List<Role> findAll();
    Optional<Role>findById(Long id);
    Role save(Role role);
}
