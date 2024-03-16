package me.carda.awesome_notifications.core.enumerators;

import androidx.annotation.NonNull;

public enum KeyDerivationAlgorithm implements SafeEnum {
    pbkdf2WithHmacSHA256("pbkdf2WithHmacSHA256", "PBKDF2WithHmacSHA256"),
    pbkdf2WithHmacSHA512("pbkdf2WithHmacSHA512", "PBKDF2WithHmacSHA512");

    private final String safeName;
    private final String algorithmName;

    KeyDerivationAlgorithm(String safeName, String algorithmName) {
        this.safeName = safeName;
        this.algorithmName = algorithmName;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public static KeyDerivationAlgorithm fromSafeName(@NonNull String name) {
        for (KeyDerivationAlgorithm alg : KeyDerivationAlgorithm.values()) {
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
