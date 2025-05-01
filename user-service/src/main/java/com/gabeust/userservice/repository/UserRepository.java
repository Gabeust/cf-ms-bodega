package com.gabeust.userservice.repository;

import com.gabeust.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByEmain (String email);
    boolean existsByEmail(String email);
}
