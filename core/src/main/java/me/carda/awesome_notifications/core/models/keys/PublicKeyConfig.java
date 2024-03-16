package me.carda.awesome_notifications.core.models.keys;

import android.content.Context;
import android.util.Base64;

import androidx.annotation.NonNull;

import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

import me.carda.awesome_notifications.core.enumerators.AsymmetricAlgorithm;
import me.carda.awesome_notifications.core.enumerators.SymmetricAlgorithm;
import me.carda.awesome_notifications.core.exceptions.AwesomeNotificationsException;

public class PublicKeyConfig extends AsymmetricKeyConfig {

    public PublicKeyConfig(){
        super();
    }

    public PublicKeyConfig(
            @NonNull String keyReference,
            @NonNull AsymmetricAlgorithm asymmetricAlgorithm
    ){
        super(
            keyReference,
            1024,
            asymmetricAlgorithm
        );
    }

    @Override
    public PublicKeyConfig fromMap(@NonNull Map<String, Object> arguments) {
        return (PublicKeyConfig) super.fromMap(arguments);
    }

    @Override
    public void validate(Context context) throws AwesomeNotificationsException {

    }
}
