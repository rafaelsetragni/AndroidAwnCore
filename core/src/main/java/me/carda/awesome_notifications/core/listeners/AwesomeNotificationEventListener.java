package me.carda.awesome_notifications.core.listeners;

import me.carda.awesome_notifications.core.models.actions.NotificationReceived;

public interface AwesomeNotificationEventListener {
    public void onNewNotificationReceived(String eventName, NotificationReceived notificationReceived);
}
