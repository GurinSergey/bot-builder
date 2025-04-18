package com.example.bot_builder.service;

import com.example.bot_builder.domain.CustomerRequest;
import com.example.bot_builder.domain.TelegramUser;
import com.example.bot_builder.repository.TelegramUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TelegramUserService {
    private final TelegramUserRepo telegramUserRepo;

    @Autowired
    public TelegramUserService(TelegramUserRepo telegramUserRepo) {
        this.telegramUserRepo = telegramUserRepo;
    }

    public TelegramUser save(TelegramUser telegramUser) {
        return telegramUserRepo.save(telegramUser);
    }

    public TelegramUser findUser(TelegramUser telegramUser) {
        return telegramUserRepo.findByUserName(telegramUser.getUserName())
                .orElse(telegramUserRepo.findByUserId(telegramUser.getUserId())
                        .orElse(null));
    }
}
