package com.example.bot_builder.domain.model.mapper;

import com.example.bot_builder.domain.TelegramUser;
import org.mapstruct.Mapper;
import org.telegram.telegrambots.meta.api.objects.User;

@Mapper(componentModel = "spring")
public interface TelegramUserMapper extends DataMapper<TelegramUser, User> {
}