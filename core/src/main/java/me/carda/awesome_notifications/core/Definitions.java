package me.carda.awesome_notifications.core;

import java.util.HashMap;
import java.util.Map;

import me.carda.awesome_notifications.core.enumerators.ActionType;
import me.carda.awesome_notifications.core.enumerators.DefaultRingtoneType;
import me.carda.awesome_notifications.core.enumerators.GroupAlertBehaviour;
import me.carda.awesome_notifications.core.enumerators.GroupSort;
import me.carda.awesome_notifications.core.enumerators.NotificationImportance;
import me.carda.awesome_notifications.core.enumerators.NotificationLayout;
import me.carda.awesome_notifications.core.enumerators.NotificationPrivacy;
import me.carda.awesome_notifications.core.utils.CalendarUtils;

public interface Definitions {

    String AWESOME_FOREGROUND_ID = "me.carda.awesome_notifications.notifications.system.services.ForegroundService$StartParameter";
    String BROADCAST_FCM_TOKEN = "me.carda.awesome_notifications.notifications.system.services.firebase.TOKEN";
    String EXTRA_BROADCAST_FCM_TOKEN = "token";
    String EXTRA_ANDROID_MEDIA_BUTTON = "android.intent.action.MEDIA_BUTTON";

    String MEDIA_VALID_NETWORK = "^https?:\\/\\/";
    String MEDIA_VALID_FILE = "^files?:\\/\\/";
    String MEDIA_VALID_ASSET = "^assets?:\\/\\/";
    String MEDIA_VALID_RESOURCE = "^resources?:\\/\\/";

    String INITIALIZE_DEBUG_MODE = "debug";
    String INITIALIZE_STORE_INITIAL_ACTION = "storeInitialAction";
    String INITIALIZE_DEFAULT_ICON = "defaultIcon";
    String INITIALIZE_CHANNELS = "initializeChannels";
    String INITIALIZE_CHANNEL_GROUPS = "initializeChannelGroups";

    String BROADCAST_CREATED_NOTIFICATION   = "broadcast.awesome_notifications.CREATED_NOTIFICATION";
    String BROADCAST_DISPLAYED_NOTIFICATION = "broadcast.awesome_notifications.DISPLAYED_NOTIFICATION";
    String BROADCAST_DISMISSED_NOTIFICATION = "broadcast.awesome_notifications.DISMISSED_NOTIFICATION";
    String BROADCAST_SILENT_ACTION = "broadcast.awesome_notifications.SILENT_ACTION";
    String BROADCAST_DEFAULT_ACTION = "broadcast.awesome_notifications.DEFAULT_ACTION";
    String BROADCAST_BACKGROUND_ACTION ="broadcast.awesome_notifications.BACKGROUND_ACTION";
    String EXTRA_BROADCAST_MESSAGE = "notification";

    String CHANNEL_FLUTTER_PLUGIN = "awesome_notifications";
    String DART_REVERSE_CHANNEL = "awesome_notifications_reverse";

    String BADGE_COUNT = "badgeCount";
    String ACTION_HANDLE = "actionHandle";
    String CREATED_HANDLE = "createdHandle";
    String DISPLAYED_HANDLE = "displayedHandle";
    String DISMISSED_HANDLE = "dismissedHandle";
    String SILENT_HANDLE = "silentHandle";
    String STORE_INITIAL_ACTION = "storeInitialAction";
    String BACKGROUND_HANDLE = "awesomeDartBGHandle";

    String SCHEDULER_HELPER_SHARED = "awnot.sh.";
    String SCHEDULER_HELPER_ALL = "all";
    String SCHEDULER_HELPER_GROUP = "group";
    String SCHEDULER_HELPER_CHANNEL = "channel";

    String NOTIFICATION_MODEL = "notificationModel";
    String NOTIFICATION_MODEL_CONTENT = "content";
    String NOTIFICATION_MODEL_SCHEDULE = "schedule";
    String NOTIFICATION_MODEL_BUTTONS = "actionButtons";
    String NOTIFICATION_MODEL_LOCALIZATIONS = "localizations";
    String NOTIFICATION_MODEL_ENCRYPTED_CONTENT = "encryptedContent";
    String NOTIFICATION_MODEL_DECRYPTED_CONTENT = "decryptedContent";
    String NOTIFICATION_SILENT_ACTION = "silentAction";
    String NOTIFICATION_RECEIVED_ACTION = "receivedAction";
    String NOTIFICATION_MODEL_DECRYPT_CONFIG = "decryptConfig";

