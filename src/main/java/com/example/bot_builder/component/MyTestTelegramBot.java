package com.example.bot_builder.component;

import com.example.bot_builder.domain.CustomerRequest;
import com.example.bot_builder.domain.TelegramUser;
import com.example.bot_builder.domain.model.mapper.TelegramUserMapper;
import com.example.bot_builder.enums.BotKind;
import com.example.bot_builder.enums.RequestSteps;
import com.example.bot_builder.exeption.CustomWarningException;
import com.example.bot_builder.service.CustomerRequestService;
import com.example.bot_builder.service.TelegramUserService;
import com.example.bot_builder.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.bot_builder.enums.RequestSteps.*;

@Component
@ConditionalOnProperty(name = "bots.testbot.enabled", havingValue = "true")
public class MyTestTelegramBot extends TelegramLongPollingBot {
    private final CustomerRequestService customerRequestService;
    private final TelegramUserMapper telegramUserMapper;
    private final TelegramUserService telegramUserService;

    private static final String BUTTON_BACK = "🔙 Назад";
    private static final String BUTTON_SKIP = "Пропустити";

    @Value("${bots.testbot.username}")
    private String userName;

    @Value("${bots.testbot.token}")
    private String userToken;

    private final Map<Long, RequestSteps> requestSteps = new HashMap<>();
    private final Map<Long, CustomerRequest> customerRequest = new HashMap<>();

    @Autowired
    public MyTestTelegramBot(CustomerRequestService customerRequestService,
                             TelegramUserMapper telegramUserMapper,
                             TelegramUserService telegramUserService) {
        this.customerRequestService = customerRequestService;
        this.telegramUserMapper = telegramUserMapper;
        this.telegramUserService = telegramUserService;
    }

    @Override
    public String getBotUsername() {
        return userName;
    }

    @Override
    public String getBotToken() {
        return userToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        TelegramUser telegramUser = getTelegramUser(update);

        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String text = message.getText();
            Long chatId = message.getChatId();

            if (requestSteps.containsKey(chatId)) {
                handleRequestFlow(telegramUser, chatId, text);
                return;
            }

            switch (text) {
                case "/start", BUTTON_BACK -> sendMainMenu(chatId);
                case "Про нас" -> sendMessage(chatId, "Ми Spring Boot 🚀 developers", true);
                case "Залишити запит" -> {
                    requestSteps.put(chatId, ASK_EMAIL);
                    customerRequest.put(chatId, new CustomerRequest());
                    sendMessage(chatId, "Введіть ваш email:", true, false);
                }
                default -> sendMessage(chatId, "Невідома команда");
            }
        }
    }

    private TelegramUser getTelegramUser(Update update) {
        if ((update == null) || (update.getMessage() == null) || (update.getMessage().getFrom() == null)) {
            throw new CustomWarningException("user not identified");
        }

        TelegramUser tgUser = telegramUserMapper.dtoToEntity(update.getMessage().getFrom());

        TelegramUser telegramUser = telegramUserService.findUser(tgUser);
        if (telegramUser != null) {
            return telegramUser;
        }

        return telegramUserService.save(tgUser);
    }

    private boolean isValid(RequestSteps requestSteps, String text) {
        switch (requestSteps) {
            case ASK_MESSAGE -> {
                return !Utils.isEmpty(text);
            }
            case ASK_EMAIL -> {
                return Utils.isValidEmail(text);
            }
            case ASK_PHONE -> {
                return Utils.isValidMobileNumber(text);
            }
            default -> {
                return false;
            }
        }
    }

    private void handleRequestFlow(TelegramUser telegramUser, Long chatId, String text) {
        if (BUTTON_BACK.equalsIgnoreCase(text)) {
            requestSteps.remove(chatId);
            customerRequest.remove(chatId);
            sendMainMenu(chatId);

            return;
        }

        RequestSteps step = requestSteps.get(chatId);
        CustomerRequest customerRequest = this.customerRequest.get(chatId);

        switch (step) {
            case ASK_EMAIL -> {
                if (!text.equalsIgnoreCase(BUTTON_SKIP)) {
                    if (!isValid(ASK_EMAIL, text)) {
                        sendMessage(chatId, "Email не коректний. Введіть ваш email:", true);
                        return;
                    }

                    customerRequest.setEmail(text);
                }
                requestSteps.put(chatId, ASK_PHONE);
                sendMessage(chatId, "Введіть ваш мобільний (066 123 45 67):", true);
            }
            case ASK_PHONE -> {
                if (!isValid(ASK_PHONE, text)) {
                    sendMessage(chatId, "Мобільний не коректний. Введіть ваш мобільний:", true);
                    return;
                }

                customerRequest.setMobileNumber(text);
                requestSteps.put(chatId, RequestSteps.ASK_MESSAGE);
                sendMessage(chatId, "Напишіть текст вашого звернення:", true);
            }
            case ASK_MESSAGE -> {
                if (!isValid(ASK_MESSAGE, text)) {
                    sendMessage(chatId, "Звернення не може бути пустим. Напишіть текст вашого звернення:", true);
                    return;
                }

                customerRequest.setText(text);
                requestSteps.remove(chatId);
                this.customerRequest.remove(chatId);

                customerRequest.setTelegramUser(telegramUser);
                customerRequest.setBotKind(BotKind.fromValue(getBotUsername()));

                CustomerRequest save = customerRequestService.save(customerRequest);

                sendMessage(chatId, "Дякуємо\n" +
                        "Запит успішно створено. ID: " + save.getId() + "\n" +
                        "Ми зв'яжемося з вами найближчим часом");

                sendMainMenu(chatId);
            }
        }
    }

    private ReplyKeyboardMarkup getKeyboard(List<String> buttonLabels) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        for (String label : buttonLabels) {
            row.add(label);
        }

        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);

        return keyboardMarkup;
    }

    private void sendMainMenu(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Головне меню:");

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("Про нас");
        row.add("Залишити запит");

        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);

        message.setReplyMarkup(keyboardMarkup);
        sendMessage(message);
    }

    private void sendMessage(Long chatId, String text) {
        sendMessage(chatId, text, false, false);
    }

    private void sendMessage(Long chatId, String text, boolean back) {
        sendMessage(chatId, text, back, true);
    }

    private void sendMessage(Long chatId, String text, boolean back, boolean required) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        List<String> buttonLabels = new ArrayList<>();

        if (back) {
            buttonLabels.add(BUTTON_BACK);
        }

        if (!required) {
            buttonLabels.add(BUTTON_SKIP);
        }

        message.setReplyMarkup(getKeyboard(buttonLabels));

        sendMessage(message);
    }

    private void sendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}