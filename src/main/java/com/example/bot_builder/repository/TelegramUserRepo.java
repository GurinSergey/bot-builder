package com.example.bot_builder.repository;

import com.example.bot_builder.domain.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TelegramUserRepo extends JpaRepository<TelegramUser, Long> {
    Optional<TelegramUser> findByUserName(String userName);
    Optional<TelegramUser> findByUserId(Long userId);
}

