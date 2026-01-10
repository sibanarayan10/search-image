package com.searchimage.search_image.repository;

import com.searchimage.search_image.entity.User;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface  UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByName(String username);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