    String NOTIFICATION_SERVICE_START_MODE = "startMode";
    String NOTIFICATION_FOREGROUND_SERVICE_TYPE = "foregroundServiceType";

    String SHARED_DEFAULTS = "defaults";
    String SHARED_MANAGER = "sharedManager";
    String SHARED_CHANNELS = "channels";
    String SHARED_CREATED = "created";
    String SHARED_CHANNEL_GROUP = "channelGroup";
    String SHARED_DISPLAYED = "displayed";
    String SHARED_DISMISSED = "dismissed";
    String SHARED_SCHEDULED_NOTIFICATIONS = "schedules";
    String SHARED_SCHEDULED_NOT_IDS = "schedulesIds";
    String SHARED_SCHEDULED_NOT_GROUPS = "schedulesGroups";
    String SHARED_SCHEDULED_NOT_CHANNELS = "schedulesChannels";

    String CHANNEL_METHOD_INITIALIZE = "initialize";
    String CHANNEL_METHOD_PUSH_NEXT_DATA = "pushNext";
    String CHANNEL_METHOD_GET_DRAWABLE_DATA = "getDrawableData";
    String CHANNEL_METHOD_ENABLE_WAKELOCK = "enableWakelock";
    String CHANNEL_METHOD_CREATE_NOTIFICATION = "createNewNotification";

    String CHANNEL_METHOD_GET_UTC_TIMEZONE_IDENTIFIER = "getUtcTimeZoneIdentifier";
    String CHANNEL_METHOD_GET_LOCAL_TIMEZONE_IDENTIFIER = "getLocalTimeZoneIdentifier";

    String CHANNEL_METHOD_GET_FCM_TOKEN = "getFirebaseToken";
    String CHANNEL_METHOD_NEW_FCM_TOKEN = "newTokenReceived";
    String CHANNEL_METHOD_IS_FCM_AVAILABLE = "isFirebaseAvailable";

    String CHANNEL_METHOD_SET_NOTIFICATION_CHANNEL = "setNotificationChannel";
    String CHANNEL_METHOD_REMOVE_NOTIFICATION_CHANNEL = "removeNotificationChannel";

    String CHANNEL_METHOD_SHOW_NOTIFICATION_PAGE = "showNotificationPage";
    String CHANNEL_METHOD_SHOW_ALARM_PAGE = "showAlarmPage";
    String CHANNEL_METHOD_SHOW_GLOBAL_DND_PAGE = "showGlobalDndPage";
    String CHANNEL_METHOD_GET_INITIAL_ACTION = "getInitialAction";
    String CHANNEL_METHOD_CLEAR_STORED_ACTION = "clearStoredActions";
    String CHANNEL_METHOD_IS_NOTIFICATION_ALLOWED = "isNotificationAllowed";
    String CHANNEL_METHOD_REQUEST_NOTIFICATIONS = "requestNotifications";
    String CHANNEL_METHOD_CHECK_PERMISSIONS = "checkPermissions";
    String CHANNEL_METHOD_SHOULD_SHOW_RATIONALE = "shouldShowRationale";

    String CHANNEL_METHOD_GET_BADGE_COUNT = "getBadgeCount";
    String CHANNEL_METHOD_SET_BADGE_COUNT = "setBadgeCount";
    String CHANNEL_METHOD_INCREMENT_BADGE_COUNT = "incBadgeCount";
    String CHANNEL_METHOD_DECREMENT_BADGE_COUNT = "decBadgeCount";
    String CHANNEL_METHOD_RESET_BADGE = "resetBadge";

    String CHANNEL_METHOD_GET_APP_LIFE_CYCLE = "getAppLifeCycle";
    String CHANNEL_METHOD_IS_NOTIFICATION_ACTIVE = "isNotificationActive";
    String CHANNEL_METHOD_GET_ALL_ACTIVE_NOTIFICATION_IDS = "getAllActiveNotificationIds";

