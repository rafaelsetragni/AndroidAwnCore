package me.carda.awesome_notifications.core.models.keys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import me.carda.awesome_notifications.core.Definitions;
import me.carda.awesome_notifications.core.enumerators.AsymmetricAlgorithm;
import me.carda.awesome_notifications.core.models.AbstractModel;

public abstract class AsymmetricKeyConfig extends AbstractModel {
    static String TAG = "AsymmetricKeyModel";

    public String keyReference;
    public Integer privateKeySize;
    public AsymmetricAlgorithm asymmetricAlgorithm;

    public AsymmetricKeyConfig(
            @NonNull String keyReference,
            @NonNull Integer privateKeySize,
            @NonNull AsymmetricAlgorithm asymmetricAlgorithm
    ){
        this.keyReference = keyReference;
        this.privateKeySize = privateKeySize;
        this.asymmetricAlgorithm = asymmetricAlgorithm;
    }

    public AsymmetricKeyConfig() {}

    @Override
    public AsymmetricKeyConfig fromMap(Map<String, Object> arguments) {
        keyReference = getValueOrDefault(arguments, Definitions.ENCRYPT_KEY_REFERENCE, String.class, null);
        privateKeySize = getValueOrDefault(arguments, Definitions.ENCRYPT_PRIVATE_KEY_SIZE, Integer.class, 0);
        String asymmetricName = getValueOrDefault(arguments, Definitions.ENCRYPT_ASYMMETRIC_ALGORITHM, String.class, null);
        assert asymmetricName != null;
        asymmetricAlgorithm = AsymmetricAlgorithm.fromSafeName(asymmetricName);
        return this;
    }

    @NonNull
    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> responseObject = new HashMap<>();
        putDataOnSerializedMap(Definitions.ENCRYPT_KEY_REFERENCE, responseObject, this.keyReference);
        putDataOnSerializedMap(Definitions.ENCRYPT_PRIVATE_KEY_SIZE, responseObject, this.privateKeySize);
        putDataOnSerializedMap(Definitions.ENCRYPT_ASYMMETRIC_ALGORITHM, responseObject, this.asymmetricAlgorithm.getSafeName());
        return responseObject;
    }

    @Override
    public String toJson() {
        return templateToJson();
    }

    @Override
    public AsymmetricKeyConfig fromJson(@Nullable String json){
        return (AsymmetricKeyConfig) super.templateFromJson(json);
    }
}
