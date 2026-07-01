package me.carda.awesome_notifications.core.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import me.carda.awesome_notifications.core.Definitions;

public class NotificationContentModelTest {

    @Test
    public void fromMap_readsCriticalAlertFlag() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put(Definitions.NOTIFICATION_CHANNEL_KEY, "test_channel");
        map.put(Definitions.NOTIFICATION_CRITICAL_ALERT, true);

        NotificationContentModel content = new NotificationContentModel().fromMap(map);

        assertTrue(content.criticalAlert);
    }

    @Test
    public void fromMap_defaultsCriticalAlertToFalse() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put(Definitions.NOTIFICATION_CHANNEL_KEY, "test_channel");

        NotificationContentModel content = new NotificationContentModel().fromMap(map);

        assertFalse(content.criticalAlert);
    }

    @Test
    public void toMap_serializesCriticalAlertFlag() throws Exception {
        NotificationContentModel content = new NotificationContentModel();
        content.channelKey = "test_channel";
        content.criticalAlert = true;

        Map<String, Object> map = content.toMap();

        assertEquals(true, map.get(Definitions.NOTIFICATION_CRITICAL_ALERT));
    }
}
