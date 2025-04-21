package com.example.bot_builder.domain;

import com.example.bot_builder.enums.BotKind;
import jakarta.persistence.*;

@Entity
@Table(schema = "my_telegram_bot", name = "customer_request")
public class CustomerRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "telegram_user_id")
    private TelegramUser telegramUser;

    @Column(name = "email")
    private String email;

    @Column(name = "mobile_phone")
    private String mobileNumber;

    @Column(name = "text")
    private String text;

    @Column(name = "processed")
    private boolean processed;

    @Enumerated
    @Column(name = "bot_kind")
    private BotKind botKind;

    public CustomerRequest() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public TelegramUser getTelegramUser() {
        return telegramUser;
    }

    public void setTelegramUser(TelegramUser telegramUser) {
        this.telegramUser = telegramUser;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public BotKind getBotKind() {
        return botKind;
    }

    public void setBotKind(BotKind botKind) {
        this.botKind = botKind;
    }
}
