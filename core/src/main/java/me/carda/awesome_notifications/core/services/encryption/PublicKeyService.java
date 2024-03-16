package me.carda.awesome_notifications.core.services.encryption;

import me.carda.awesome_notifications.core.exceptions.AwesomeNotificationsException;
import me.carda.awesome_notifications.core.exceptions.ExceptionCode;
import me.carda.awesome_notifications.core.exceptions.ExceptionFactory;
import me.carda.awesome_notifications.core.managers.KeyConfigManager;
import me.carda.awesome_notifications.core.models.keys.PublicKeyPem;
import me.carda.awesome_notifications.core.models.keys.PublicKeyConfig;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import android.content.Context;
import android.util.Base64;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PublicKeyService {
    private static final String TAG = "PublicKeyService";
    private final KeyConfigManager keyConfigManager;
    static final int MAX_ENTRIES = 100;

    private final Map<String, PublicKey> publicKeyCache = new LinkedHashMap<String, PublicKey>(
            MAX_ENTRIES + 1, .75F, true) {
        protected boolean removeEldestEntry(Map.Entry<String, PublicKey> eldest) {
            return size() > MAX_ENTRIES;
        }
    };

    static PublicKeyService instance;
    public static void initialize(Context context) {
        if (instance != null) return;
        instance = new PublicKeyService(context);
    }

    @NonNull
    public static PublicKeyService getInstance() throws AwesomeNotificationsException {
        if (instance == null) {
            throw ExceptionFactory
                    .getInstance()
                    .createNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_CLASS_NOT_FOUND,
                            "PublicKeyService service was not initialized",
                            ExceptionCode.DETAILED_INITIALIZATION_FAILED+".getInstance");
        }
        return instance;
    }

    private PublicKeyService(@NonNull Context context) {
        this.keyConfigManager = new KeyConfigManager(context);
    }

    public boolean registerPublicKeyConfig(
            @NonNull PublicKeyConfig publicKeyConfig
    ) throws AwesomeNotificationsException {
        return keyConfigManager.setPublicKeyConfig(publicKeyConfig);
    }

    @Nullable
    public PublicKeyConfig readPublicKeyConfig(
            @NonNull String keyReference
    ) throws AwesomeNotificationsException {
        return keyConfigManager.getPublicKeyConfig(keyReference);
    }

    public boolean removePublicKeyConfig(
            @NonNull String keyReference
    ) throws AwesomeNotificationsException {
        return keyConfigManager.removePublicKeyConfig(keyReference);
    }

    @NonNull
    public List<PublicKeyConfig> listAllPublicKeyConfigs(
    ) throws AwesomeNotificationsException {
        return keyConfigManager.listAllPublicKeyConfigs();
    }

    public boolean registerPublicKeyPem(
            @NonNull PublicKeyPem publicKeyPem
    ) throws AwesomeNotificationsException {
        return keyConfigManager.setPublicPemConfig(publicKeyPem);
    }

    @Nullable
    public PublicKeyPem readPublicKeyPem(
            @NonNull String keyReference
    ) throws AwesomeNotificationsException {
        return keyConfigManager.getPublicKeyPem(keyReference);
    }

    public boolean removePublicKeyPem(
            @NonNull String keyReference
    ) throws AwesomeNotificationsException {
        return keyConfigManager.removePublicKeyPem(keyReference);
    }

    @NonNull
    public List<PublicKeyPem> listAllPublicKeyPems(
    ) throws AwesomeNotificationsException {
        return keyConfigManager.listAllPublicKeyPem();
    }

    @NonNull
    public PublicKey extractPublicKey(
            @NonNull PublicKeyConfig publicKeyConfig,
            @NonNull PublicKeyPem publicKeyPem
    ) throws AwesomeNotificationsException {
        try {
            String cacheKey = publicKeyConfig.keyReference;
            PublicKey cachedKey = publicKeyCache.get(cacheKey);
            if (cachedKey != null) {
                return cachedKey;
            }

            String pemContent = publicKeyPem.getValue()
                    .replaceAll("-----[A-Z ]+-----", "")
                    .replaceAll("\\s+", "");
            byte[] decoded = Base64.decode(pemContent, Base64.DEFAULT);

            X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance(publicKeyConfig.asymmetricAlgorithm.getAlgorithmName());
            PublicKey publicKey = keyFactory.generatePublic(spec);

            publicKeyCache.put(cacheKey, publicKey);
            return publicKey;
        } catch (Exception e) {
            throw ExceptionFactory
                    .getInstance()
                    .createNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_EVENT_EXCEPTION,
                            "Extract public key has failed: " + e.getMessage(),
                            ExceptionCode.DETAILED_UNEXPECTED_ERROR + ".extractPublicKey",
                            e);
        }
    }
}

