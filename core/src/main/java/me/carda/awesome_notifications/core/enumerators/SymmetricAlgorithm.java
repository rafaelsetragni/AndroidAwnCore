package me.carda.awesome_notifications.core.enumerators;


import androidx.annotation.NonNull;

public enum SymmetricAlgorithm implements SafeEnum {
    aesEcbNoPadding("aes", "AES", "AES/ECB/NoPadding"),
    aesGcmNoPadding("aes", "AES", "AES/GCM/NoPadding"),
    aesEcbPkcs7Padding("aes", "AES", "AES/ECB/PKCS7Padding"),
    aesCbcPkcs7Padding("aes", "AES", "AES/CBC/PKCS7Padding");

    private final String safeName;
    private final String algorithmName;
    private final String cipherTransformation;

    SymmetricAlgorithm(
            String safeName,
            String algorithmName,
            String cipherTransformation
    ) {
        this.safeName = safeName;
        this.algorithmName = algorithmName;
        this.cipherTransformation = cipherTransformation;
    }

    public String getCipherTransformation() {
        return cipherTransformation;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public static SymmetricAlgorithm fromSafeName(@NonNull String name) {
        for (SymmetricAlgorithm alg : SymmetricAlgorithm.values()) {
            if (alg.safeName.equalsIgnoreCase(name)) {
                return alg;
            }
        }
        throw new IllegalArgumentException("The algorithm " + name + " is not available");
    }

    @Override
    public String getSafeName() {
        return safeName;
    }
}
