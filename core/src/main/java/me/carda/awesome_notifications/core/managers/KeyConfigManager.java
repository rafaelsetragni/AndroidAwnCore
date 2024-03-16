package me.carda.awesome_notifications.core.managers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.carda.awesome_notifications.core.databases.keypairs.KeyStorage;
import me.carda.awesome_notifications.core.databases.keypairs.SecureKeyStorage;
import me.carda.awesome_notifications.core.exceptions.AwesomeNotificationsException;
import me.carda.awesome_notifications.core.models.keys.PrivateKeyPem;
import me.carda.awesome_notifications.core.models.keys.PublicKeyPem;
import me.carda.awesome_notifications.core.models.keys.PrivateKeyConfig;
import me.carda.awesome_notifications.core.models.keys.PublicKeyConfig;
import me.carda.awesome_notifications.core.models.keys.SymmetricSecretConfig;

public class KeyConfigManager {
    private final KeyStorage keyStorage;
    public KeyConfigManager(Context context) {
        try {
            keyStorage = new SecureKeyStorage();
            keyStorage.initialize(context);
        } catch (AwesomeNotificationsException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    private String getSymmetricSecretReference(@NonNull String originalKey){
        return "0==" + originalKey;
    }

    @NonNull
    private String getPrivateKeyReference(@NonNull String originalKey){
        return "1==" + originalKey;
    }

    @NonNull
    private String getPublicKeyReference(@NonNull String originalKey){
        return "2==" + originalKey;
    }

    @NonNull
    private String getPrivatePemReference(@NonNull String originalKey){
        return "3==" + originalKey;
    }

    @NonNull
    private String getPublicPemReference(@NonNull String originalKey){
        return "4==" + originalKey;
    }

    public boolean setSymmetricSecretConfig(
            @NonNull SymmetricSecretConfig secretConfig
    ) throws AwesomeNotificationsException {
        return keyStorage
                .write(
                        getPrivateKeyReference(secretConfig.keyReference),
                        secretConfig,
                        SymmetricSecretConfig.class
                );
    }

    public boolean setPrivateKeyConfig(
            @NonNull PrivateKeyConfig privateKeyConfig
    ) throws AwesomeNotificationsException {
        return keyStorage
                .write(
                    getPrivateKeyReference(privateKeyConfig.keyReference),
                    privateKeyConfig,
                    PrivateKeyConfig.class
                );
    }

    public boolean setPublicKeyConfig(
            @NonNull PublicKeyConfig publicKeyConfig
    ) throws AwesomeNotificationsException {
        return keyStorage
                .write(
                        getPublicKeyReference(publicKeyConfig.keyReference),
                        publicKeyConfig,
                        PublicKeyConfig.class
                );
    }

    public boolean setPrivatePemConfig(
            @NonNull PrivateKeyPem privateKeyPem
    ) throws AwesomeNotificationsException {
        return keyStorage
                .write(
                        getPublicPemReference(privateKeyPem.keyReference),
                        privateKeyPem.toJson(),
                        String.class
                );
    }

    public boolean setPublicPemConfig(
            @NonNull PublicKeyPem publicKeyPem
    ) throws AwesomeNotificationsException {
        return keyStorage
                .write(
                        getPublicPemReference(publicKeyPem.keyReference),
                        publicKeyPem.toJson(),
                        String.class
                );
    }

    @Nullable
    public SymmetricSecretConfig getSymmetricSecretConfig(
            @NonNull String keyReference
    ) throws AwesomeNotificationsException {
        return keyStorage
                .read(
                        getSymmetricSecretReference(keyReference),
                        SymmetricSecretConfig.class
                );
    }

    @Nullable
    public PrivateKeyConfig getPrivateKeyConfig(
            @NonNull String keyReference
    ) throws AwesomeNotificationsException {
        return keyStorage
                .read(
                    getPrivateKeyReference(keyReference),
                    PrivateKeyConfig.class
                );
    }

    @Nullable
    public PublicKeyConfig getPublicKeyConfig(
            @NonNull String keyReference
    ) throws AwesomeNotificationsException {
        return keyStorage
                .read(
                    getPublicKeyReference(keyReference),
                    PublicKeyConfig.class
                );
    }

    @Nullable
    public PrivateKeyPem getPrivateKeyPem(
            @NonNull String keyReference
    ) throws AwesomeNotificationsException {
        String value = keyStorage
                .read(getPrivatePemReference(keyReference), String.class);
        if (value == null) return null;
        return new PrivateKeyPem(keyReference, value);
    }

    @Nullable
    public PublicKeyPem getPublicKeyPem(
            @NonNull String keyReference
    ) throws AwesomeNotificationsException {
        String value = keyStorage
                .read(getPublicPemReference(keyReference), String.class);
        if (value == null) return null;
        return new PublicKeyPem(keyReference, value);
    }

    @NonNull
    public List<SymmetricSecretConfig> listAllSymmetricSecretConfigs(
    ) throws AwesomeNotificationsException {
        return new ArrayList<>(
                keyStorage
                        .readAll(SymmetricSecretConfig.class)
                        .values()
        );
    }

    @NonNull
    public List<PrivateKeyConfig> listAllPrivateKeyConfigs(
    ) throws AwesomeNotificationsException {
        return new ArrayList<>(
            keyStorage
                    .readAll(PrivateKeyConfig.class)
                    .values()
        );
    }

    @NonNull
    public List<PublicKeyConfig> listAllPublicKeyConfigs(
    ) throws AwesomeNotificationsException {
        return new ArrayList<>(
            keyStorage
                    .readAll(PublicKeyConfig.class)
                    .values()
        );
    }

    @NonNull
    public List<PrivateKeyPem> listAllPrivateKeyPem() throws AwesomeNotificationsException {
        Map<String, String> allKeys = keyStorage.readAll(String.class);
        List<PrivateKeyPem> privateKeyPems = new ArrayList<>();
        for (Map.Entry<String, String> entry : allKeys.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(getPrivatePemReference(""))) {
                String keyReference = key.substring(3);
                privateKeyPems.add(new PrivateKeyPem(keyReference, entry.getValue()));
            }
        }
        return privateKeyPems;
    }

    @NonNull
    public List<PublicKeyPem> listAllPublicKeyPem() throws AwesomeNotificationsException {
        Map<String, String> allKeys = keyStorage.readAll(String.class);
        List<PublicKeyPem> publicKeyPems = new ArrayList<>();
        for (Map.Entry<String, String> entry : allKeys.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(getPublicPemReference(""))) {
                String keyReference = key.substring(3);
                publicKeyPems.add(new PublicKeyPem(keyReference, entry.getValue()));
            }
        }
        return publicKeyPems;
    }

    public boolean removeSymmetricSecretConfig(
            @NonNull String keyReference
    ) throws AwesomeNotificationsException {
        return keyStorage
                .delete(
                        getSymmetricSecretReference(keyReference),
                        SymmetricSecretConfig.class
                );
    }

    public boolean removePrivateKeyConfig(
            @NonNull String keyReference
    ) throws AwesomeNotificationsException {
        return keyStorage
                .delete(
                        getPrivateKeyReference(keyReference),
                        PrivateKeyConfig.class
                );
    }

    public boolean removePublicKeyConfig(
            @NonNull String keyReference
    ) throws AwesomeNotificationsException {
        return keyStorage
                .delete(
                        getPublicKeyReference(keyReference),
                        PublicKeyConfig.class
                );
    }

    public boolean removePrivateKeyPem(
            @NonNull String keyReference
    ) throws AwesomeNotificationsException {
        return keyStorage
                .delete(
                        getPrivatePemReference(keyReference),
                        String.class
                );
    }

    public boolean removePublicKeyPem(
            @NonNull String keyReference
    ) throws AwesomeNotificationsException {
        return keyStorage
                .delete(
                        getPublicPemReference(keyReference),
                        String.class
                );
    }
}
