package me.carda.awesome_notifications.core.enumerators;


import androidx.annotation.NonNull;

public enum AsymmetricAlgorithm implements SafeEnum {
    rsaEcbNoPadding("rsaEcbNoPadding", "RSA", "RSA/ECB/NoPadding"),
    rsaEcbPkcs1("rsaEcbPkcs1", "RSA", "RSA/ECB/PKCS1Padding"),
    rsaEcbOaepSha1("rsaEcbOaepSha1", "RSA", "RSA/ECB/OAEPWithSHA-1AndMGF1Padding"),
    rsaEcbOaepSha256("rsaEcbOaepSha256", "RSA", "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"),
    rsaEcbOaepSha512("rsaEcbOaepSha512", "RSA", "RSA/ECB/OAEPWithSHA-512AndMGF1Padding");

    private final String algorithmName;
    private final String cipherTransformation;
    private final String safeName;

    AsymmetricAlgorithm(
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

    public static AsymmetricAlgorithm fromSafeName(@NonNull String name) {
        for (AsymmetricAlgorithm alg : AsymmetricAlgorithm.values()) {
            if (alg.name().equalsIgnoreCase(name)) {
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
