package ru.practicum.ewm.events.enums;

import java.util.Arrays;

public enum StateActionPrivate {
    SEND_TO_REVIEW,
    CANCEL_REVIEW;

    public static StateActionPrivate getState(String state) {
        return Arrays.stream(values()).filter(it -> it.name().equals(state)).findFirst().orElse(null);
    }
}
