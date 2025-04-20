package com.example.bot_builder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.List;

@Configuration
public class BotsRegisterConfig {
    @Bean
    public TelegramBotsApi telegramBotsApi(List<TelegramLongPollingBot> bots) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        for (TelegramLongPollingBot bot : bots) {
            telegramBotsApi.registerBot(bot);
        }
        return telegramBotsApi;
    }
}