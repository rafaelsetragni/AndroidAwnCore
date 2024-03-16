package me.carda.awesome_notifications.core.models.keys;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import me.carda.awesome_notifications.core.Definitions;
import me.carda.awesome_notifications.core.enumerators.KeyDerivationAlgorithm;
import me.carda.awesome_notifications.core.enumerators.SymmetricAlgorithm;
import me.carda.awesome_notifications.core.exceptions.AwesomeNotificationsException;
import me.carda.awesome_notifications.core.models.AbstractModel;

public class SymmetricSecretConfig extends AbstractModel {
    public String keyReference;
    public SymmetricAlgorithm symmetricAlgorithm;
    public KeyDerivationAlgorithm keyDerivationAlgorithm;
    public Integer secretLength;
    public Integer saltLength;
    public Integer keyBytesCount;
    public Integer digestIterations;

    public SymmetricSecretConfig(){}

    public SymmetricSecretConfig(
            @NonNull String keyReference,
            @NonNull SymmetricAlgorithm symmetricAlgorithm,
            @NonNull KeyDerivationAlgorithm keyDerivationAlgorithm,
            @NonNull Integer secretLength,
            @NonNull Integer saltLength,
            @NonNull Integer keyBytesCount,
            @NonNull Integer digestIterations
    ){
        this.keyReference = keyReference;
        this.secretLength = secretLength;
        this.saltLength = saltLength;
        this.keyBytesCount = keyBytesCount;
        this.digestIterations = digestIterations;
        this.symmetricAlgorithm = symmetricAlgorithm;
        this.keyDerivationAlgorithm = keyDerivationAlgorithm;
    }

    @Override
    public SymmetricSecretConfig fromMap(Map<String, Object> arguments) {
        keyReference = getValueOrDefault(arguments, Definitions.ENCRYPT_KEY_REFERENCE, String.class, null);
        secretLength = getValueOrDefault(arguments, Definitions.ENCRYPT_SECRET_LENGTH, Integer.class, null);
        saltLength = getValueOrDefault(arguments, Definitions.ENCRYPT_SALT_LENGTH, Integer.class, null);
        keyBytesCount = getValueOrDefault(arguments, Definitions.ENCRYPT_KEY_BYTES_COUNT, Integer.class, null);
        digestIterations = getValueOrDefault(arguments, Definitions.ENCRYPT_DIGEST_ITERATIONS, Integer.class, null);
        String symmetricName = getValueOrDefault(arguments, Definitions.ENCRYPT_SYMMETRIC_ALGORITHM, String.class, null);
        assert symmetricName != null;
        symmetricAlgorithm = SymmetricAlgorithm.fromSafeName(symmetricName);
        return this;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> responseObject = new HashMap<>();
        putDataOnSerializedMap(Definitions.ENCRYPT_KEY_REFERENCE, responseObject, this.keyReference);
        putDataOnSerializedMap(Definitions.ENCRYPT_SECRET_LENGTH, responseObject, this.secretLength);
        putDataOnSerializedMap(Definitions.ENCRYPT_SALT_LENGTH, responseObject, this.saltLength);
        putDataOnSerializedMap(Definitions.ENCRYPT_KEY_BYTES_COUNT, responseObject, this.keyBytesCount);
        putDataOnSerializedMap(Definitions.ENCRYPT_DIGEST_ITERATIONS, responseObject, this.digestIterations);
        putDataOnSerializedMap(Definitions.ENCRYPT_SYMMETRIC_ALGORITHM, responseObject, this.symmetricAlgorithm.getSafeName());
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

    @Override
    public void validate(Context context) throws AwesomeNotificationsException {

    }
}
