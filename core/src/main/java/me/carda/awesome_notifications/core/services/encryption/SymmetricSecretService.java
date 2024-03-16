package me.carda.awesome_notifications.core.services.encryption;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.content.Context;
import android.util.Base64;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.List;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import me.carda.awesome_notifications.core.exceptions.AwesomeNotificationsException;
import me.carda.awesome_notifications.core.exceptions.ExceptionCode;
import me.carda.awesome_notifications.core.exceptions.ExceptionFactory;
import me.carda.awesome_notifications.core.managers.KeyConfigManager;
import me.carda.awesome_notifications.core.models.keys.SymmetricSecretConfig;

public class SymmetricSecretService {
    private static final String TAG = "SymmetricKeyService";
    private final KeyConfigManager keyConfigManager;

    static SymmetricSecretService instance;
    public static void initialize(Context context) {
        if (instance != null) return;
        instance = new SymmetricSecretService(context);
    }

    @NonNull
    public static SymmetricSecretService getInstance() throws AwesomeNotificationsException {
        if (instance == null) {
            throw ExceptionFactory
                    .getInstance()
                    .createNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_CLASS_NOT_FOUND,
                            "SymmetricKeyService service was not initialized",
                            ExceptionCode.DETAILED_INITIALIZATION_FAILED+".getInstance");
        }
        return instance;
    }

    private SymmetricSecretService(@NonNull Context context) {
        this.keyConfigManager = new KeyConfigManager(context);
    }

    public boolean registerSymmetricSecretConfig(
            @NonNull SymmetricSecretConfig secretConfig
    ) throws AwesomeNotificationsException {
        return keyConfigManager.setSymmetricSecretConfig(secretConfig);
    }

    @Nullable
    public SymmetricSecretConfig readSymmetricSecretConfig(
            @NonNull String keyReference
    ) throws AwesomeNotificationsException {
        return keyConfigManager.getSymmetricSecretConfig(keyReference);
    }

    public boolean removeSymmetricSecretConfig(
            @NonNull String keyReference
    ) throws AwesomeNotificationsException {
        return keyConfigManager.removeSymmetricSecretConfig(keyReference);
    }

    @NonNull
    public List<SymmetricSecretConfig> listAllSymmetricSecretConfigs(
    ) throws AwesomeNotificationsException {
        return keyConfigManager.listAllSymmetricSecretConfigs();
    }

    @NonNull
    public String generateSymmetricBase64Secret(
            @NonNull SymmetricSecretConfig secretConfig
    ) throws AwesomeNotificationsException {
        try {
            SecretKey secretKey = generateSymmetricSecret(secretConfig);
            return Base64.encodeToString(secretKey.getEncoded(), Base64.DEFAULT);
        }
        catch (AwesomeNotificationsException e) { throw e; }
        catch (Exception e) {
            throw ExceptionFactory
                    .getInstance()
                    .createNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_EVENT_EXCEPTION,
                            "Base64Secret key has failed to generate: " + e.getMessage(),
                            ExceptionCode.DETAILED_UNEXPECTED_ERROR + ".generateSymmetricBase64Secret",
                            e);
        }
    }

    @NonNull
    public SecretKey generateSymmetricSecret(
            @NonNull SymmetricSecretConfig secretConfig
    ) throws AwesomeNotificationsException {
        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[secretConfig.saltLength];
            random.nextBytes(salt);

            KeySpec spec = new PBEKeySpec(
                    generateRandomPassword(secretConfig.secretLength),
                    salt,
                    secretConfig.digestIterations,
                    secretConfig.keyBytesCount * 8
            );

            SecretKeyFactory factory = SecretKeyFactory.getInstance(secretConfig.keyDerivationAlgorithm.getAlgorithmName());
            byte[] secretKeyEncoded = factory.generateSecret(spec).getEncoded();
            return new SecretKeySpec(secretKeyEncoded, secretConfig.symmetricAlgorithm.getAlgorithmName());
        } catch (Exception e) {
            throw ExceptionFactory
                    .getInstance()
                    .createNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_EVENT_EXCEPTION,
                            "Symmetric secret has failed to generate: "+e.getMessage(),
                            ExceptionCode.DETAILED_UNEXPECTED_ERROR + ".generateSymmetricSecret",
                            e);
        }
    }

    private static char[] generateRandomPassword(int length) {
        SecureRandom random = new SecureRandom();
        char[] password = new char[length];
        for (int i = 0; i < length; i++) {
            password[i] = (char) (random.nextInt(126 - 33) + 33);
        }
        return password;
    }

    @NonNull
    public SecretKey extractSecretKeySpecFromBase64Secret(
            @NonNull SymmetricSecretConfig secretConfig,
            @NonNull String base64Secret
    ) {
        byte[] decodedKey = Base64.decode(base64Secret, Base64.DEFAULT);
        return new SecretKeySpec(decodedKey, secretConfig.symmetricAlgorithm.getAlgorithmName());
    }
}
