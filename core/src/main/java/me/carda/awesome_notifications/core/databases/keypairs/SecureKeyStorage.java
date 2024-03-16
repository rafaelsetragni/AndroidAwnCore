package me.carda.awesome_notifications.core.databases.keypairs;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.google.gson.Gson;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import me.carda.awesome_notifications.core.exceptions.AwesomeNotificationsException;
import me.carda.awesome_notifications.core.exceptions.ExceptionCode;
import me.carda.awesome_notifications.core.exceptions.ExceptionFactory;
import me.carda.awesome_notifications.core.logs.Logger;

public class SecureKeyStorage implements KeyStorage {
    private final String TAG = "SecureKeyStorage";

    private final String DATABASE_NAME = TAG;
    private final Gson gson = new Gson();
    private SharedPreferences preferences;

    public void initialize(Context context) throws AwesomeNotificationsException {
        if (preferences == null) {
            try {
                preferences = getSharedPreferences(context);
            } catch (Exception e) {
                throw ExceptionFactory
                        .getInstance()
                        .createNewAwesomeException(
                                TAG,
                                ExceptionCode.CODE_INVALID_ARGUMENTS,
                                "SecureKeyStorage is not available",
                                ExceptionCode.DETAILED_INVALID_ARGUMENTS + ".SecureKeyStorage");
            }
        }
    }

    private SharedPreferences getSharedPreferences(
            @NonNull Context context
    ) throws GeneralSecurityException, IOException {
        KeyGenParameterSpec keyParameters = new KeyGenParameterSpec
                .Builder(
                    MasterKey.DEFAULT_MASTER_KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT
                )
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setKeySize(256)
                .build();

        MasterKey masterKey = new MasterKey
                .Builder(context)
                .setKeyGenParameterSpec(keyParameters)
                .build();

        return EncryptedSharedPreferences.create(
                context,
                DATABASE_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
    }

    private void ensureInitialized() throws AwesomeNotificationsException {
        if (preferences == null)
            throw ExceptionFactory
                    .getInstance()
                    .createNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_INITIALIZATION_EXCEPTION,
                            "SecureKeyStorage is not initialized",
                            ExceptionCode.DETAILED_INITIALIZATION_FAILED + ".SecureKeyStorage");
    }

    @Nullable
    private <T> T readRaw(@NonNull String value, @NonNull Class<T> clazz) throws Exception {
        if (clazz == String.class) {
            return clazz.cast(value);
        } else if (clazz == Boolean.class) {
            return clazz.cast("1".equals(value));
        } else if (clazz == Float.class) {
            return clazz.cast(Float.valueOf(value));
        } else if (clazz == Double.class) {
            return clazz.cast(Double.valueOf(value));
        } else if (clazz == Long.class) {
            return clazz.cast(Long.valueOf(value));
        } else if (clazz == Integer.class) {
            return clazz.cast(Integer.valueOf(value));
        } else {
            return gson.fromJson(value, clazz);
        }
    }

    @NonNull
    public <T> String writeRaw(@NonNull String modifiedKey, @NonNull T value) {
        if (modifiedKey.startsWith("\"")) {
            return gson.toJson(value);
        } else
        if (modifiedKey.startsWith("b")) {
            return (boolean) value ? "1" : "0";
        } else {
            return value.toString();
        }
    }

    @NonNull
    private <T> String getKeyPrefix(@NonNull Class<T> clazz) {
        String prefix;
        if (clazz == String.class) {
            prefix = "s==";
        } else if (clazz == Boolean.class) {
            prefix = "b==";
        } else if (clazz == Float.class) {
            prefix = "f==";
        } else if (clazz == Double.class) {
            prefix = "d==";
        } else if (clazz == Long.class) {
            prefix = "l==";
        } else if (clazz == Integer.class) {
            prefix = "i==";
        } else {
            prefix = "\"==";
        }
        return prefix;
    }

    @NonNull
    private <T> String getModifiedKey(@NonNull String key, @NonNull Class<T> clazz) {
        return getKeyPrefix(clazz) + key;
    }

    @Override
    public <T> boolean containsKey(@NonNull String key, @NonNull Class<T> clazz) throws AwesomeNotificationsException {
        ensureInitialized();
        return preferences.contains(getModifiedKey(key, clazz));
    }

    @Nullable
    @Override
    public <T> T read(@NonNull String key, @NonNull Class<T> clazz) throws AwesomeNotificationsException {
        ensureInitialized();

        SharedPreferences prefs = preferences;
        try {
            String modifiedKey = getModifiedKey(key, clazz);
            String value = prefs.getString(modifiedKey, null);
            if (value == null) return null;
            return readRaw(value, clazz);
        } catch (Exception e){
            Logger.getInstance().e(TAG, e.getMessage(), e);
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove(key);
            editor.apply();
            return null;
        }
    }

    @NonNull
    @Override
    public <T> Map<String, T> readAll(@NonNull Class<T> clazz) throws AwesomeNotificationsException {
        ensureInitialized();

        SharedPreferences prefs = preferences;
        Map<String, T> results = new HashMap<>();

        String prefix = getKeyPrefix(clazz);
        Map<String, ?> allPrefs = prefs.getAll();
        for (String fullKey : allPrefs.keySet()) {
            if (!fullKey.startsWith(prefix)) continue;
            String originalKey = fullKey.substring(3);
            try {
                String rawValue = (String) allPrefs.get(fullKey);
                if (rawValue == null) continue;
                T value = readRaw(rawValue, clazz);
                if (value == null) continue;
                results.put(originalKey, value);
            } catch (Exception e) {
                // Log or handle the exception as needed
                Logger.getInstance().e(TAG, "Failed to read key " + originalKey + ": " + e.getMessage(), e);
            }
        }
        return results;
    }

    @Override
    public <T> boolean write(@NonNull String key, @NonNull T value, @NonNull Class<T> clazz) throws AwesomeNotificationsException {
        ensureInitialized();

        SharedPreferences.Editor editor = preferences.edit();
        try {
            String modifiedKey = getModifiedKey(key, clazz);
            String stringValue = writeRaw(modifiedKey, value);
            editor.putString(modifiedKey, stringValue);
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            editor.apply();
        }
    }

    @Override
    public <T> boolean writeAll(@NonNull Map<String, T> values, @NonNull Class<T> clazz) throws AwesomeNotificationsException {
        ensureInitialized();

        SharedPreferences.Editor editor = preferences.edit();
        try {
            for (Map.Entry<String, T> entry : values.entrySet()) {
                String modifiedKey = getModifiedKey(entry.getKey(), clazz);
                editor.putString(modifiedKey, writeRaw(modifiedKey, entry.getValue()));
            }
            return true;
        } catch (Exception e){
            return false;
        } finally {
            editor.apply();
        }
    }

    @Override
    public <T> boolean delete(@NonNull String key, @NonNull Class<T> clazz) throws AwesomeNotificationsException {
        ensureInitialized();

        String modifiedKey = getModifiedKey(key, clazz);
        if (preferences.contains(modifiedKey)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove(modifiedKey);
            editor.apply();
            return true;
        }

        return false;
    }

    @Override
    public <T> boolean deleteAll(@NonNull Class<T> clazz) throws AwesomeNotificationsException {
        ensureInitialized();

        Map<String, ?> entries = preferences.getAll();
        String prefix = getKeyPrefix(clazz);

        SharedPreferences.Editor editor = preferences.edit();
        for (Map.Entry<String, ?> entry : entries.entrySet()) {
            String key = entry.getKey();
            if (!key.startsWith(prefix)) continue;
            editor.remove(key);
        }

        editor.apply();
        return true;
    }
}