    String CHANNEL_METHOD_GET_NEXT_DATE = "getNextDate";
    String CHANNEL_METHOD_DISMISS_NOTIFICATION = "dismissNotification";
    String CHANNEL_METHOD_CANCEL_NOTIFICATION = "cancelNotification";
    String CHANNEL_METHOD_CANCEL_SCHEDULE = "cancelSchedule";
    String CHANNEL_METHOD_DISMISS_NOTIFICATIONS_BY_CHANNEL_KEY = "dismissNotificationsByChannelKey";
    String CHANNEL_METHOD_CANCEL_NOTIFICATIONS_BY_CHANNEL_KEY = "cancelNotificationsByChannelKey";
    String CHANNEL_METHOD_CANCEL_SCHEDULES_BY_CHANNEL_KEY = "cancelSchedulesByChannelKey";
    String CHANNEL_METHOD_DISMISS_NOTIFICATIONS_BY_GROUP_KEY = "dismissNotificationsByGroupKey";
    String CHANNEL_METHOD_CANCEL_NOTIFICATIONS_BY_GROUP_KEY = "cancelNotificationsByGroupKey";
    String CHANNEL_METHOD_CANCEL_SCHEDULES_BY_GROUP_KEY = "cancelSchedulesByGroupKey";
    String CHANNEL_METHOD_DISMISS_ALL_NOTIFICATIONS = "dismissAllNotifications";
    String CHANNEL_METHOD_CANCEL_ALL_SCHEDULES = "cancelAllSchedules";
    String CHANNEL_METHOD_CANCEL_ALL_NOTIFICATIONS = "cancelAllNotifications";
    String CHANNEL_METHOD_SILENT_CALLBACK = "silentCallbackReference";
    String CHANNEL_METHOD_SET_EVENT_HANDLES = "setEventHandles";

    String CHANNEL_METHOD_START_FOREGROUND = "startForeground";
    String CHANNEL_METHOD_STOP_FOREGROUND = "stopForeground";

    String CHANNEL_METHOD_GENERATE_SYMMETRIC_SECRET = "generateSymmetricSecret";
    String CHANNEL_METHOD_GENERATE_ASYMMETRIC_KEYS = "generateAsymmetricKeys";
    String CHANNEL_METHOD_REGISTER_SYMMETRIC_CONFIG = "registerSymmetricConfig";
    String CHANNEL_METHOD_REGISTER_PRIVATE_KEY_CONFIG = "setPrivateKeyConfig";
    String CHANNEL_METHOD_REGISTER_PUBLIC_KEY_CONFIG = "setPublicKeyConfig";
    String CHANNEL_METHOD_REGISTER_PRIVATE_PEM = "setPrivateKeyPem";
    String CHANNEL_METHOD_REGISTER_PUBLIC_PEM = "setPublicKeyPem";
    String CHANNEL_METHOD_READ_SYMMETRIC_CONFIG = "getSymmetricConfig";
    String CHANNEL_METHOD_READ_PRIVATE_KEY_CONFIG = "getPrivateKeyConfig";
    String CHANNEL_METHOD_READ_PUBLIC_KEY_CONFIG = "getPublicKeyConfig";
    String CHANNEL_METHOD_READ_PRIVATE_PEM = "getPrivateKeyPem";
    String CHANNEL_METHOD_READ_PUBLIC_PEM = "getPublicKeyPem";
    String CHANNEL_METHOD_REMOVE_SYMMETRIC_CONFIG = "removeSymmetricConfig";
    String CHANNEL_METHOD_REMOVE_PRIVATE_KEY_CONFIG = "removePrivateKeyConfig";
    String CHANNEL_METHOD_REMOVE_PUBLIC_KEY_CONFIG = "removePublicKeyConfig";
    String CHANNEL_METHOD_REMOVE_PRIVATE_PEM = "removePrivateKeyPem";
    String CHANNEL_METHOD_REMOVE_PUBLIC_PEM = "removePublicKeyPem";
    String CHANNEL_METHOD_LIST_SYMMETRIC_CONFIGS = "listSymmetricConfigs";
    String CHANNEL_METHOD_LIST_PRIVATE_KEY_CONFIGS = "listPrivateKeyConfigs";
    String CHANNEL_METHOD_LIST_PUBLIC_KEY_CONFIGS = "listPublicKeyConfigs";
    String CHANNEL_METHOD_LIST_PRIVATE_PEM = "listPrivateKeyPem";
    String CHANNEL_METHOD_LIST_PUBLIC_PEM = "listPublicKeyPem";
    String CHANNEL_METHOD_ENCRYPT_WITH_SYMMETRIC_REF = "encryptWithSymmetricRef";
    String CHANNEL_METHOD_DECRYPT_WITH_SYMMETRIC_REF = "decryptWithSymmetricRef";
    String CHANNEL_METHOD_ENCRYPT_WITH_ASYMMETRIC_REF = "encryptWithAsymmetricRef";
    String CHANNEL_METHOD_DECRYPT_WITH_ASYMMETRIC_REF = "decryptWithAsymmetricRef";
    String CHANNEL_METHOD_ENCRYPT_WITH_SYMMETRIC_SECRET = "encryptWithSymmetricSecret";
    String CHANNEL_METHOD_DECRYPT_WITH_SYMMETRIC_SECRET = "decryptWithSymmetricSecret";
    String CHANNEL_METHOD_ENCRYPT_WITH_ASYMMETRIC_CONFIG = "encryptWithAsymmetricKey";
    String CHANNEL_METHOD_DECRYPT_WITH_ASYMMETRIC_CONFIG = "decryptWithAsymmetricKey";
    String CHANNEL_METHOD_ENCRYPT_CONTENT = "encryptContent";
    String CHANNEL_METHOD_DECRYPT_CONTENT = "decryptContent";
    String CHANNEL_METHOD_ENCRYPT_CONTENT_WITH_CONFIGS = "encryptContentWithConfigs";
    String CHANNEL_METHOD_DECRYPT_CONTENT_WITH_CONFIGS = "decryptContentWithConfigs";

