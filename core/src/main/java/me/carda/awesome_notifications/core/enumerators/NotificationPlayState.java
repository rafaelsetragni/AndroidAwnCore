package me.carda.awesome_notifications.core.enumerators;

import androidx.annotation.NonNull;

public enum NotificationPlayState implements SafeEnum {
    unknown("unknown", -1),
    none("none", 0),
    stopped("stopped", 1),
    paused("paused", 2),
    playing("playing", 3),
    forwarding("forwarding", 4),
    rewinding("rewinding", 5),
    buffering("buffering", 6),
    error("error", 7),
    connecting("connecting", 8),
    previous("previous", 9),
    next("next", 10),
    skippingToQueueItem("skippingToQueueItem", 11);

    private final String safeName;
    public final int rawValue;
    NotificationPlayState(@NonNull String safeName, @NonNull int rawValue) {
        this.safeName = safeName;
        this.rawValue = rawValue;
    }

    public int toMap() { return rawValue; }

    public static NotificationPlayState fromMap(Object value) {
        if (value instanceof String) {
            final String stringValue = (String) value;
            for (NotificationPlayState state : values()) {
                if (state.safeName.equalsIgnoreCase(stringValue)) {
                    return state;
                }
            }
        } else if (value instanceof Number) {
            final int intValue = ((Number) value).intValue();
            for (NotificationPlayState state : values()) {
                if (state.rawValue == intValue) {
                    return state;
                }
            }
        }
        return null;
    }

    @Override
    public String getSafeName() {
        return safeName;
    }
}