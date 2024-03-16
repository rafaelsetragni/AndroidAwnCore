package me.carda.awesome_notifications.core.models;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.carda.awesome_notifications.core.Definitions;
import me.carda.awesome_notifications.core.exceptions.AwesomeNotificationsException;
import me.carda.awesome_notifications.core.exceptions.ExceptionCode;
import me.carda.awesome_notifications.core.exceptions.ExceptionFactory;
import me.carda.awesome_notifications.core.models.encrypted.NotificationDecryptedContentModel;
import me.carda.awesome_notifications.core.models.encrypted.NotificationEncryptedContentModel;

public class NotificationModel extends AbstractModel {

    private static final String TAG = "NotificationModel";

    public boolean groupSummary = false;
    public String remoteHistory;

    public NotificationContentModel content;
    public NotificationScheduleModel schedule;
    public List<NotificationButtonModel> actionButtons;
    public Map<String, NotificationLocalizationModel> localizations;
    public NotificationEncryptedContentModel encryptedContent;
    public NotificationDecryptedContentModel decryptedContent;

    public NotificationModel(){}

    public NotificationModel ClonePush(){
        return new NotificationModel().fromMap(this.toMap());
    }

    @Override
    @Nullable
    public NotificationModel fromMap(Map<String, Object> parameters){
        content = extractNotificationContent(
                Definitions.NOTIFICATION_MODEL_CONTENT, parameters);
        if(content == null) return null;

        schedule = extractNotificationSchedule(
                Definitions.NOTIFICATION_MODEL_SCHEDULE, parameters);
        actionButtons = extractNotificationButtons(
                Definitions.NOTIFICATION_MODEL_BUTTONS, parameters);
        localizations = extractNotificationLocalizations(
                Definitions.NOTIFICATION_MODEL_LOCALIZATIONS, parameters);
        encryptedContent = extractNotificationEncryptedContent(
                Definitions.NOTIFICATION_MODEL_ENCRYPTED_CONTENT, parameters);
        decryptedContent = extractNotificationDecryptedContent(
                Definitions.NOTIFICATION_MODEL_DECRYPTED_CONTENT, parameters);

        return this;
    }

    @Override
    public Map<String, Object> toMap(){
        if(content == null) return null;
        Map<String, Object> dataMap = new HashMap<>();

        putDataOnSerializedMap(Definitions.NOTIFICATION_MODEL_CONTENT, dataMap, content);
        putDataOnSerializedMap(Definitions.NOTIFICATION_MODEL_SCHEDULE, dataMap, schedule);
        putDataOnSerializedMap(Definitions.NOTIFICATION_MODEL_BUTTONS, dataMap, actionButtons);
        putDataOnSerializedMap(Definitions.NOTIFICATION_MODEL_LOCALIZATIONS, dataMap, localizations);
        putDataOnSerializedMap(Definitions.NOTIFICATION_MODEL_ENCRYPTED_CONTENT, dataMap, encryptedContent);
        putDataOnSerializedMap(Definitions.NOTIFICATION_MODEL_DECRYPTED_CONTENT, dataMap, decryptedContent);

        return dataMap;
    }

    @Override
    public String toJson() {
        return templateToJson();
    }

    @Override
    public NotificationModel fromJson(String json){
        return (NotificationModel) super.templateFromJson(json);
    }

    @Nullable
    private static NotificationContentModel extractNotificationContent(
            @NonNull String reference,
            @NonNull Map<String, Object> parameters
    ){
        if(!parameters.containsKey(reference)) return null;
        Object obj = parameters.get(reference);

        if(!(obj instanceof Map<?,?>)) return null;

        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) obj;

        if(map.isEmpty()) return null;
        else return new NotificationContentModel().fromMap(map);
    }

    @Nullable
    private static NotificationScheduleModel extractNotificationSchedule(
            @NonNull String reference,
            @NonNull Map<String, Object> parameters
    ){
        if(!parameters.containsKey(reference)) return null;
        Object obj = parameters.get(reference);

        if(!(obj instanceof Map<?,?>)) return null;

        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) obj;

        return NotificationScheduleModel.getScheduleModelFromMap(map);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private static List<NotificationButtonModel> extractNotificationButtons(
            @NonNull String reference,
            @NonNull Map<String, Object> parameters
    ){
        if(!parameters.containsKey(reference)) return null;
        Object obj = parameters.get(reference);

        if(!(obj instanceof List<?>)) return null;
        List<Object> actionButtonsData = (List<Object>) obj;

        List<NotificationButtonModel> actionButtons = new ArrayList<>();

        for (Object objButton: actionButtonsData) {
            if(!(objButton instanceof Map<?,?>)) return null;

            Map<String, Object> map = (Map<String, Object>) objButton;
            if(map.isEmpty()) continue;

            NotificationButtonModel button = new NotificationButtonModel().fromMap(map);
            actionButtons.add(button);
        }

        if(actionButtons.isEmpty()) return null;

        return actionButtons;
    }

    private Map<String, NotificationLocalizationModel> extractNotificationLocalizations(
            @NonNull String reference,
            @NonNull Map<String, Object> parameters
    ) {
        if(!parameters.containsKey(reference)) return null;
        Object obj = parameters.get(reference);

        if(!(obj instanceof Map<?,?>)) return null;
        Map<String, Object> localizationsData = (Map<String, Object>) obj;
        if (localizationsData == null) return null;

        Map<String, NotificationLocalizationModel> localizationModels = new HashMap<>();
        for (Map.Entry<String, Object> entry: localizationsData.entrySet()) {
            if(!(entry.getValue() instanceof Map<?,?>)) continue;
            Map<String, Object> localizationData = (Map<String, Object>) entry.getValue();
            NotificationLocalizationModel localizationModel =
                    new NotificationLocalizationModel().fromMap(localizationData);
            if(localizationModel == null) continue;
            localizationModels.put(entry.getKey(), localizationModel);
        }

        if (localizationModels.isEmpty()) return null;
        return localizationModels;
    }

    @Nullable
    private NotificationEncryptedContentModel extractNotificationEncryptedContent(
            @NonNull String reference,
            @NonNull Map<String, Object> parameters
    ) {
        if(!parameters.containsKey(reference)) return null;
        Object obj = parameters.get(reference);

        if(!(obj instanceof Map<?,?>)) return null;

        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) obj;

        return new NotificationEncryptedContentModel("").fromMap(map);
    }

    @Nullable
    private NotificationDecryptedContentModel extractNotificationDecryptedContent(
            @NonNull String reference,
            @NonNull Map<String, Object> parameters
    ) {
        if(!parameters.containsKey(reference)) return null;
        Object obj = parameters.get(reference);

        if(!(obj instanceof Map<?,?>)) return null;

        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) obj;

        return new NotificationDecryptedContentModel("").fromMap(map);
    }

    public void validate(
            Context context
    ) throws AwesomeNotificationsException {
        if(this.content == null)
            throw ExceptionFactory
                    .getInstance()
                    .createNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_INVALID_ARGUMENTS,
                            "Notification content is required",
                            ExceptionCode.DETAILED_INVALID_ARGUMENTS+".notificationContent");

        this.content.validate(context);

        if(this.schedule != null)
            this.schedule.validate(context);

        if(this.actionButtons != null){
            for(NotificationButtonModel button : this.actionButtons){
                button.validate(context);
            }
        }
    }

}