    String CHANNEL_METHOD_LIST_ALL_SCHEDULES = "listAllSchedules";
    String CHANNEL_FORCE_UPDATE = "forceUpdate";

    String EVENT_NOTIFICATION_CREATED = "notificationCreated";
    String EVENT_NOTIFICATION_DISPLAYED = "notificationDisplayed";
    String EVENT_NOTIFICATION_DISMISSED = "notificationDismissed";
    String EVENT_DEFAULT_ACTION = "defaultAction";
    String EVENT_SILENT_ACTION = "silentAction";

    String FIREBASE_ENABLED = "FIREBASE_ENABLED";
    String SELECT_NOTIFICATION = "SELECT_NOTIFICATION";
    String DISMISSED_NOTIFICATION = "DISMISSED_NOTIFICATION";
    String MEDIA_BUTTON = "MEDIA_BUTTON";
    String NOTIFICATION_BUTTON_ACTION_PREFIX = "ACTION_NOTIFICATION";

    String SHARED_PREFERENCES_CHANNEL_MANAGER = "channel_manager";

    String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    String NOTIFICATION_ICON_RESOURCE_ID = "iconResourceId";

    String NOTIFICATION_SCHEDULE_CREATED_DATE = "createdDate";
    String NOTIFICATION_SCHEDULE_ERA = "era";
    String NOTIFICATION_SCHEDULE_TIMEZONE = "timeZone";
    String NOTIFICATION_SCHEDULE_PRECISE_ALARM = "preciseAlarm";
    String NOTIFICATION_SCHEDULE_DELAY_TOLERANCE = "delayTolerance";
    String NOTIFICATION_SCHEDULE_YEAR = "year";
    String NOTIFICATION_SCHEDULE_MONTH = "month";
    String NOTIFICATION_SCHEDULE_DAY = "day";
    String NOTIFICATION_SCHEDULE_HOUR = "hour";
    String NOTIFICATION_SCHEDULE_MINUTE = "minute";
    String NOTIFICATION_SCHEDULE_SECOND = "second";
    String NOTIFICATION_SCHEDULE_MILLISECOND = "millisecond";
    String NOTIFICATION_SCHEDULE_WEEKDAY = "weekday";
    String NOTIFICATION_SCHEDULE_WEEKOFMONTH = "weekOfMonth";
    String NOTIFICATION_SCHEDULE_WEEKOFYEAR = "weekOfYear";
    String NOTIFICATION_SCHEDULE_INTERVAL = "interval";
    String NOTIFICATION_SCHEDULE_REPEATS = "repeats";
    String ACTIVE_NOTIFICATION_IDS = "activeNotificationIds";

