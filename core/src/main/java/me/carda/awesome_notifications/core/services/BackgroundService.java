package me.carda.awesome_notifications.core.services;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import me.carda.awesome_notifications.core.AwesomeNotifications;
import me.carda.awesome_notifications.core.background.BackgroundExecutor;
import me.carda.awesome_notifications.core.exceptions.AwesomeNotificationsException;
import me.carda.awesome_notifications.core.exceptions.ExceptionCode;
import me.carda.awesome_notifications.core.exceptions.ExceptionFactory;
import me.carda.awesome_notifications.core.logs.Logger;
import me.carda.awesome_notifications.core.managers.DefaultsManager;

public abstract class BackgroundService extends JobIntentService {
    private static final String TAG = "BackgroundService";

    public abstract void initializeExternalPlugins(Context context) throws Exception;

    @Override
    protected void onHandleWork(@NonNull final Intent intent) {
        Logger.d(TAG, "A new Dart background service has started");
        try {
            initializeExternalPlugins(this);
            AwesomeNotifications.initialize(this);

            Long dartCallbackHandle = getDartCallbackDispatcher(this);
            if (dartCallbackHandle == 0L) {
                ExceptionFactory
                        .getInstance()
                        .registerNewAwesomeException(
                                TAG,
                                ExceptionCode.CODE_BACKGROUND_EXECUTION_EXCEPTION,
                                "A background message could not be handled in Dart" +
                                " because there is no onActionReceivedMethod handler registered.",
                                ExceptionCode.DETAILED_INVALID_ARGUMENTS+".dartCallback");
                return;
            }

            Long actionCallbackHandle = getActionCallbackDispatcher(this);
            if (actionCallbackHandle == 0L) {
                ExceptionFactory
                        .getInstance()
                        .registerNewAwesomeException(
                                TAG,
                                ExceptionCode.CODE_BACKGROUND_EXECUTION_EXCEPTION,
                                "An action background message could not be handled in Dart" +
                                " because there is no silent dart background handler registered.",
                                ExceptionCode.DETAILED_INVALID_ARGUMENTS+".actionCallback");
                return;
            }

            BackgroundExecutor.runBackgroundExecutor(
                    this,
                    intent,
                    dartCallbackHandle,
                    actionCallbackHandle);

        } catch (AwesomeNotificationsException ignored) {
        } catch (Exception e) {
            ExceptionFactory
                    .getInstance()
                    .registerNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_BACKGROUND_EXECUTION_EXCEPTION,
                            "A new Dart background service could not be executed",
                            ExceptionCode.DETAILED_INVALID_ARGUMENTS,
                            e);
        }
    }

    public static Long getDartCallbackDispatcher(Context context) throws AwesomeNotificationsException {
        return DefaultsManager
                .getInstance(context)
                .getDartCallbackDispatcher(context);
    }

    public static Long getActionCallbackDispatcher(Context context) throws AwesomeNotificationsException {
        return DefaultsManager
                .getInstance(context)
                .getActionCallbackDispatcher(context);
    }
}