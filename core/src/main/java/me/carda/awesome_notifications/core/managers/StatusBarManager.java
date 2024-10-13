package me.carda.awesome_notifications.core.managers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.carda.awesome_notifications.core.AwesomeNotifications;
import me.carda.awesome_notifications.core.Definitions;
import me.carda.awesome_notifications.core.databases.SQLitePrimitivesDB;
import me.carda.awesome_notifications.core.enumerators.NotificationLayout;
import me.carda.awesome_notifications.core.exceptions.AwesomeNotificationsException;
import me.carda.awesome_notifications.core.exceptions.ExceptionCode;
import me.carda.awesome_notifications.core.exceptions.ExceptionFactory;
import me.carda.awesome_notifications.core.models.NotificationModel;
import me.carda.awesome_notifications.core.utils.JsonUtils;
import me.carda.awesome_notifications.core.utils.StringUtils;

public class StatusBarManager extends NotificationListenerService {

    private static final String TAG = "CancellationManager";

    private final StringUtils stringUtils;

    private final SharedPreferences preferences;
    public final Map<String, List<String>> activeNotificationsGroup;
    public final Map<String, List<String>> activeNotificationsChannel;

    // ************** SINGLETON PATTERN ***********************

    public StatusBarManager() {
        this.stringUtils = StringUtils.getInstance();
        preferences = this.getSharedPreferences(
                AwesomeNotifications.getPackageName(this) + "." + stringUtils.digestString(TAG),
                Context.MODE_PRIVATE);

        activeNotificationsGroup = loadNotificationIdFromPreferences("group");
        activeNotificationsChannel = loadNotificationIdFromPreferences("channel");
    }

    private static StatusBarManager instance;

    private StatusBarManager(@NonNull final Context context, @NonNull StringUtils stringUtils) {
        this.stringUtils = stringUtils;

        preferences = context.getSharedPreferences(
                AwesomeNotifications.getPackageName(context) + "." + stringUtils.digestString(TAG),
                Context.MODE_PRIVATE);

        activeNotificationsGroup = loadNotificationIdFromPreferences("group");
        activeNotificationsChannel = loadNotificationIdFromPreferences("channel");
    }

    public static StatusBarManager getInstance(@NonNull final Context context) {
        if (instance == null)
            instance = new StatusBarManager(
                    context,
                    StringUtils.getInstance());
        return instance;
    }

    // ********************************************************

