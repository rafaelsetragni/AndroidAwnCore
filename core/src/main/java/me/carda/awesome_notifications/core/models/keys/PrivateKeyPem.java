package me.carda.awesome_notifications.core.models.keys;

import static me.carda.awesome_notifications.core.Definitions.ENCRYPT_PEM_CONTENT;
import static me.carda.awesome_notifications.core.Definitions.ENCRYPT_PEM_REFERENCE;

import android.content.Context;
import android.util.Base64;

import androidx.annotation.NonNull;

import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;

import me.carda.awesome_notifications.core.Definitions;
import me.carda.awesome_notifications.core.exceptions.AwesomeNotificationsException;
import me.carda.awesome_notifications.core.models.AbstractModel;
import me.carda.awesome_notifications.core.models.KeyPairModel;

public class PrivateKeyPem extends AbstractModel {
    public String keyReference;
    public String penContent;
    public PrivateKeyPem(
            @NonNull String configReference,
            @NonNull String pemContent
    ) {
        this.penContent = pemContent;
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
    public PrivateKeyPem fromMap(
            @NonNull Map<String, Object> arguments
    ){
        keyReference = (String) arguments.get(Definitions.ENCRYPT_PEM_REFERENCE);
        penContent = (String) arguments.get(Definitions.ENCRYPT_PEM_CONTENT);
        return this;
    }

    @Override
    public Map<String, Object> toMap() {
        return new HashMap<String, Object>(){{
            put(ENCRYPT_PEM_REFERENCE, keyReference);
            put(ENCRYPT_PEM_CONTENT, penContent);
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
