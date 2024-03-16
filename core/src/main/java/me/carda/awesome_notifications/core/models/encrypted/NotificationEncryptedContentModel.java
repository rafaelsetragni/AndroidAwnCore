package me.carda.awesome_notifications.core.models.encrypted;

import androidx.annotation.NonNull;

import java.util.Map;

import me.carda.awesome_notifications.core.Definitions;

public class NotificationEncryptedContentModel extends NotificationProtectedContentModel {
    String TAG = "NotificationEncryptedContentModel";

    public String payload;
    public String base64Secret;

    public NotificationEncryptedContentModel(String keyReference){
        super(keyReference);
    }

    @Override
    public NotificationEncryptedContentModel fromMap(@NonNull Map<String, Object> arguments) {
        super.fromMap(arguments);
        base64Secret = getValueOrDefault(arguments, Definitions.NOTIFICATION_SECRET, String.class, null);
        payload = getValueOrDefault(arguments, Definitions.NOTIFICATION_PAYLOAD, String.class, null);
        return this;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> responseMap = super.toMap();
        putDataOnSerializedMap(Definitions.NOTIFICATION_SECRET, responseMap, this.base64Secret);
        putDataOnSerializedMap(Definitions.NOTIFICATION_PAYLOAD, responseMap, this.payload);
        return responseMap;
    }
}