    // The status bar cannot be closed from a notification action since Android 12
    // https://developer.android.com/about/versions/12/behavior-changes-all?hl=pt-br#close-system-dialogs-exceptions
    // https://developer.android.com/reference/android/content/Intent?hl=pt-br#ACTION_CLOSE_SYSTEM_DIALOGS
    @SuppressLint("MissingPermission")
    public void closeStatusBar(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S /*Android 12*/) {
            Intent closingIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(closingIntent);
        }
    }

    public void showNotificationOnStatusBar(@NonNull Context context, NotificationModel notificationModel, Notification notification) throws Exception {

        registerActiveNotification(notificationModel, notificationModel.content.id);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = getNotificationManager(context);
            notificationManager.notify(notificationModel.content.id, notification);
        } else {
            NotificationManagerCompat notificationManagerCompat = getAdaptedOldNotificationManager(context);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            notificationManagerCompat.notify(String.valueOf(notificationModel.content.id), notificationModel.content.id, notification);
        }
    }

    public void dismissNotification(Context context, Integer id) throws AwesomeNotificationsException {

        String idKey = id.toString();
        int idKeyValue = Integer.parseInt(idKey);

        if (Build.VERSION.SDK_INT >=  Build.VERSION_CODES.O /*Android 8*/) {
            NotificationManager notificationManager = getNotificationManager(context);

            notificationManager.cancel(idKey, idKeyValue);
            notificationManager.cancel(idKeyValue);

            // Dismiss the last notification group summary notification
            String groupKey = getIndexActiveNotificationGroup(idKey);
            if (!groupKey.isEmpty()) {
                try {
                    dismissNotificationsByGroupKey(context, groupKey);
                } catch (AwesomeNotificationsException ignored) {
                }
            }
        }
        else {
            NotificationManagerCompat notificationManager = getAdaptedOldNotificationManager(context);

            notificationManager.cancel(idKey, idKeyValue);
            notificationManager.cancel(idKeyValue);
        }

        unregisterActiveNotification(context, id);
    }

    public boolean dismissNotificationsByChannelKey(@NonNull Context context, @NonNull final String channelKey) throws AwesomeNotificationsException {

        List<String> notificationIds = unregisterActiveChannelKey(channelKey);

        if (notificationIds != null)
            for (String idKey : notificationIds)
                dismissNotification(context, Integer.parseInt(idKey));

        return notificationIds != null;
    }

    public boolean dismissNotificationsByGroupKey(@NonNull Context context, @NonNull final String groupKey) throws AwesomeNotificationsException {

        List<String> notificationIds = unregisterActiveGroupKey(groupKey);

        if (notificationIds != null)
            for (String idKey : notificationIds)
                dismissNotification(context, Integer.parseInt(idKey));

        return notificationIds != null;
    }

    public void dismissAllNotifications(@NonNull Context context) throws AwesomeNotificationsException {

        if (Build.VERSION.SDK_INT >=  Build.VERSION_CODES.O /*Android 8*/) {
            NotificationManagerCompat notificationManager = getAdaptedOldNotificationManager(context);
            notificationManager.cancelAll();
        }
        else {
            NotificationManager notificationManager
                    = (NotificationManager)
                            context.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.cancelAll();
        }

        resetRegisters();
    }

    public boolean isFirstActiveOnGroupKey(String groupKey){
        if(stringUtils.isNullOrEmpty(groupKey))
            return false;

        List<String> activeGroupedNotifications =
                activeNotificationsGroup.get(groupKey);

        return activeGroupedNotifications == null || activeGroupedNotifications.isEmpty();
    }

    public boolean isFirstActiveOnChannelKey(String channelKey){
        if(stringUtils.isNullOrEmpty(channelKey))
            return false;

        List<String> activeGroupedNotifications =
                activeNotificationsChannel.get(channelKey);

        return activeGroupedNotifications == null || activeGroupedNotifications.isEmpty();
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private NotificationManager getNotificationManager(@NonNull Context context) throws AwesomeNotificationsException {
        try {
            return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        } catch (Exception exception) {
            throw ExceptionFactory
                    .getInstance()
                    .createNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_MISSING_ARGUMENTS,
                            "Notification Service is not available",
                            ExceptionCode.DETAILED_REQUIRED_ARGUMENTS+".notificationService",
                            exception);
        }
    }

    private NotificationManagerCompat getAdaptedOldNotificationManager(@NonNull Context context) throws AwesomeNotificationsException {
        try {
            return NotificationManagerCompat.from(context);
        } catch (Exception exception) {
            throw ExceptionFactory
                    .getInstance()
                    .createNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_MISSING_ARGUMENTS,
                            "Notification Manager is not available",
                            ExceptionCode.DETAILED_REQUIRED_ARGUMENTS+".notificationManager",
                            exception);
        }
    }

    private void setIndexActiveNotificationChannel(SharedPreferences.Editor editor, String idKey, String channelKey) throws AwesomeNotificationsException {
        try {
            editor.putString("ic:"+idKey, channelKey);
        } catch (Exception exception) {
            throw ExceptionFactory
                    .getInstance()
                    .createNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_MISSING_ARGUMENTS,
                            "Shared preferences is not available",
                            ExceptionCode.DETAILED_REQUIRED_ARGUMENTS+".sharedPreferences",
                            exception);
        }
    }

    private String getIndexActiveNotificationChannel(String idKey){
        return preferences.getString("ic:"+idKey, "");
    }

    private void removeIndexActiveNotificationChannel(SharedPreferences.Editor editor, String idKey){
        editor.remove("ic:"+idKey);
    }

    private void setIndexActiveNotificationGroup(SharedPreferences.Editor editor, String idKey, String groupKey){
        editor.putString("ig:"+idKey, groupKey);
    }

    private String getIndexActiveNotificationGroup(String idKey){
        return preferences.getString("ig:"+idKey, "");
    }

    private void removeIndexActiveNotificationGroup(SharedPreferences.Editor editor, String idKey){
        editor.remove("ig:"+idKey);
    }

    private void setIndexCollapsedLayout(SharedPreferences.Editor editor, String idKey, boolean isCollapsed){
        editor.putBoolean("cl:"+idKey, isCollapsed);
    }

    private boolean isIndexCollapsedLayout(String groupKey) {
        return preferences.getBoolean("cl:" + groupKey, false);
    }

    private void removeIndexCollapsedLayout(SharedPreferences.Editor editor, String idKey){
        editor.remove("cl:"+idKey);
    }

    private void registerActiveNotification(@NonNull NotificationModel notificationModel, int id) throws Exception {

        String idKey = String.valueOf(id);
        String groupKey = !stringUtils.isNullOrEmpty(notificationModel.content.groupKey) ? notificationModel.content.groupKey : "";
        String channelKey = !stringUtils.isNullOrEmpty(notificationModel.content.channelKey) ? notificationModel.content.channelKey : "";

        SharedPreferences.Editor editor = preferences.edit();

        if(!channelKey.isEmpty()){
            registerNotificationIdOnPreferences(editor, "channel", activeNotificationsChannel, channelKey, idKey);
            setIndexActiveNotificationChannel(editor, idKey, channelKey);
        }

        if(!groupKey.isEmpty()){
            registerNotificationIdOnPreferences(editor, "group", activeNotificationsGroup, groupKey, idKey);
            setIndexActiveNotificationGroup(editor, idKey, groupKey);
        }

        setIndexCollapsedLayout(editor, idKey, notificationModel.content.notificationLayout != NotificationLayout.Default);

        editor.apply();
    }

    private void registerNotificationIdOnPreferences(SharedPreferences.Editor editor, String type, Map<String, List<String>> map, String reference, String notificationId){
        List<String> list = map.get(reference);

        if(list == null)
            list = new ArrayList<String>();

        if(!list.contains(notificationId))
            list.add(notificationId);

        map.put(reference, list);
        updateActiveMapIntoPreferences(editor, type, map);
    }

    public void unregisterActiveNotification(Context context, int notificationId) throws AwesomeNotificationsException {

        SharedPreferences.Editor editor = preferences.edit();

        String idKey = String.valueOf(notificationId);
        String groupKey = getIndexActiveNotificationGroup(idKey);
        if(!groupKey.isEmpty()){

            List<String> listToRemove = activeNotificationsGroup.get(groupKey);
            if(listToRemove != null){
                if(listToRemove.remove(idKey)){
                    if(listToRemove.isEmpty())
                        activeNotificationsGroup.remove(groupKey);
                    else
                        activeNotificationsGroup.put(groupKey, listToRemove);
                    updateActiveMapIntoPreferences(editor, "group", activeNotificationsGroup);

                    boolean isCollapsedLayout = isIndexCollapsedLayout(groupKey);

                    // For collapsed layouts, where the group has 1 left notification,
                    // the missing summary orphan group should be removed
                    if(!isCollapsedLayout && listToRemove.size() == 1)
                        dismissNotification(context, Integer.parseInt(listToRemove.get(0)));
                }
            }
        }

        String channelKey = getIndexActiveNotificationChannel(idKey);
        if(!channelKey.isEmpty()){
            List<String> listToRemove = activeNotificationsChannel.get(channelKey);
            if(listToRemove != null){
                listToRemove.remove(idKey);
                if(listToRemove.isEmpty())
                    activeNotificationsChannel.remove(channelKey);
                else
                    activeNotificationsChannel.put(channelKey, listToRemove);
                updateActiveMapIntoPreferences(editor, "channel", activeNotificationsChannel);
            }
        }

        removeAllIndexes(editor, idKey);
        editor.apply();
    }

    private void removeAllIndexes(SharedPreferences.Editor editor, String idKey){
        removeIndexActiveNotificationChannel(editor, idKey);
        removeIndexActiveNotificationGroup(editor, idKey);
        removeIndexCollapsedLayout(editor, idKey);
    }

    private List<String> unregisterActiveChannelKey(String channelKey){

        if(!stringUtils.isNullOrEmpty(channelKey)){
            List<String> removed = activeNotificationsChannel.remove(channelKey);
            if(removed != null){

                SharedPreferences.Editor editor = preferences.edit();

                boolean hasGroup = false;
                for(String idKey : removed){
                    String groupKey = getIndexActiveNotificationGroup(idKey);
                    if(!groupKey.isEmpty()){
                        List<String> listToRemove = activeNotificationsGroup.get(groupKey);
                        if(listToRemove != null){
                            hasGroup = true;
                            listToRemove.remove(idKey);
                            if(listToRemove.isEmpty())
                                activeNotificationsGroup.remove(groupKey);
                            else
                                activeNotificationsGroup.put(channelKey, listToRemove);
                        }
                    }
                    removeAllIndexes(editor, idKey);
                }

                updateActiveMapIntoPreferences(editor, "channel", activeNotificationsChannel);
                if(hasGroup)
                    updateActiveMapIntoPreferences(editor, "group", activeNotificationsGroup);

                editor.apply();
                return removed;
            }
        }

        return null;
    }

    public List<String> unregisterActiveGroupKey(String groupKey){

        if(!stringUtils.isNullOrEmpty(groupKey)){
            List<String> removed = activeNotificationsGroup.remove(groupKey);
            if(removed != null){

                SharedPreferences.Editor editor = preferences.edit();

                boolean hasGroup = false;
                for(String idKey : removed){
                    String channelKey = getIndexActiveNotificationChannel(idKey);
                    if(!channelKey.isEmpty()){
                        List<String> listToRemove = activeNotificationsChannel.get(channelKey);
                        if(listToRemove != null){
                            hasGroup = true;
                            listToRemove.remove(idKey);
                            if(listToRemove.isEmpty())
                                activeNotificationsChannel.remove(channelKey);
                            else
                                activeNotificationsChannel.put(channelKey, listToRemove);
                        }
                    }
                    removeAllIndexes(editor, idKey);
                }

                updateActiveMapIntoPreferences(editor, "group", activeNotificationsGroup);
                if(hasGroup)
                    updateActiveMapIntoPreferences(editor, "channel", activeNotificationsChannel);

                editor.apply();
                return removed;
            }
        }

        return null;
    }

    private void updateActiveMapIntoPreferences(SharedPreferences.Editor editor, String type, Map<String, List<String>> map) {
        JsonUtils<List<String>> jsonUtils = new JsonUtils<>();
        String mapString = jsonUtils.toJson(map);
        editor.putString(type, mapString);
    }

    private Map<String, List<String>> loadNotificationIdFromPreferences(String type){
        String mapString = preferences.getString(type, null);
        if (mapString != null){
            JsonUtils<List<String>> jsonUtils = new JsonUtils<>();
            Object parsed = jsonUtils.fromJson(mapString);
            if (parsed instanceof Map) {
                return (Map<String, List<String>>) parsed;
            }
        }
        return new HashMap<>();
    }

    private void resetRegisters(){
        preferences
                .edit()
                .clear()
                .apply();

        activeNotificationsGroup.clear();
        activeNotificationsChannel.clear();
    }

    public Notification getAndroidNotificationById(Context context, int id){
        if(context != null){

            NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            StatusBarNotification[] currentActiveNotifications = manager.getActiveNotifications();

            if(currentActiveNotifications != null){
                for (StatusBarNotification activeNotification : currentActiveNotifications) {
                    if(activeNotification.getId() == id){
                        return activeNotification.getNotification();
                    }
                }
            }
        }
        return null;
    }

    public List<Notification> getAllAndroidActiveNotificationsByChannelKey(Context context, String channelKey){
        List<Notification> notifications = new ArrayList<>();
        if(context != null && !stringUtils.isNullOrEmpty(channelKey)){

            NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            StatusBarNotification[] currentActiveNotifications = manager.getActiveNotifications();

            String hashedKey = stringUtils.digestString(channelKey);

            if(currentActiveNotifications != null){
                for (StatusBarNotification activeNotification : currentActiveNotifications) {

                    Notification notification = activeNotification.getNotification();

                    String notificationChannelKey = notification.extras
                            .getString(Definitions.NOTIFICATION_CHANNEL_KEY);

                    if(notificationChannelKey != null && notificationChannelKey.equals(hashedKey)){
                        notifications.add(notification);
                    }
                }
            }
        }
        return notifications;
    }

    public List<Notification> getAllAndroidActiveNotificationsByGroupKey(Context context, String groupKey){
        List<Notification> notifications = new ArrayList<>();
        if(context != null && !stringUtils.isNullOrEmpty(groupKey)){

            NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            StatusBarNotification[] currentActiveNotifications = manager.getActiveNotifications();

            String hashedKey = stringUtils.digestString(groupKey);

            if(currentActiveNotifications != null){
                for (StatusBarNotification activeNotification : currentActiveNotifications) {

                    Notification notification = activeNotification.getNotification();

                    String notificationGroupKey = notification.extras
                            .getString(Definitions.NOTIFICATION_GROUP_KEY);

                    if(notificationGroupKey != null && notificationGroupKey.equals(hashedKey)){
                        notifications.add(notification);
                    }
                }
            }
        }
        return notifications;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        SQLitePrimitivesDB sqLitePrimitives = SQLitePrimitivesDB.getInstance(this);
        int id = sbn.getNotification()
                        .extras
                        .getInt(Definitions.NOTIFICATION_ID);

        sqLitePrimitives.setInt(
                this,
                Definitions.ACTIVE_NOTIFICATION_IDS,
                Integer.toString(id),
                id);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        SQLitePrimitivesDB sqLitePrimitives = SQLitePrimitivesDB.getInstance(this);
        int id = sbn.getNotification()
                .extras
                .getInt(Definitions.NOTIFICATION_ID);

        sqLitePrimitives.removeInt(
                this,
                Definitions.ACTIVE_NOTIFICATION_IDS,
                Integer.toString(id));
    }

    public Collection<Integer> getAllActiveNotificationIdsOnStatusBar() {
        List<Integer> activeIds = _getAllActiveIdsWithoutServices();
        if (activeIds != null) return activeIds;

        SQLitePrimitivesDB sqLitePrimitives = SQLitePrimitivesDB.getInstance(this);
        return sqLitePrimitives.getAllIntValues(
                this,
                Definitions.ACTIVE_NOTIFICATION_IDS
        ).values();
    }

    public boolean isNotificationActiveOnStatusBar(int id) {
        return getIsNotificationActiveWithoutServices(id);

    }

    @Nullable
    private List<Integer> _getAllActiveIdsWithoutServices() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        StatusBarNotification[] activeNotifications = notificationManager.getActiveNotifications();
        List<Integer> activeIds = new ArrayList<>();
        for (StatusBarNotification notification : activeNotifications) {
            activeIds.add(notification.getId());
        }
        return activeIds;
    }

    @NotNull
    private boolean getIsNotificationActiveWithoutServices(int id) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        StatusBarNotification[] activeNotifications = notificationManager.getActiveNotifications();
        for (StatusBarNotification notification : activeNotifications) {
            if (notification.getId() == id) {
                return true;
            }
        }
        return false;
    }
}
