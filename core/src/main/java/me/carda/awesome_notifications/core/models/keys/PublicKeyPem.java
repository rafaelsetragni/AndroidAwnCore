package me.carda.awesome_notifications.core.models.keys;

import android.util.Base64;

import androidx.annotation.NonNull;

import java.security.PublicKey;
import java.util.Map;

import me.carda.awesome_notifications.core.Definitions;
import me.carda.awesome_notifications.core.models.attributes.AnemicAttribute;

public class PublicKeyPem extends AnemicAttribute<String> {
    public final String keyReference;
    public PublicKeyPem(
            @NonNull String keyReference,
            @NonNull String value
    ) {
        super(value);
        this.keyReference = keyReference;
    }

    @NonNull
    public static PublicKeyPem fromSecurityKey(
            @NonNull String configReference,
            @NonNull PublicKey publicKey
    ){
        return new PublicKeyPem(
                configReference,
                Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT)
        );
    }

    @NonNull
    public static PublicKeyPem fromMap(
            @NonNull Map<String, Object> arguments
    ){
        String keyReference = (String) arguments.get(Definitions.ENCRYPT_KEY_REFERENCE);
        String value = (String) arguments.get(Definitions.ENCRYPT_VALUE);

        assert keyReference != null;
        assert value != null;

        return new PublicKeyPem(keyReference, value);
    }
}
