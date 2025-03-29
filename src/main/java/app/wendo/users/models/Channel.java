package app.wendo.users.models;

import app.wendo.exceptions.InvalidChannelException;
import lombok.Getter;

@Getter
public enum Channel {
    EMAIL("email"),
    SMS("sms");

    private final String value;

    Channel(String value) {
        this.value = value;
    }

    public static Channel fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Channel value cannot be null or empty");
        }

        for (Channel channel : Channel.values()) {
            if (channel.value.equalsIgnoreCase(value.trim())) {
                return channel;
            }
        }
        throw new IllegalArgumentException("Invalid channel type: " + value);
    }

    public static void validate(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidChannelException("Channel value cannot be null or empty");
        }

        for (Channel channel : Channel.values()) {
            if (channel.value.equalsIgnoreCase(value.trim())) {
                return;
            }
        }
        throw new InvalidChannelException("Invalid channel type: " + value);
    }
}