    String NOTIFICATION_CREATED_SOURCE = "createdSource";
    String NOTIFICATION_CREATED_LIFECYCLE = "createdLifeCycle";
    String NOTIFICATION_DISPLAYED_LIFECYCLE = "displayedLifeCycle";
    String NOTIFICATION_DISMISSED_LIFECYCLE = "dismissedLifeCycle";
    String NOTIFICATION_ACTION_LIFECYCLE = "actionLifeCycle";
    String NOTIFICATION_CREATED_DATE = "createdDate";
    String NOTIFICATION_ACTION_DATE = "actionDate";
    String NOTIFICATION_DISPLAYED_DATE = "displayedDate";
    String NOTIFICATION_DISMISSED_DATE = "dismissedDate";

    String NOTIFICATION_ID = "id";
    String NOTIFICATION_RANDOM_ID = "randomId";
    String NOTIFICATION_LAYOUT = "notificationLayout";
    String NOTIFICATION_TITLE = "title";
    String NOTIFICATION_BODY = "body";
    String NOTIFICATION_TIMESTAMP = "timestamp";
    String NOTIFICATION_SUMMARY = "summary";
    String NOTIFICATION_SHOW_WHEN = "showWhen";
    String NOTIFICATION_TIMEOUT_AFTER = "timeoutAfter";
    String NOTIFICATION_BUTTON_KEY_PRESSED = "buttonKeyPressed";
    String NOTIFICATION_BUTTON_KEY_INPUT = "buttonKeyInput";
    String NOTIFICATION_JSON = "notificationJson";
    String NOTIFICATION_ACTION_JSON = "notificationActionJson";

    String NOTIFICATION_TITLE_LOC_KEY = "titleLocKey";
    String NOTIFICATION_BODY_LOC_KEY = "bodyLocKey";
    String NOTIFICATION_TITLE_LOC_ARGS = "titleLocArgs";
    String NOTIFICATION_BODY_LOC_ARGS = "bodyLocArgs";

    String NOTIFICATION_MESSAGES = "messages";
    String NOTIFICATION_BUTTON_KEY = "key";
    String NOTIFICATION_BUTTON_ICON = "icon";
    String NOTIFICATION_BUTTON_LABEL = "label";
    String NOTIFICATION_ACTION_TYPE = "actionType";
    String NOTIFICATION_CHRONOMETER = "chronometer";
    String NOTIFICATION_REQUIRE_INPUT_TEXT = "requireInputText";
    String NOTIFICATION_AUTHENTICATION_REQUIRED = "isAuthenticationRequired";

    String NOTIFICATION_PAYLOAD = "payload";
    String NOTIFICATION_INITIAL_FIXED_DATE = "fixedDate";

    String NOTIFICATION_ROUNDED_LARGE_ICON = "roundedLargeIcon";
    String NOTIFICATION_ROUNDED_BIG_PICTURE = "roundedBigPicture";

    String NOTIFICATION_INITIAL_DATE_TIME = "initialDateTime";
    String NOTIFICATION_EXPIRATION_DATE_TIME = "expirationDateTime";
    String NOTIFICATION_CRONTAB_EXPRESSION = "crontabExpression";
    String NOTIFICATION_PRECISE_SCHEDULES = "preciseSchedules";
    String NOTIFICATION_ENABLED = "enabled";
    String NOTIFICATION_AUTO_DISMISSIBLE = "autoDismissible";
    String NOTIFICATION_IS_DANGEROUS_OPTION = "isDangerousOption";
    String NOTIFICATION_PERMISSIONS = "permissions";

