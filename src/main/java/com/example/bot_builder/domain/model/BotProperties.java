package com.example.bot_builder.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BotProperties {
    private String username;
    private String token;
    private boolean enabled;
}
