package com.example.bot_builder.enums;

public enum BotKind {
    UNKNOWN(0, "unknown"),
    MY_TEST(1, "my_test_builder_bot"),
    ANGEIMEBEL(2, "angeimebel_bot");

    int index;
    String value;

    BotKind(int index, String value) {
        this.index = index;
        this.value = value;
    }

    public int getIndex() {
        return index;
    }

    public String getValue() {
        return value;
    }

    public static BotKind fromValue(String value) {
        for (BotKind botKind : BotKind.values()) {
            if (botKind.getValue().equalsIgnoreCase(value)) {
                return botKind;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
