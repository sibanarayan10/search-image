package com.searchimage.search_image.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;


@Configuration
public class Config {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

    @Bean
    public ModelMapper createModelMapper() {
        return new ModelMapper();
    }

    @Bean
    public com.querydsl.jpa.impl.JPAQueryFactory jpaQueryFactory(jakarta.persistence.EntityManager entityManager) {
        return new com.querydsl.jpa.impl.JPAQueryFactory(entityManager);
    }
}