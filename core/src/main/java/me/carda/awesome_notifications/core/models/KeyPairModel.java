package me.carda.awesome_notifications.core.models;

import static me.carda.awesome_notifications.core.Definitions.NOTIFICATION_PRIVATE_KEY;
import static me.carda.awesome_notifications.core.Definitions.NOTIFICATION_PUBLIC_KEY;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import me.carda.awesome_notifications.core.Definitions;
import me.carda.awesome_notifications.core.exceptions.AwesomeNotificationsException;
import me.carda.awesome_notifications.core.models.keys.PrivateKeyPem;
import me.carda.awesome_notifications.core.models.keys.PublicKeyPem;

public class KeyPairModel extends AbstractModel {
    public String keyReference;
    public PrivateKeyPem privateKeyPem;
    public PublicKeyPem publicKeyPem;

    public KeyPairModel(
        @NonNull PrivateKeyPem privateKeyPem,
        @NonNull PublicKeyPem publicKeyPem
    ){
        assert Objects.equals(privateKeyPem.keyReference, publicKeyPem.keyReference);
        this.keyReference = privateKeyPem.keyReference;
        this.privateKeyPem = privateKeyPem;
        this.publicKeyPem = publicKeyPem;
    }

    @Override
    public AbstractModel fromMap(Map<String, Object> arguments) {
        keyReference = getValueOrDefault(
                arguments,
                Definitions.ENCRYPT_KEY_REFERENCE,
                String.class,
                null
        );
        assert keyReference != null;
        privateKeyPem = new PrivateKeyPem(
                keyReference,
                Objects.requireNonNull(getValueOrDefault(
                        arguments,
                        NOTIFICATION_PRIVATE_KEY,
                        String.class,
                        null
                ))
        );
        publicKeyPem = new PublicKeyPem(
                keyReference,
                Objects.requireNonNull(getValueOrDefault(
                        arguments,
                        NOTIFICATION_PUBLIC_KEY,
                        String.class,
                        null
                ))
        );
        return this;
    }

    @Override
    public Map<String, Object> toMap() {
        return new HashMap<String, Object>(){{
            put(NOTIFICATION_PRIVATE_KEY, privateKeyPem.getValue());
            put(NOTIFICATION_PUBLIC_KEY, publicKeyPem.getValue());
        }};
    }

    @Override
    public String toJson() {
        return templateToJson();
    }

    @Override
    public KeyPairModel fromJson(String json){
        return (KeyPairModel) super.templateFromJson(json);
    }

    @Override
    public void validate(Context context) throws AwesomeNotificationsException {

    }
}
