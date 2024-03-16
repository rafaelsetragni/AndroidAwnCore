package me.carda.awesome_notifications.core.services.encryption;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.carda.awesome_notifications.core.enumerators.SymmetricAlgorithm;
import me.carda.awesome_notifications.core.exceptions.AwesomeNotificationsException;
import me.carda.awesome_notifications.core.exceptions.ExceptionCode;
import me.carda.awesome_notifications.core.exceptions.ExceptionFactory;
import me.carda.awesome_notifications.core.models.keys.PrivateKeyPem;
import me.carda.awesome_notifications.core.models.keys.PublicKeyPem;
import me.carda.awesome_notifications.core.models.encrypted.NotificationDecryptedContentModel;
import me.carda.awesome_notifications.core.models.encrypted.NotificationEncryptedContentModel;
import me.carda.awesome_notifications.core.models.KeyPairModel;
import me.carda.awesome_notifications.core.models.keys.PrivateKeyConfig;
import me.carda.awesome_notifications.core.models.keys.PublicKeyConfig;
import me.carda.awesome_notifications.core.models.keys.SymmetricSecretConfig;
import me.carda.awesome_notifications.core.utils.JsonUtils;


public class EncryptionService {
    static final String TAG = "EncryptionService";

    final SymmetricSecretService symmetricKeyService;
    final PrivateKeyService privateKeyService;
    final PublicKeyService publicKeyService;

    public EncryptionService() throws AwesomeNotificationsException {
        symmetricKeyService = SymmetricSecretService.getInstance();
        privateKeyService = PrivateKeyService.getInstance();
        publicKeyService = PublicKeyService.getInstance();
    }

    static EncryptionService instance;
    public void initialize(@NonNull Context context) throws AwesomeNotificationsException {
        if (instance != null) return;
        SymmetricSecretService.initialize(context);
        PrivateKeyService.initialize(context);
        PublicKeyService.initialize(context);
        instance = new EncryptionService();
    }

