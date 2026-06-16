package me.carda.awesome_notifications.core.enumerators;

import android.content.pm.ServiceInfo;
import android.os.Build;

import java.util.Locale;

// https://developer.android.com/reference/android/content/pm/ServiceInfo#FOREGROUND_SERVICE_TYPE_CAMERA
public enum ForegroundServiceType implements SafeEnum {

    /// Corresponds to [`ServiceInfo.FOREGROUND_SERVICE_TYPE_NONE`](https://developer.android.com/reference/android/content/pm/ServiceInfo#FOREGROUND_SERVICE_TYPE_NONE).
    none("none"),
    /// Corresponds to [`ServiceInfo.FOREGROUND_SERVICE_TYPE_MANIFEST`](https://developer.android.com/reference/android/content/pm/ServiceInfo#FOREGROUND_SERVICE_TYPE_MANIFEST).
    manifest("manifest"),
    /// Corresponds to [`ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC`](https://developer.android.com/reference/android/content/pm/ServiceInfo#FOREGROUND_SERVICE_TYPE_DATA_SYNC).
    dataSync("dataSync"),
    /// Corresponds to [`ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK`](https://developer.android.com/reference/android/content/pm/ServiceInfo#FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK).
    mediaPlayback("mediaPlayback"),
    /// Corresponds to [`Service.START_REDELIVER_INTENT`](https://developer.android.com/reference/android/app/Service#START_REDELIVER_INTENT).
    redeliveryIntent("redeliveryIntent"),
    /// Corresponds to [`ServiceInfo.FOREGROUND_SERVICE_TYPE_PHONE_CALL`](https://developer.android.com/reference/android/content/pm/ServiceInfo#FOREGROUND_SERVICE_TYPE_PHONE_CALL).
    phoneCall("phoneCall"),
    /// Corresponds to [`ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE`](https://developer.android.com/reference/android/content/pm/ServiceInfo#FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE).
    connectedDevice("connectedDevice"),
    /// Corresponds to [`ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION`](https://developer.android.com/reference/android/content/pm/ServiceInfo#FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION).
    mediaProjection("mediaProjection"),
    /// Corresponds to [`ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION`](https://developer.android.com/reference/android/content/pm/ServiceInfo#FOREGROUND_SERVICE_TYPE_LOCATION).
    location("location"),
    /// Corresponds to [`ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA`](https://developer.android.com/reference/android/content/pm/ServiceInfo#FOREGROUND_SERVICE_TYPE_CAMERA).
    camera("camera"),
    /// Corresponds to [`ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE`](https://developer.android.com/reference/android/content/pm/ServiceInfo#FOREGROUND_SERVICE_TYPE_MICROPHONE).
    microphone("microphone"),
    /// Corresponds to [`ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH`](https://developer.android.com/reference/android/content/pm/ServiceInfo#FOREGROUND_SERVICE_TYPE_HEALTH).
    /// Added in API 34.
    health("health"),
    /// Corresponds to [`ServiceInfo.FOREGROUND_SERVICE_TYPE_REMOTE_MESSAGING`](https://developer.android.com/reference/android/content/pm/ServiceInfo#FOREGROUND_SERVICE_TYPE_REMOTE_MESSAGING).
    /// Added in API 34.
    remoteMessaging("remoteMessaging"),
    /// Corresponds to [`ServiceInfo.FOREGROUND_SERVICE_TYPE_SHORT_SERVICE`](https://developer.android.com/reference/android/content/pm/ServiceInfo#FOREGROUND_SERVICE_TYPE_SHORT_SERVICE).
    /// Added in API 34. Has a ~3 minute timeout via Service.onTimeout(int).
    shortService("shortService"),
    /// Corresponds to [`ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE`](https://developer.android.com/reference/android/content/pm/ServiceInfo#FOREGROUND_SERVICE_TYPE_SPECIAL_USE).
    /// Added in API 34. Requires FOREGROUND_SERVICE_SPECIAL_USE permission.
    specialUse("specialUse"),
    /// Corresponds to [`ServiceInfo.FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED`](https://developer.android.com/reference/android/content/pm/ServiceInfo#FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED).
    /// Added in API 34. Reserved for system apps.
    systemExempted("systemExempted"),
    /// Corresponds to [`ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROCESSING`](https://developer.android.com/reference/android/content/pm/ServiceInfo#FOREGROUND_SERVICE_TYPE_MEDIA_PROCESSING).
    /// Added in API 35. Has a 6-hour timeout via Service.onTimeout(int, int).
    mediaProcessing("mediaProcessing");

