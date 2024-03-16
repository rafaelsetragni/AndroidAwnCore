package me.carda.awesome_notifications.core.models.keys;

import android.content.Context;
import android.util.Base64;

import androidx.annotation.NonNull;

import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.util.Map;

import me.carda.awesome_notifications.core.enumerators.AsymmetricAlgorithm;
import me.carda.awesome_notifications.core.enumerators.SymmetricAlgorithm;
import me.carda.awesome_notifications.core.exceptions.AwesomeNotificationsException;

public class PrivateKeyConfig extends AsymmetricKeyConfig {

    public PrivateKeyConfig() {
        super();
    }

    public PrivateKeyConfig(
            @NonNull String keyReference,
            @NonNull Integer privateKeySize,
            @NonNull AsymmetricAlgorithm asymmetricAlgorithm
    ){
        super(
            keyReference,
            privateKeySize,
            asymmetricAlgorithm
        );
    }

    @NonNull
    public static PrivateKeyConfig fromSecurityKey(
            @NonNull String keyReference,
            @NonNull PrivateKey privateKey,
            @NonNull AsymmetricAlgorithm asymmetricAlgorithm
    ){
        if (privateKey instanceof RSAPrivateKey) {
            RSAPrivateKey rsaKey = (RSAPrivateKey) privateKey;
            return new PrivateKeyConfig(
                    keyReference,
                    rsaKey.getModulus().bitLength(),
                    asymmetricAlgorithm
            );
        } else {
            throw new IllegalArgumentException("Unsupported key type: " + privateKey.getClass().getName());
        }
    }

    @Override
    public PrivateKeyConfig fromMap(@NonNull Map<String, Object> arguments) {
        return (PrivateKeyConfig) super.fromMap(arguments);
    }

    @Override
    public void validate(Context context) throws AwesomeNotificationsException {

    }
}