    String NOTIFICATION_SHOW_IN_COMPACT_VIEW = "showInCompactView";
    String NOTIFICATION_LOCKED = "locked";
    String NOTIFICATION_DISPLAY_ON_FOREGROUND = "displayOnForeground";
    String NOTIFICATION_DISPLAY_ON_BACKGROUND = "displayOnBackground";
    String NOTIFICATION_ICON = "icon";
    String NOTIFICATION_FULL_SCREEN_INTENT = "fullScreenIntent";
    String NOTIFICATION_WAKE_UP_SCREEN = "wakeUpScreen";
    String NOTIFICATION_PLAY_SOUND = "playSound";
    String NOTIFICATION_SOUND_SOURCE = "soundSource";
    String NOTIFICATION_ENABLE_VIBRATION = "enableVibration";
    String NOTIFICATION_VIBRATION_PATTERN = "vibrationPattern";
    String NOTIFICATION_GROUP_KEY = "groupKey";
    String NOTIFICATION_GROUP_SORT = "groupSort";
    String NOTIFICATION_GROUP_ALERT_BEHAVIOR = "groupAlertBehavior";
    String NOTIFICATION_PRIVACY = "privacy";
    String NOTIFICATION_CATEGORY = "category";
    String NOTIFICATION_CUSTOM_SOUND = "customSound";
    String NOTIFICATION_DEFAULT_PRIVACY = "defaultPrivacy";
    String NOTIFICATION_DEFAULT_RINGTONE_TYPE = "defaultRingtoneType";
    String NOTIFICATION_PRIVATE_MESSAGE = "privateMessage";
    String NOTIFICATION_ONLY_ALERT_ONCE = "onlyAlertOnce";
    String NOTIFICATION_CHANNEL_KEY = "channelKey";
    String NOTIFICATION_CHANNEL_NAME = "channelName";
    String NOTIFICATION_CHANNEL_DESCRIPTION = "channelDescription";
    String NOTIFICATION_CHANNEL_SHOW_BADGE = "channelShowBadge";
    String NOTIFICATION_CHANNEL_GROUP_NAME = "channelGroupName";
    String NOTIFICATION_CHANNEL_GROUP_KEY = "channelGroupKey";
    String NOTIFICATION_CHANNEL_CRITICAL_ALERTS = "criticalAlerts";
    String CHANNEL_METHOD_SET_LOCALIZATION = "setLocalization";
    String CHANNEL_METHOD_GET_LOCALIZATION = "getLocalization";
    String NOTIFICATION_IMPORTANCE = "importance";
    String NOTIFICATION_COLOR = "color";
    String NOTIFICATION_BACKGROUND_COLOR = "backgroundColor";
    String NOTIFICATION_DEFAULT_COLOR = "defaultColor";
    String NOTIFICATION_APP_ICON = "defaultIcon";
    String NOTIFICATION_LARGE_ICON = "largeIcon";
    String NOTIFICATION_BIG_PICTURE = "bigPicture";
    String NOTIFICATION_HIDE_LARGE_ICON_ON_EXPAND = "hideLargeIconOnExpand";
    String NOTIFICATION_PROGRESS = "progress";
    String NOTIFICATION_BADGE = "badge";
    String NOTIFICATION_ENABLE_LIGHTS = "enableLights";
    String NOTIFICATION_LED_COLOR = "ledColor";
    String NOTIFICATION_LED_ON_MS = "ledOnMs";
    String NOTIFICATION_LED_OFF_MS = "ledOffMs";
    String NOTIFICATION_TICKER = "ticker";
    String NOTIFICATION_BUTTON_LABELS = "buttonLabels";
    String NOTIFICATION_ALLOW_WHILE_IDLE = "allowWhileIdle";
    String NOTIFICATION_BG_HANDLE_CLASS = "bgHandleClass";
    String NOTIFICATION_DURATION = "duration";
    String NOTIFICATION_PLAY_STATE = "playState";
    String NOTIFICATION_PLAYBACK_SPEED = "playbackSpeed";

    String NOTIFICATION_PRIVATE_KEY = "privateKey";
    String NOTIFICATION_PUBLIC_KEY = "publicKey";
    String NOTIFICATION_ENCRYPT_KEY_REF = "keyReference";
    String NOTIFICATION_ENCRYPT_PEM_REF = "pemReference";
    String NOTIFICATION_SECRET_REF = "secretReference";
    String NOTIFICATION_SECRET = "secret";

    String ENCRYPT_PRIVATE_KEY_CONFIG = "keyReference";
    String ENCRYPT_PUBLIC_KEY_CONFIG = "keyReference";
    String ENCRYPT_SYMMETRIC_CONFIG = "keyReference";
    String ENCRYPT_PEM_CONTENT = "pemContent";
    String ENCRYPT_KEY_REFERENCE = "keyReference";
    String ENCRYPT_PEM_REFERENCE = "pemReference";
    String ENCRYPT_PRIVATE_KEY_SIZE = "privateKeySize";

