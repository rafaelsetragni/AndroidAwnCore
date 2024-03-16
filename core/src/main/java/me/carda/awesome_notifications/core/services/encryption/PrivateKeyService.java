package me.carda.awesome_notifications.core.services.encryption;

import me.carda.awesome_notifications.core.exceptions.AwesomeNotificationsException;
import me.carda.awesome_notifications.core.exceptions.ExceptionCode;
import me.carda.awesome_notifications.core.exceptions.ExceptionFactory;
import me.carda.awesome_notifications.core.managers.KeyConfigManager;
import me.carda.awesome_notifications.core.models.keys.PrivateKeyPem;
import me.carda.awesome_notifications.core.models.keys.PrivateKeyConfig;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

import android.content.Context;
import android.util.Base64;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PrivateKeyService {
    private static final String TAG = "PrivateKeyService";
    private final KeyConfigManager keyConfigManager;
    static final int MAX_ENTRIES = 20;

    private final Map<String, PrivateKey> privateKeyCache = new LinkedHashMap<String, PrivateKey>(
            MAX_ENTRIES + 1, .75F, true) {
        protected boolean removeEldestEntry(Map.Entry<String, PrivateKey> eldest) {
            return size() > MAX_ENTRIES;
        }
    };

    static PrivateKeyService instance;
    public static void initialize(Context context) {
        if (instance != null) return;
        instance = new PrivateKeyService(context);
    }

    @NonNull
    public static PrivateKeyService getInstance() throws AwesomeNotificationsException {
        if (instance == null) {
            throw ExceptionFactory
                    .getInstance()
                    .createNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_CLASS_NOT_FOUND,
                            "PrivateKeyService service was not initialized",
                            ExceptionCode.DETAILED_INITIALIZATION_FAILED+".getInstance");
        }
        return instance;
    }

    private PrivateKeyService(@NonNull Context context) {
        this.keyConfigManager = new KeyConfigManager(context);
    }

    public boolean registerPrivateKeyConfig(
            @NonNull PrivateKeyConfig privateKeyConfig
    ) throws AwesomeNotificationsException {
        return keyConfigManager.setPrivateKeyConfig(privateKeyConfig);
    }

    @Nullable
    public PrivateKeyConfig readPrivateKeyConfig(
            @NonNull String keyReference
    ) throws AwesomeNotificationsException {
        return keyConfigManager.getPrivateKeyConfig(keyReference);
    }

    public boolean removePrivateKeyConfig(
            @NonNull String keyReference
    ) throws AwesomeNotificationsException {
        return keyConfigManager.removePrivateKeyConfig(keyReference);
    }

    @NonNull
    public List<PrivateKeyConfig> listAllPrivateKeyConfigs(
    ) throws AwesomeNotificationsException {
        return keyConfigManager.listAllPrivateKeyConfigs();
    }

    public boolean registerPrivateKeyPem(
            @NonNull PrivateKeyPem privateKeyPem
    ) throws AwesomeNotificationsException {
        return keyConfigManager.setPrivatePemConfig(privateKeyPem);
    }

    @Nullable
    public PrivateKeyPem readPrivateKeyPem(
            @NonNull String keyReference
    ) throws AwesomeNotificationsException {
        return keyConfigManager.getPrivateKeyPem(keyReference);
    }

    public boolean removePrivateKeyPem(
            @NonNull String keyReference
    ) throws AwesomeNotificationsException {
        return keyConfigManager.removePrivateKeyPem(keyReference);
    }

    @NonNull
    public List<PrivateKeyPem> listAllPrivateKeyPems(
    ) throws AwesomeNotificationsException {
        return keyConfigManager.listAllPrivateKeyPem();
    }

    @NonNull
    public PrivateKey extractPrivateKey(
            @NonNull PrivateKeyConfig privateKeyConfig,
            @NonNull PrivateKeyPem privateKeyPem
    ) throws AwesomeNotificationsException {
        // Check the cache first
        PrivateKey cachedKey = privateKeyCache.get(privateKeyConfig.keyReference);
        if (cachedKey != null) return cachedKey;

        try {
            // Remove the first and last lines and newlines
            String pemContent = privateKeyPem.getValue()
                    .replaceAll("-----[A-Z ]+-----", "")
                    .replaceAll("\\s+", "");
            byte[] decoded = Base64.decode(pemContent, Base64.DEFAULT);

            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance(privateKeyConfig.asymmetricAlgorithm.getAlgorithmName());
            PrivateKey privateKey = keyFactory.generatePrivate(spec);

            // Store the extracted key in the cache before returning it
            privateKeyCache.put(privateKeyConfig.keyReference, privateKey);

            return privateKey;
        } catch (Exception e) {
            throw ExceptionFactory
                    .getInstance()
                    .createNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_EVENT_EXCEPTION,
                            "Extract private key has failed: " + e.getMessage(),
                            ExceptionCode.DETAILED_UNEXPECTED_ERROR + ".extractPrivateKey",
                            e);
        }
    }
}