package me.carda.awesome_notifications.core.models.keys;

import static me.carda.awesome_notifications.core.Definitions.ENCRYPT_PEM_CONTENT;
import static me.carda.awesome_notifications.core.Definitions.ENCRYPT_PEM_REFERENCE;

import android.content.Context;
import android.util.Base64;

import androidx.annotation.NonNull;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import me.carda.awesome_notifications.core.Definitions;
import me.carda.awesome_notifications.core.exceptions.AwesomeNotificationsException;
import me.carda.awesome_notifications.core.models.AbstractModel;
import me.carda.awesome_notifications.core.models.KeyPairModel;

public class PublicKeyPem extends AbstractModel {
    public String keyReference;
    public String pemContent;

    public PublicKeyPem() {}
    public PublicKeyPem(
            @NonNull String keyReference,
            @NonNull String pemContent
    ) {
        this.pemContent = pemContent;
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
    public PublicKeyPem fromMap(
            @NonNull Map<String, Object> arguments
    ){
        keyReference = (String) arguments.get(Definitions.ENCRYPT_PEM_REFERENCE);
        pemContent = (String) arguments.get(Definitions.ENCRYPT_PEM_CONTENT);
        return this;
    }

    @Override
    public Map<String, Object> toMap() {
        return new HashMap<String, Object>(){{
            put(ENCRYPT_PEM_REFERENCE, keyReference);
            put(ENCRYPT_PEM_CONTENT, pemContent);
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