    String ENCRYPT_SECRET_LENGTH = "secretLength";
    String ENCRYPT_SALT_LENGTH = "saltLength";
    String ENCRYPT_KEY_BYTES_COUNT = "keyBytesCount";
    String ENCRYPT_DIGEST_ITERATIONS = "digestIterations";

    String ENCRYPT_SYMMETRIC_ALGORITHM = "symmetricAlgorithm";
    String ENCRYPT_ASYMMETRIC_ALGORITHM = "asymmetricAlgorithm";
    String ENCRYPT_CONTENT = "encryptedContent";
    String ENCRYPT_VALUE = "value";
    String ENCRYPT_SEED = "seed";

    Map<String, Object> initialValues = new HashMap<String, Object>(){{
        put(Definitions.NOTIFICATION_SCHEDULE_REPEATS, true);
        put(Definitions.NOTIFICATION_ID, 0);
        put(Definitions.NOTIFICATION_IMPORTANCE, NotificationImportance.Default);
        put(Definitions.NOTIFICATION_LAYOUT, NotificationLayout.Default);
        put(Definitions.NOTIFICATION_GROUP_SORT, GroupSort.Desc);
        put(Definitions.NOTIFICATION_GROUP_ALERT_BEHAVIOR, GroupAlertBehaviour.All);
        put(Definitions.NOTIFICATION_DEFAULT_PRIVACY, NotificationPrivacy.Private);
        put(Definitions.NOTIFICATION_CHANNEL_DESCRIPTION, "Notifications");
        put(Definitions.NOTIFICATION_CHANNEL_NAME, "Notifications");
        put(Definitions.NOTIFICATION_CHANNEL_SHOW_BADGE, false);
        put(Definitions.NOTIFICATION_DISPLAY_ON_FOREGROUND, true);
        put(Definitions.NOTIFICATION_DISPLAY_ON_BACKGROUND, true);
        put(Definitions.NOTIFICATION_HIDE_LARGE_ICON_ON_EXPAND, false);
        put(Definitions.NOTIFICATION_ENABLED, true);
        put(Definitions.NOTIFICATION_SHOW_WHEN, true);
        put(Definitions.NOTIFICATION_REQUIRE_INPUT_TEXT, false);
        put(Definitions.NOTIFICATION_ACTION_TYPE, ActionType.Default);
        put(Definitions.NOTIFICATION_PAYLOAD, null);
        put(Definitions.NOTIFICATION_ENABLE_VIBRATION, true);
        put(Definitions.NOTIFICATION_DEFAULT_COLOR, 0xFF000000);
        put(Definitions.NOTIFICATION_LED_COLOR, 0xFFFFFFFF);
        put(Definitions.NOTIFICATION_ENABLE_LIGHTS, true);
        put(Definitions.NOTIFICATION_LED_OFF_MS, 700);
        put(Definitions.NOTIFICATION_LED_ON_MS, 300);
        put(Definitions.NOTIFICATION_PLAY_SOUND, true);
        put(Definitions.NOTIFICATION_AUTO_DISMISSIBLE, true);
        put(Definitions.NOTIFICATION_DEFAULT_RINGTONE_TYPE, DefaultRingtoneType.Notification);
        put(Definitions.NOTIFICATION_SCHEDULE_TIMEZONE, CalendarUtils.getInstance().getLocalTimeZone().toString());
        put(Definitions.NOTIFICATION_ALLOW_WHILE_IDLE, false);
        put(Definitions.NOTIFICATION_ONLY_ALERT_ONCE, false);
        put(Definitions.NOTIFICATION_SHOW_IN_COMPACT_VIEW, true);
        put(Definitions.NOTIFICATION_IS_DANGEROUS_OPTION, false);
        put(Definitions.NOTIFICATION_WAKE_UP_SCREEN, false);
        put(Definitions.NOTIFICATION_CHANNEL_CRITICAL_ALERTS, false);
        put(Definitions.NOTIFICATION_ROUNDED_LARGE_ICON, false);
        put(Definitions.NOTIFICATION_ROUNDED_BIG_PICTURE, false);
    }};
}
