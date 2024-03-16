package me.carda.awesome_notifications.core.models.encrypted;

import androidx.annotation.NonNull;

import java.util.Map;

import me.carda.awesome_notifications.core.Definitions;

public class NotificationDecryptedContentModel extends NotificationProtectedContentModel {
    String TAG = "NotificationDecryptedContentModel";

    public Map<String, Object> payload;
    public String secret;

    public NotificationDecryptedContentModel(String keyReference){
        super(keyReference);
    }

    @Override
    public NotificationDecryptedContentModel fromMap(@NonNull Map<String, Object> arguments) {
        super.fromMap(arguments);
        payload = getValueOrDefaultMap(arguments, Definitions.NOTIFICATION_PAYLOAD, null);
        secret  = getValueOrDefault(arguments, Definitions.NOTIFICATION_SECRET, String.class, null);
        return this;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> responseMap = super.toMap();
        putDataOnSerializedMap(Definitions.NOTIFICATION_SECRET, responseMap, this.secret);
        putDataOnSerializedMap(Definitions.NOTIFICATION_PAYLOAD, responseMap, this.payload);
        return responseMap;
    }
}
