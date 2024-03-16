package me.carda.awesome_notifications.core.models.keys;

import android.util.Base64;

import androidx.annotation.NonNull;

import java.security.PrivateKey;
import java.util.Map;

import me.carda.awesome_notifications.core.Definitions;
import me.carda.awesome_notifications.core.models.attributes.AnemicAttribute;

public class PrivateKeyPem extends AnemicAttribute<String> {
    public final String keyReference;
    public PrivateKeyPem(
            @NonNull String configReference,
            @NonNull String value
    ) {
        super(value);
        this.keyReference = configReference;
    }

    @NonNull
    public static PrivateKeyPem fromSecurityKey(
            @NonNull String configReference,
            @NonNull PrivateKey privateKey
    ){
        return new PrivateKeyPem(
                configReference,
                Base64.encodeToString(privateKey.getEncoded(), Base64.DEFAULT)
        );
    }

    @NonNull
    public static PrivateKeyPem fromMap(
            @NonNull Map<String, Object> arguments
    ){
        String keyReference = (String) arguments.get(Definitions.ENCRYPT_KEY_REFERENCE);
        String value = (String) arguments.get(Definitions.ENCRYPT_VALUE);

        assert keyReference != null;
        assert value != null;

        return new PrivateKeyPem(keyReference, value);
    }
}
