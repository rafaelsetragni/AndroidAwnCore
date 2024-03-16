package me.carda.awesome_notifications.core.databases.keypairs;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import me.carda.awesome_notifications.core.exceptions.AwesomeNotificationsException;

public interface KeyStorage {

    void initialize(Context context) throws AwesomeNotificationsException;

    <T> boolean containsKey(@NonNull String key, @NonNull Class<T> clazz) throws AwesomeNotificationsException;

    @Nullable
    <T> T read(@NonNull String key, @NonNull Class<T> clazz) throws AwesomeNotificationsException;
    @NonNull
    <T> Map<String, T> readAll(@NonNull Class<T> clazz) throws AwesomeNotificationsException;

    <T> boolean write(@NonNull String key, @NonNull T value, @NonNull Class<T> clazz) throws AwesomeNotificationsException;
    <T> boolean writeAll(@NonNull Map<String, T> values, @NonNull Class<T> clazz) throws AwesomeNotificationsException;

    <T> boolean delete(@NonNull String key, @NonNull Class<T> clazz) throws AwesomeNotificationsException;
    <T> boolean deleteAll(@NonNull Class<T> clazz) throws AwesomeNotificationsException;
}