    private final String safeName;
    ForegroundServiceType(final String safeName){
        this.safeName = safeName.toLowerCase(Locale.ENGLISH);
    }

    public int toAndroidServiceType() {
        switch (this){
            case camera:            return ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA;
            case connectedDevice:   return ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE;
            case dataSync:          return ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC;
            case location:          return ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION;
            case manifest:          return ServiceInfo.FOREGROUND_SERVICE_TYPE_MANIFEST;
            case mediaPlayback:     return ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK;
            case mediaProjection:   return ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION;
            case microphone:        return ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE;
            case phoneCall:         return ServiceInfo.FOREGROUND_SERVICE_TYPE_PHONE_CALL;

            case health:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
                    return ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH;
                return ServiceInfo.FOREGROUND_SERVICE_TYPE_NONE;

            case remoteMessaging:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
                    return ServiceInfo.FOREGROUND_SERVICE_TYPE_REMOTE_MESSAGING;
                return ServiceInfo.FOREGROUND_SERVICE_TYPE_NONE;

            case shortService:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
                    return ServiceInfo.FOREGROUND_SERVICE_TYPE_SHORT_SERVICE;
                return ServiceInfo.FOREGROUND_SERVICE_TYPE_NONE;

            case specialUse:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
                    return ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE;
                return ServiceInfo.FOREGROUND_SERVICE_TYPE_NONE;

            case systemExempted:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
                    return ServiceInfo.FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED;
                return ServiceInfo.FOREGROUND_SERVICE_TYPE_NONE;

            case mediaProcessing:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM)
                    return ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROCESSING;
                return ServiceInfo.FOREGROUND_SERVICE_TYPE_NONE;

            case none:
            default:
                return ServiceInfo.FOREGROUND_SERVICE_TYPE_NONE;
        }
    }

    @Override
    public String getSafeName() {
        return this.safeName;
    }

    static ForegroundServiceType[] valueList = ForegroundServiceType.class.getEnumConstants();
    public static ForegroundServiceType getSafeEnum(String reference) {
        if (reference == null) return null;
        int stringLength = reference.length();
        if (stringLength == 0) return null;

//        if(valueList == null) return null;
//        for (ForegroundServiceType candidate : valueList) {
//            if (candidate.getSafeName().equalsIgnoreCase(reference)) {
//                return candidate;
//            }
//        }

        if (SafeEnum.charMatches(reference, stringLength, 0, 'p')){
            return phoneCall;
        }
        if (SafeEnum.charMatches(reference, stringLength, 0, 'n')){
            return none;
        }
        if (SafeEnum.charMatches(reference, stringLength, 0, 'c')){
            if(SafeEnum.charMatches(reference, stringLength, 1, 'a')) return camera;
            return connectedDevice;
        }
        if (SafeEnum.charMatches(reference, stringLength, 0, 'm')){
            if(SafeEnum.charMatches(reference, stringLength, 1, 'i')) return microphone;
            if(SafeEnum.charMatches(reference, stringLength, 1, 'a')) return manifest;
            // media* types: mediaPlayback, mediaProjection, mediaProcessing
            if(SafeEnum.charMatches(reference, stringLength, 6, 'l')) return mediaPlayback;
            if(SafeEnum.charMatches(reference, stringLength, 6, 'r')){
                if(SafeEnum.charMatches(reference, stringLength, 8, 'j')) return mediaProjection;
                if(SafeEnum.charMatches(reference, stringLength, 8, 'c')) return mediaProcessing;
            }
            return none;
        }
        if (SafeEnum.charMatches(reference, stringLength, 0, 'd')){
            return dataSync;
        }
        if (SafeEnum.charMatches(reference, stringLength, 0, 'l')){
            return location;
        }
        if (SafeEnum.charMatches(reference, stringLength, 0, 'h')){
            return health;
        }
        if (SafeEnum.charMatches(reference, stringLength, 0, 'r')){
            if(SafeEnum.charMatches(reference, stringLength, 2, 'm')) return remoteMessaging;
            return redeliveryIntent;
        }
        if (SafeEnum.charMatches(reference, stringLength, 0, 's')){
            if(SafeEnum.charMatches(reference, stringLength, 1, 'h')) return shortService;
            if(SafeEnum.charMatches(reference, stringLength, 1, 'p')) return specialUse;
            if(SafeEnum.charMatches(reference, stringLength, 1, 'y')) return systemExempted;
        }
        return null;
    }
}