    @NonNull
    public static EncryptionService getInstance() throws AwesomeNotificationsException {
        if (instance == null) {
            throw ExceptionFactory
                    .getInstance()
                    .createNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_CLASS_NOT_FOUND,
                            "Encryption service was not initialized",
                            ExceptionCode.DETAILED_INITIALIZATION_FAILED+".getInstance");
        }
        return instance;
    }

    @NonNull
    public KeyPairModel generateAsymmetricKeyPair(
            @NonNull PrivateKeyConfig privateKeyConfig,
            @Nullable String seed
    ) throws AwesomeNotificationsException {
        try {
            KeyPairGenerator generator = KeyPairGenerator
                    .getInstance(privateKeyConfig.asymmetricAlgorithm.getAlgorithmName());

            if (seed != null) {
                // Initialize a SecureRandom instance with the seed.
                SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
                secureRandom.setSeed(seed.getBytes());
                generator.initialize(privateKeyConfig.privateKeySize, secureRandom);
            } else {
                generator.initialize(privateKeyConfig.privateKeySize);
            }

            KeyPair keyPair = generator.generateKeyPair();
            return new KeyPairModel(
                    PrivateKeyPem.fromSecurityKey(
                            privateKeyConfig.keyReference,
                            keyPair.getPrivate()
                    ),
                    PublicKeyPem.fromSecurityKey(
                            privateKeyConfig.keyReference,
                            keyPair.getPublic()
                    )
            );
        } catch (Error e) {
            throw ExceptionFactory
                    .getInstance()
                    .createNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_EVENT_EXCEPTION,
                            ExceptionCode.DETAILED_UNEXPECTED_ERROR + ".encryptTextWithSymmetricSecretKey",
                            e);
        } catch (Exception e) {
            throw ExceptionFactory
                    .getInstance()
                    .createNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_EVENT_EXCEPTION,
                            "Asymmetric key failed to generate: " + e.getMessage(),
                            ExceptionCode.DETAILED_UNEXPECTED_ERROR + ".encryptTextWithSymmetricSecretKey",
                            e);
        }
    }

    @Nullable
    public String encryptTextWithSymmetricRef(
            @NonNull String symmetricConfigReference,
            @NonNull String base64Secret,
            @NonNull String textToEncrypt
    ) throws AwesomeNotificationsException {
        SymmetricSecretConfig secretConfig =symmetricKeyService
                .readSymmetricSecretConfig(symmetricConfigReference);
        if (secretConfig == null)
            return null;
        return encryptTextWithSymmetricConfig(
                secretConfig,
                base64Secret,
                textToEncrypt
        );
    }

    @NonNull
    public String encryptTextWithSymmetricConfig(
            @NonNull SymmetricSecretConfig secretConfig,
            @NonNull String base64Secret,
            @NonNull String textToEncrypt
    ) throws AwesomeNotificationsException {
        SecretKey secretKey = symmetricKeyService
                .extractSecretKeySpecFromBase64Secret(secretConfig, base64Secret);
        return encryptTextWithSymmetricSecretKey(
                secretConfig.symmetricAlgorithm,
                secretKey,
                textToEncrypt
        );
    }

    @NonNull
    private String encryptTextWithSymmetricSecretKey(
            @NonNull SymmetricAlgorithm algorithm,
            @NonNull SecretKey secret,
            @NonNull String text
    ) throws AwesomeNotificationsException {
        try {
            Cipher cipher = Cipher.getInstance(algorithm.getCipherTransformation());
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            byte[] encryptedData = cipher.doFinal(text.getBytes());
            return Base64.encodeToString(encryptedData, Base64.DEFAULT);
        } catch (Exception e) {
            throw ExceptionFactory
                    .getInstance()
                    .createNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_EVENT_EXCEPTION,
                            "Text failed to be encrypted: " + e.getMessage(),
                            ExceptionCode.DETAILED_UNEXPECTED_ERROR + ".encryptTextWithSymmetricSecretKey",
                            e);
        }
    }

    @Nullable
    public String decryptTextWithSymmetricRef(
            @NonNull String symmetricConfigReference,
            @NonNull String base64Secret,
            @NonNull String encryptedText
    ) throws AwesomeNotificationsException {
        SymmetricSecretConfig secretConfig = symmetricKeyService
                .readSymmetricSecretConfig(symmetricConfigReference);
        if (secretConfig == null)
            return null;
        return decryptTextWithSymmetricConfig(
                secretConfig,
                base64Secret,
                encryptedText
        );
    }

    @NonNull
    public String decryptTextWithSymmetricConfig(
            @NonNull SymmetricSecretConfig secretConfig,
            @NonNull String base64Secret,
            @NonNull String encryptedText
    ) throws AwesomeNotificationsException {
        SecretKey secretKey = symmetricKeyService
                .extractSecretKeySpecFromBase64Secret(secretConfig, base64Secret);
        return decryptTextWithSymmetricSecretKey(
                secretConfig.symmetricAlgorithm,
                secretKey,
                encryptedText
        );
    }

    @NonNull
    private String decryptTextWithSymmetricSecretKey(
            @NonNull SymmetricAlgorithm algorithm,
            @NonNull SecretKey secret,
            @NonNull String encryptedText
    ) throws AwesomeNotificationsException {
        try {
            byte[] decodedData = Base64.decode(encryptedText, Base64.DEFAULT);
            Cipher cipher = Cipher.getInstance(algorithm.getCipherTransformation());
            cipher.init(Cipher.DECRYPT_MODE, secret);
            byte[] decryptedData = cipher.doFinal(decodedData);
            return new String(decryptedData);
        } catch (Exception e) {
            throw ExceptionFactory
                    .getInstance()
                    .createNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_EVENT_EXCEPTION,
                            "Text failed to be decrypted: " + e.getMessage(),
                            ExceptionCode.DETAILED_UNEXPECTED_ERROR + ".decryptTextWithSymmetricSecretKey",
                            e);
        }
    }

    @Nullable
    public String encryptTextWithAsymmetricRef(
            @NonNull String publicConfigRef,
            @NonNull String publicPemRef,
            @NonNull String textToEncrypt
    ) throws AwesomeNotificationsException {
        PublicKeyConfig publicKeyConfig = publicKeyService
                .readPublicKeyConfig(publicConfigRef);
        if (publicKeyConfig == null)
            throw ExceptionFactory
                    .getInstance()
                    .createNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_INVALID_ARGUMENTS,
                            "Public key configuration not found: "+publicConfigRef,
                            ExceptionCode.DETAILED_INVALID_ARGUMENTS+".encryptTextWithAsymmetricRef");

        PublicKeyPem publicKeyPem = publicKeyService
                .readPublicKeyPem(publicPemRef);
        if (publicKeyPem == null)
            throw ExceptionFactory
                    .getInstance()
                    .createNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_INVALID_ARGUMENTS,
                            "Public key pem not found: "+publicPemRef,
                            ExceptionCode.DETAILED_INVALID_ARGUMENTS+".encryptTextWithAsymmetricRef");

        return encryptTextWithAsymmetricKey(
                publicKeyConfig,
                publicKeyPem,
                textToEncrypt
        );
    }

    // Encrypt text using RSA public key
    @Nullable
    public String encryptTextWithAsymmetricKey(
            @NonNull PublicKeyConfig publicKeyConfig,
            @NonNull PublicKeyPem publicKeyPem,
            @NonNull String textToEncrypt
    ) throws AwesomeNotificationsException {
        try {
            PublicKey publicKey = publicKeyService.extractPublicKey(publicKeyConfig, publicKeyPem);
            Cipher cipher = Cipher.getInstance(publicKeyConfig.asymmetricAlgorithm.getAlgorithmName());
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedBytes = cipher.doFinal(textToEncrypt.getBytes());
            return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
        } catch (Exception e) {
            throw ExceptionFactory
                    .getInstance()
                    .createNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_INVALID_ARGUMENTS,
                            "text failed to be encrypted",
                            ExceptionCode.DETAILED_INVALID_ARGUMENTS+".encryptTextWithAsymmetric",
                            e);
        }
    }

    @Nullable
    public String decryptTextWithAsymmetricRef(
            @NonNull String privateConfigRef,
            @NonNull String privatePemRef,
            @NonNull String encryptedText
    ) throws AwesomeNotificationsException {
        PrivateKeyConfig privateKeyConfig = privateKeyService
                .readPrivateKeyConfig(privateConfigRef);
        if (privateKeyConfig == null)
            throw ExceptionFactory
                    .getInstance()
                    .createNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_INVALID_ARGUMENTS,
                            "Private key configuration not found: "+privateConfigRef,
                            ExceptionCode.DETAILED_INVALID_ARGUMENTS+".decryptTextWithAsymmetricRef");

        PrivateKeyPem privateKeyPem = privateKeyService.readPrivateKeyPem(privatePemRef);
        if (privateKeyPem == null)
            throw ExceptionFactory
                    .getInstance()
                    .createNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_INVALID_ARGUMENTS,
                            "Private key pem not found: "+privatePemRef,
                            ExceptionCode.DETAILED_INVALID_ARGUMENTS+".decryptTextWithAsymmetricRef");

        return decryptTextWithAsymmetricKey(
                privateKeyConfig,
                privateKeyPem,
                encryptedText
        );
    }

    @Nullable
    public String decryptTextWithAsymmetricKey(
            @NonNull PrivateKeyConfig privateKeyConfig,
            @NonNull PrivateKeyPem privateKeyPem,
            @NonNull String encryptedText
    ) throws AwesomeNotificationsException {
        try {
            byte[] encryptedBytes = Base64.decode(encryptedText, Base64.DEFAULT);
            Cipher cipher = Cipher.getInstance(privateKeyConfig.asymmetricAlgorithm.getAlgorithmName());
            PrivateKey privateKey = privateKeyService.extractPrivateKey(privateKeyConfig, privateKeyPem);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw ExceptionFactory
                    .getInstance()
                    .createNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_INVALID_ARGUMENTS,
                            "text failed to be decrypted",
                            ExceptionCode.DETAILED_INVALID_ARGUMENTS+".decryptTextWithAsymmetricKey",
                            e);
        }
    }

    @Nullable
    public NotificationEncryptedContentModel encryptNotificationContent(
            @NonNull NotificationDecryptedContentModel decryptedContent
    ) throws AwesomeNotificationsException {
        if (decryptedContent.secret == null) {
            throw ExceptionFactory
                    .getInstance()
                    .createNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_INVALID_ARGUMENTS,
                            "Decrypted content doesn't contain a secret",
                            ExceptionCode.DETAILED_INVALID_ARGUMENTS + ".encryptNotificationContent");
        }

        final PublicKeyConfig publicKeyConfig = publicKeyService
                .readPublicKeyConfig(decryptedContent.keyReference);
        if (publicKeyConfig == null) {
            throw ExceptionFactory
                    .getInstance()
                    .createNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_INVALID_ARGUMENTS,
                            "Private key config not found for \"" + decryptedContent.keyReference + "\"",
                            ExceptionCode.DETAILED_INVALID_ARGUMENTS + ".encryptNotificationContent");
        }

        final PublicKeyPem publicKeyPem = publicKeyService
                .readPublicKeyPem(decryptedContent.pemReference);
        if (publicKeyPem == null) {
            throw ExceptionFactory
                    .getInstance()
                    .createNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_INVALID_ARGUMENTS,
                            "Private key config not found for \"" + decryptedContent.keyReference + "\"",
                            ExceptionCode.DETAILED_INVALID_ARGUMENTS + ".encryptNotificationContent");
        }

        final SymmetricSecretConfig secretConfig = symmetricKeyService
                .readSymmetricSecretConfig(decryptedContent.secretReference);
        if (secretConfig == null) {
            throw ExceptionFactory
                    .getInstance()
                    .createNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_INVALID_ARGUMENTS,
                            "Symmetric secret config not found for \"" + decryptedContent.secretReference + "\"",
                            ExceptionCode.DETAILED_INVALID_ARGUMENTS + ".encryptNotificationContent");
        }

        return encryptNotificationContentWithConfigs(
                decryptedContent,
                secretConfig,
                publicKeyConfig,
                publicKeyPem
        );
    }

    @Nullable
    public NotificationEncryptedContentModel encryptNotificationContentWithConfigs(
            @NonNull NotificationDecryptedContentModel decryptedContent,
            @NonNull SymmetricSecretConfig secretConfig,
            @NonNull PublicKeyConfig publicKeyConfig,
            @NonNull PublicKeyPem publicKeyPem
    ) throws AwesomeNotificationsException {
        if (decryptedContent.secret == null) {
            throw ExceptionFactory
                    .getInstance()
                    .createNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_INVALID_ARGUMENTS,
                            "Decrypted content doesn't contain a secret",
                            ExceptionCode.DETAILED_INVALID_ARGUMENTS + ".encryptNotificationContent");
        }

        final NotificationEncryptedContentModel encrypted =
                new NotificationEncryptedContentModel(decryptedContent.keyReference);

        final SecretKey secretKey = symmetricKeyService
                .extractSecretKeySpecFromBase64Secret(secretConfig, encrypted.base64Secret);

        encrypted.base64Secret = EncryptionService
                .getInstance()
                .encryptTextWithAsymmetricKey(
                        publicKeyConfig,
                        publicKeyPem,
                        decryptedContent.secret
                );

        if (decryptedContent.title != null) {
            encrypted.title = encryptTextWithSymmetricSecretKey(
                    secretConfig.symmetricAlgorithm,
                    secretKey,
                    decryptedContent.title
            );
        }

        if (decryptedContent.body != null) {
            encrypted.body = encryptTextWithSymmetricSecretKey(
                    secretConfig.symmetricAlgorithm,
                    secretKey,
                    decryptedContent.body
            );
        }

        if (decryptedContent.summary != null) {
            encrypted.summary = encryptTextWithSymmetricSecretKey(
                    secretConfig.symmetricAlgorithm,
                    secretKey,
                    decryptedContent.summary
            );
        }

        if (decryptedContent.bigPicture != null) {
            encrypted.bigPicture = encryptTextWithSymmetricSecretKey(
                    secretConfig.symmetricAlgorithm,
                    secretKey,
                    decryptedContent.bigPicture
            );
        }

        if (decryptedContent.largeIcon != null) {
            encrypted.largeIcon = encryptTextWithSymmetricSecretKey(
                    secretConfig.symmetricAlgorithm,
                    secretKey,
                    decryptedContent.largeIcon
            );
        }

        if (decryptedContent.payload != null) {
            encrypted.payload = encryptTextWithSymmetricSecretKey(
                    secretConfig.symmetricAlgorithm,
                    secretKey,
                    JsonUtils.toJson(decryptedContent.payload)
            );
        }

        return encrypted;
    }

    @Nullable
    public NotificationDecryptedContentModel decryptVisualProtectedContent(
            @NonNull NotificationEncryptedContentModel encryptedContent
    ) throws AwesomeNotificationsException {
        final PrivateKeyConfig privateKeyConfig = privateKeyService
                .readPrivateKeyConfig(encryptedContent.keyReference);
        if (privateKeyConfig == null) {
            throw ExceptionFactory
                    .getInstance()
                    .createNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_INVALID_ARGUMENTS,
                            "Private key config not found for \"" + encryptedContent.keyReference + "\"",
                            ExceptionCode.DETAILED_INVALID_ARGUMENTS + ".decryptVisualProtectedContent");
        }

        final PrivateKeyPem privateKeyPem = privateKeyService
                .readPrivateKeyPem(encryptedContent.pemReference);
        if (privateKeyPem == null) {
            throw ExceptionFactory
                    .getInstance()
                    .createNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_INVALID_ARGUMENTS,
                            "Private pem not found for \"" + encryptedContent.pemReference + "\"",
                            ExceptionCode.DETAILED_INVALID_ARGUMENTS + ".decryptVisualProtectedContent");
        }

        final SymmetricSecretConfig secretConfig = symmetricKeyService
                .readSymmetricSecretConfig(encryptedContent.secretReference);
        if (secretConfig == null) {
            throw ExceptionFactory
                    .getInstance()
                    .createNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_INVALID_ARGUMENTS,
                            "Symmetric secret config not found for \"" + encryptedContent.secretReference + "\"",
                            ExceptionCode.DETAILED_INVALID_ARGUMENTS + ".decryptVisualProtectedContent");
        }

        return decryptVisualProtectedContentWithConfigs(
                encryptedContent,
                secretConfig,
                privateKeyConfig,
                privateKeyPem
        );
    }

    @Nullable
    public NotificationDecryptedContentModel decryptVisualProtectedContentWithConfigs(
            @NonNull NotificationEncryptedContentModel encryptedContent,
            @NonNull SymmetricSecretConfig secretConfig,
            @NonNull PrivateKeyConfig privateKeyConfig,
            @NonNull PrivateKeyPem privateKeyPem
    ) throws AwesomeNotificationsException {
        if (encryptedContent.base64Secret == null) {
            throw ExceptionFactory
                    .getInstance()
                    .createNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_INVALID_ARGUMENTS,
                            "Encrypted content doesn't contain a base64Secret",
                            ExceptionCode.DETAILED_INVALID_ARGUMENTS + ".decryptVisualProtectedContent");
        }

        final NotificationDecryptedContentModel decrypted =
                new NotificationDecryptedContentModel(encryptedContent.keyReference);

        decrypted.secret = EncryptionService
                .getInstance()
                .decryptTextWithAsymmetricKey(
                        privateKeyConfig,
                        privateKeyPem,
                        encryptedContent.base64Secret
                );
        if (decrypted.secret == null) {
            throw ExceptionFactory
                    .getInstance()
                    .createNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_INVALID_ARGUMENTS,
                            "The original secret couldn't be recovered",
                            ExceptionCode.DETAILED_INVALID_ARGUMENTS + ".encryptNotificationContent");
        }

        final SecretKey secretKey = symmetricKeyService
                .extractSecretKeySpecFromBase64Secret(
                        secretConfig,
                        decrypted.secret
                );

        if (encryptedContent.title != null) {
            decrypted.title = decryptSymmetricValue(
                    encryptedContent.title,
                    secretConfig.symmetricAlgorithm,
                    secretKey,
                    String.class
            );
        }

        if (encryptedContent.body != null) {
            decrypted.body = decryptSymmetricValue(
                    encryptedContent.body,
                    secretConfig.symmetricAlgorithm,
                    secretKey,
                    String.class
            );
        }

        if (encryptedContent.summary != null) {
            decrypted.summary = decryptSymmetricValue(
                    encryptedContent.summary,
                    secretConfig.symmetricAlgorithm,
                    secretKey,
                    String.class
            );
        }

        if (encryptedContent.bigPicture != null) {
            decrypted.bigPicture = decryptSymmetricValue(
                    encryptedContent.bigPicture,
                    secretConfig.symmetricAlgorithm,
                    secretKey,
                    String.class
            );
        }

        if (encryptedContent.largeIcon != null) {
            decrypted.largeIcon = decryptSymmetricValue(
                    encryptedContent.largeIcon,
                    secretConfig.symmetricAlgorithm,
                    secretKey,
                    String.class
            );
        }

        return decrypted;
    }

    @Nullable
    public NotificationDecryptedContentModel decryptPayloadContent(
            @NonNull NotificationEncryptedContentModel encryptedContent,
            @Nullable NotificationDecryptedContentModel decryptedContent
    ) throws AwesomeNotificationsException {
        if (encryptedContent.payload == null) return null;
        if (encryptedContent.base64Secret == null) {
            throw ExceptionFactory
                    .getInstance()
                    .createNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_INVALID_ARGUMENTS,
                            "Encrypted content doesn't contain a secret",
                            ExceptionCode.DETAILED_INVALID_ARGUMENTS + ".encryptNotificationContent");
        }

        if (decryptedContent != null) {
            if (decryptedContent.payload != null) return null;
        }

        PrivateKeyConfig privateKeyConfig = privateKeyService
                    .readPrivateKeyConfig(encryptedContent.keyReference);
        if (privateKeyConfig == null) {
            throw ExceptionFactory
                    .getInstance()
                    .createNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_INVALID_ARGUMENTS,
                            "Private key config not found for \"" + encryptedContent.keyReference + "\"",
                            ExceptionCode.DETAILED_INVALID_ARGUMENTS + ".decryptVisualProtectedContent");
        }

        PrivateKeyPem privateKeyPem = privateKeyService
                .readPrivateKeyPem(encryptedContent.pemReference);
        if (privateKeyPem == null) {
            throw ExceptionFactory
                    .getInstance()
                    .createNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_INVALID_ARGUMENTS,
                            "Private pem not found for \"" + encryptedContent.pemReference + "\"",
                            ExceptionCode.DETAILED_INVALID_ARGUMENTS + ".decryptVisualProtectedContent");
        }

        final SymmetricSecretConfig secretConfig = symmetricKeyService
                .readSymmetricSecretConfig(encryptedContent.secretReference);
        if (secretConfig == null) {
            throw ExceptionFactory
                    .getInstance()
                    .createNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_INVALID_ARGUMENTS,
                            "Symmetric secret config not found for \"" + encryptedContent.secretReference + "\"",
                            ExceptionCode.DETAILED_INVALID_ARGUMENTS + ".decryptVisualProtectedContent");
        }

        return decryptPayloadContentWithConfigs(
                encryptedContent,
                decryptedContent,
                secretConfig,
                privateKeyConfig,
                privateKeyPem
        );
    }

    @Nullable
    public NotificationDecryptedContentModel decryptPayloadContentWithConfigs(
            @NonNull NotificationEncryptedContentModel encryptedContent,
            @Nullable NotificationDecryptedContentModel decryptedContent,
            @NonNull SymmetricSecretConfig secretConfig,
            @NonNull PrivateKeyConfig privateKeyConfig,
            @NonNull PrivateKeyPem privateKeyPem
    ) throws AwesomeNotificationsException {
        if (encryptedContent.payload == null) return null;
        if (encryptedContent.base64Secret == null) {
            throw ExceptionFactory
                    .getInstance()
                    .createNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_INVALID_ARGUMENTS,
                            "Encrypted content doesn't contain a secret",
                            ExceptionCode.DETAILED_INVALID_ARGUMENTS + ".encryptNotificationContent");
        }

        if (decryptedContent != null) {
            if (decryptedContent.payload != null) return null;
        }

        final NotificationDecryptedContentModel decrypted = decryptedContent != null
                ? decryptedContent
                : new NotificationDecryptedContentModel(encryptedContent.keyReference);

        decrypted.secret = decryptTextWithAsymmetricKey(
                privateKeyConfig,
                privateKeyPem,
                encryptedContent.base64Secret
        );
        if (decrypted.secret == null) {
            throw ExceptionFactory
                    .getInstance()
                    .createNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_INVALID_ARGUMENTS,
                            "The original secret couldn't be recovered",
                            ExceptionCode.DETAILED_INVALID_ARGUMENTS + ".encryptNotificationContent");
        }

        final SecretKey secretKey = symmetricKeyService
                .extractSecretKeySpecFromBase64Secret(
                        secretConfig,
                        decrypted.secret
                );

        decrypted.payload = decryptSymmetricValue(
                encryptedContent.payload,
                secretConfig.symmetricAlgorithm,
                secretKey,
                Map.class
        );

        return decrypted;
    }

    @Nullable
    private static<T> T decryptSymmetricValue(
            @NonNull String encryptedValue,
            @NonNull SymmetricAlgorithm algorithm,
            @NonNull SecretKey secret,
            @NonNull Class<T> typeClass
    ) throws AwesomeNotificationsException {
        try {
            final String decryptedText = EncryptionService
                    .getInstance()
                    .decryptTextWithSymmetricSecretKey(
                            algorithm,
                            secret,
                            encryptedValue
                    );

            if (typeClass == String.class) {
                return typeClass.cast(decryptedText);
            } else if (Map.class.isAssignableFrom(typeClass)) {
                Map<?, ?> rawMap = JsonUtils.fromJson(decryptedText);
                if (rawMap == null) return null;

                Map<String, String> checkedMap = new HashMap<>();
                for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
                    if (entry.getKey() instanceof String && entry.getValue() instanceof String) {
                        checkedMap.put((String) entry.getKey(), (String) entry.getValue());
                    } else {
                        return null;
                    }
                }
                return typeClass.cast(checkedMap);
            }
        } catch (Exception e) {
            throw ExceptionFactory
                    .getInstance()
                    .createNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_INVALID_ARGUMENTS,
                            "Decryption text has failed: " + e.getMessage(),
                            ExceptionCode.DETAILED_INVALID_ARGUMENTS + ".decryptSymmetricText",
                            e);
        }
        return null;
    }
}