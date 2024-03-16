package me.carda.awesome_notifications.core.models.encrypted;

import android.content.Context;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import me.carda.awesome_notifications.core.Definitions;
import me.carda.awesome_notifications.core.exceptions.AwesomeNotificationsException;
import me.carda.awesome_notifications.core.models.AbstractModel;

public abstract class NotificationProtectedContentModel extends AbstractModel {
    public String keyReference;
    public String pemReference;
    public String secretReference;

    public String title;
    public String body;
    public String summary;
    public String largeIcon;
    public String bigPicture;

    NotificationProtectedContentModel(String keyReference){
        this.keyReference = keyReference;
    }

    @Override
    public NotificationProtectedContentModel fromMap(Map<String, Object> arguments) {
        keyReference    = getValueOrDefault(arguments, Definitions.NOTIFICATION_ENCRYPT_KEY_REF, String.class, "default");
        pemReference    = getValueOrDefault(arguments, Definitions.NOTIFICATION_ENCRYPT_PEM_REF, String.class, "default");
        secretReference = getValueOrDefault(arguments, Definitions.NOTIFICATION_SECRET_REF, String.class, "default");

        title       = getValueOrDefault(arguments, Definitions.NOTIFICATION_TITLE, String.class, null);
        body        = getValueOrDefault(arguments, Definitions.NOTIFICATION_BODY, String.class, null);
        summary     = getValueOrDefault(arguments, Definitions.NOTIFICATION_SUMMARY, String.class, null);
        bigPicture  = getValueOrDefault(arguments, Definitions.NOTIFICATION_BIG_PICTURE, String.class, null);
        largeIcon   = getValueOrDefault(arguments, Definitions.NOTIFICATION_LARGE_ICON, String.class, null);

        return this;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> responseMap = new HashMap<>();

        putDataOnSerializedMap(Definitions.NOTIFICATION_ENCRYPT_KEY_REF, responseMap, this.keyReference);
        putDataOnSerializedMap(Definitions.NOTIFICATION_ENCRYPT_PEM_REF, responseMap, this.keyReference);
        putDataOnSerializedMap(Definitions.NOTIFICATION_SECRET_REF, responseMap, this.keyReference);

        putDataOnSerializedMap(Definitions.NOTIFICATION_TITLE, responseMap, this.title);
        putDataOnSerializedMap(Definitions.NOTIFICATION_BODY, responseMap, this.body);
        putDataOnSerializedMap(Definitions.NOTIFICATION_SUMMARY, responseMap, this.summary);
        putDataOnSerializedMap(Definitions.NOTIFICATION_BIG_PICTURE, responseMap, this.bigPicture);
        putDataOnSerializedMap(Definitions.NOTIFICATION_LARGE_ICON, responseMap, this.largeIcon);

        return responseMap;
    }

    @Override
    public String toJson() {
        return templateToJson();
    }

    @Override
    public NotificationProtectedContentModel fromJson(@Nullable String json){
        return (NotificationProtectedContentModel) super.templateFromJson(json);
    }

    @Override
    public void validate(Context context) throws AwesomeNotificationsException {
    }

}

