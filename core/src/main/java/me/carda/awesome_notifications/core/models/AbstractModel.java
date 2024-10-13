package me.carda.awesome_notifications.core.models;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.primitives.Longs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.carda.awesome_notifications.core.enumerators.ActionType;
import me.carda.awesome_notifications.core.enumerators.DefaultRingtoneType;
import me.carda.awesome_notifications.core.enumerators.ForegroundServiceType;
import me.carda.awesome_notifications.core.enumerators.ForegroundStartMode;
import me.carda.awesome_notifications.core.enumerators.GroupAlertBehaviour;
import me.carda.awesome_notifications.core.enumerators.GroupSort;
import me.carda.awesome_notifications.core.enumerators.LogLevel;
import me.carda.awesome_notifications.core.enumerators.MediaSource;
import me.carda.awesome_notifications.core.enumerators.NotificationCategory;
import me.carda.awesome_notifications.core.enumerators.NotificationImportance;
import me.carda.awesome_notifications.core.enumerators.NotificationLayout;
import me.carda.awesome_notifications.core.enumerators.NotificationLifeCycle;
import me.carda.awesome_notifications.core.enumerators.NotificationPermission;
import me.carda.awesome_notifications.core.enumerators.NotificationPrivacy;
import me.carda.awesome_notifications.core.enumerators.NotificationSource;
import me.carda.awesome_notifications.core.enumerators.SafeEnum;
import me.carda.awesome_notifications.core.exceptions.AwesomeNotificationsException;
import me.carda.awesome_notifications.core.utils.JsonUtils;
import me.carda.awesome_notifications.core.utils.SerializableUtils;
import me.carda.awesome_notifications.core.utils.StringUtils;

public abstract class AbstractModel implements Cloneable {
    protected final SerializableUtils serializableUtils;
    protected final StringUtils stringUtils;
    protected final static  JsonUtils<Object> jsonUtils = new JsonUtils<>();
    public static Map<String, Object> defaultValues = new HashMap<>();

    protected AbstractModel(){
        this.serializableUtils = SerializableUtils.getInstance();
        this.stringUtils = StringUtils.getInstance();
    }
    protected AbstractModel(SerializableUtils serializableUtils, StringUtils stringUtils){
        this.serializableUtils = serializableUtils;
        this.stringUtils = stringUtils;
    }

    public abstract AbstractModel fromMap(Map<String, Object> arguments);
    public abstract Map<String, Object> toMap();

    public abstract String toJson();
    public abstract AbstractModel fromJson(String json);

    protected String templateToJson(){
        return jsonUtils.toJson(this.toMap());
    }

    protected AbstractModel templateFromJson(String json) {
        if(json == null || json.isEmpty()) return null;
        Object object = jsonUtils.fromJson(json);
        if (object instanceof Map) {
            Map map = (Map) object;
            if(map.isEmpty()) return null;
            return this.fromMap(map);
        }
        return null;
    }

    public AbstractModel getClone () {
        try {
            return (AbstractModel)this.clone();
        }
        catch (CloneNotSupportedException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // ***********************  OUTPUT SERIALIZATION METHODS   *********************************

    public void putDataOnSerializedMap(
            @NonNull String reference,
            @NonNull Map<String, Object> mapData,
            @Nullable Serializable value
    ){
        if(value == null) return;
        if(value instanceof SafeEnum)
            putSafeEnumOnSerializedMap(
                    reference,
                    mapData,
                    (SafeEnum) value);
        else
            mapData.put(reference, value);
    }

    public void putDataOnSerializedMap(
            @NonNull String reference,
            @NonNull Map<String, Object> mapData,
            @Nullable AbstractModel value
    ){
        if (value == null) return;
        mapData.put(
                reference,
                value.toMap());
    }

    private void putSafeEnumOnSerializedMap(
            @NonNull String reference,
            @NonNull Map<String, Object> mapData,
            @Nullable SafeEnum value
    ){
        if (value == null) return;
        mapData.put(
                reference,
                value.getSafeName());
    }

    public void putDataOnSerializedMap(
            @NonNull String reference,
            @NonNull Map<String, Object> mapData,
            @Nullable Calendar value
    ){
        if (value == null) return;
        mapData.put(
                reference,
                serializableUtils.serializeCalendar(value));
    }

    public void putDataOnSerializedMap(
            @NonNull String reference,
            @NonNull Map<String, Object> mapData,
            @Nullable TimeZone value
    ){
        if (value == null) return;
        mapData.put(
                reference,
                serializableUtils.serializeTimeZone(value));
    }

    public void putDataOnSerializedMap(
            @NonNull String reference,
            @NonNull Map<String, Object> mapData,
            @Nullable List value
    ){
        if (value == null) return;
        if (value.isEmpty()) return;

        List<Object> response = new ArrayList<>();
        for (Object object : value){
            if (object instanceof AbstractModel) {
                response.add(((AbstractModel) object).toMap());
                continue;
            }
            if (object instanceof SafeEnum) {
                response.add(((SafeEnum) object).getSafeName());
                continue;
            }
            if (object instanceof Calendar) {
                response.add(SerializableUtils.getInstance().serializeCalendar((Calendar) object));
                continue;
            }
            if (object instanceof Serializable){
                response.add(object);
                continue;
            }
        }
        mapData.put(
                reference,
                response);
    }

    public void putDataOnSerializedMap(
            @NonNull String reference,
            @NonNull Map<String, Object> mapData,
            @Nullable Map value
    ){
        if (value == null) return;
        if (value.isEmpty()) return;

        Map<String, Object> serializedMap = new HashMap<>();
        for(Object objEntry : value.entrySet()) {
            Map.Entry entry = (Map.Entry) objEntry;
            Object innerValue = entry.getValue();
            if (innerValue != null)
                if (innerValue instanceof AbstractModel)
                    serializedMap.put((String) entry.getKey(), ((AbstractModel)innerValue).toMap());
                else
                    serializedMap.put((String) entry.getKey(), innerValue);
        }

        mapData.put(
                reference,
                serializedMap);
    }

    // ***********************  INPUT SERIALIZATION METHODS   *********************************

    public ActionType getValueOrDefault(
            @NonNull Map<String, Object> map,
            @NonNull String reference,
            @NonNull Class<ActionType> type,
            @Nullable ActionType defaultValue
    ){
        Object value = map.get(reference);
        if(value == null) return defaultValue;

        if(value instanceof String)
            return ActionType.getSafeEnum((String) value);

        return defaultValue;
    }

    public DefaultRingtoneType getValueOrDefault(
            @NonNull Map<String, Object> map,
            @NonNull String reference,
            @NonNull Class<DefaultRingtoneType> type,
            @Nullable DefaultRingtoneType defaultValue
    ){
        Object value = map.get(reference);
        if(value == null) return defaultValue;

        if(value instanceof String)
            return DefaultRingtoneType.getSafeEnum((String) value);

        return defaultValue;
    }

    public ForegroundServiceType getValueOrDefault(
            @NonNull Map<String, Object> map,
            @NonNull String reference,
            @NonNull Class<ForegroundServiceType> type,
            @Nullable ForegroundServiceType defaultValue
    ){
        Object value = map.get(reference);
        if(value == null) return defaultValue;

        if(value instanceof String)
            return ForegroundServiceType.getSafeEnum((String) value);

        return defaultValue;
    }

    public ForegroundStartMode getValueOrDefault(
            @NonNull Map<String, Object> map,
            @NonNull String reference,
            @NonNull Class<ForegroundStartMode> type,
            @Nullable ForegroundStartMode defaultValue
    ){
        Object value = map.get(reference);
        if(value == null) return defaultValue;

        if(value instanceof String)
            return ForegroundStartMode.getSafeEnum((String) value);

        return defaultValue;
    }

    public GroupAlertBehaviour getValueOrDefault(
            @NonNull Map<String, Object> map,
            @NonNull String reference,
            @NonNull Class<GroupAlertBehaviour> type,
            @Nullable GroupAlertBehaviour defaultValue
    ){
        Object value = map.get(reference);
        if(value == null) return defaultValue;

        if(value instanceof String)
            return GroupAlertBehaviour.getSafeEnum((String) value);

        return defaultValue;
    }

    public GroupSort getValueOrDefault(
            @NonNull Map<String, Object> map,
            @NonNull String reference,
            @NonNull Class<GroupSort> type,
            @Nullable GroupSort defaultValue
    ){
        Object value = map.get(reference);
        if(value == null) return defaultValue;

        if(value instanceof String)
            return GroupSort.getSafeEnum((String) value);

        return defaultValue;
    }

    public LogLevel getValueOrDefault(
            @NonNull Map<String, Object> map,
            @NonNull String reference,
            @NonNull Class<LogLevel> type,
            @Nullable LogLevel defaultValue
    ){
        Object value = map.get(reference);
        if(value == null) return defaultValue;

        if(value instanceof String)
            return LogLevel.getSafeEnum((String) value);

        return defaultValue;
    }

    public MediaSource getValueOrDefault(
            @NonNull Map<String, Object> map,
            @NonNull String reference,
            @NonNull Class<MediaSource> type,
            @Nullable MediaSource defaultValue
    ){
        Object value = map.get(reference);
        if(value == null) return defaultValue;

        if(value instanceof String)
            return MediaSource.getSafeEnum((String) value);

        return defaultValue;
    }

    public NotificationCategory getValueOrDefault(
            @NonNull Map<String, Object> map,
            @NonNull String reference,
            @NonNull Class<NotificationCategory> type,
            @Nullable NotificationCategory defaultValue
    ){
        Object value = map.get(reference);
        if(value == null) return defaultValue;

        if(value instanceof String)
            return NotificationCategory.getSafeEnum((String) value);

        return defaultValue;
    }

    public NotificationImportance getValueOrDefault(
            @NonNull Map<String, Object> map,
            @NonNull String reference,
            @NonNull Class<NotificationImportance> type,
            @Nullable NotificationImportance defaultValue
    ){
        Object value = map.get(reference);
        if(value == null) return defaultValue;

        if(value instanceof String)
            return NotificationImportance.getSafeEnum((String) value);

        return defaultValue;
    }

    public NotificationLayout getValueOrDefault(
            @NonNull Map<String, Object> map,
            @NonNull String reference,
            @NonNull Class<NotificationLayout> type,
            @Nullable NotificationLayout defaultValue
    ){
        Object value = map.get(reference);
        if(value == null) return defaultValue;

        if(value instanceof String)
            return NotificationLayout.getSafeEnum((String) value);

        if(value instanceof NotificationLayout)
            return (NotificationLayout) value;

        return defaultValue;
    }

    public NotificationLifeCycle getValueOrDefault(
            @NonNull Map<String, Object> map,
            @NonNull String reference,
            @NonNull Class<NotificationLifeCycle> type,
            @Nullable NotificationLifeCycle defaultValue
    ){
        Object value = map.get(reference);
        if(value == null) return defaultValue;

        if(value instanceof String)
            return NotificationLifeCycle.getSafeEnum((String) value);

        if(value instanceof NotificationLifeCycle)
            return (NotificationLifeCycle) value;

        return defaultValue;
    }

    public NotificationPermission getValueOrDefault(
            @NonNull Map<String, Object> map,
            @NonNull String reference,
            @NonNull Class<NotificationPermission> type,
            @Nullable NotificationPermission defaultValue
    ){
        Object value = map.get(reference);
        if(value == null) return defaultValue;

        if(value instanceof String)
            return NotificationPermission.getSafeEnum((String) value);

        if(value instanceof NotificationPermission)
            return (NotificationPermission) value;

        return defaultValue;
    }

    public NotificationPrivacy getValueOrDefault(
            @NonNull Map<String, Object> map,
            @NonNull String reference,
            @NonNull Class<NotificationPrivacy> type,
            @Nullable NotificationPrivacy defaultValue
    ){
        Object value = map.get(reference);
        if(value == null) return defaultValue;

        if(value instanceof String)
            return NotificationPrivacy.getSafeEnum((String) value);

        if(value instanceof NotificationPrivacy)
            return (NotificationPrivacy) value;

        return defaultValue;
    }

    public NotificationSource getValueOrDefault(
            @NonNull Map<String, Object> map,
            @NonNull String reference,
            @NonNull Class<NotificationSource> type,
            @Nullable NotificationSource defaultValue
    ){
        Object value = map.get(reference);
        if(value == null) return defaultValue;

        if(value instanceof String)
            return NotificationSource.getSafeEnum((String) value);

        if(value instanceof NotificationSource)
            return (NotificationSource) value;

        return defaultValue;
    }

    public TimeZone getValueOrDefault(
            @NonNull Map<String, Object> map,
            @NonNull String reference,
            @NonNull Class<TimeZone> type,
            @Nullable TimeZone defaultValue
    ){
        Object value = map.get(reference);
        if(value == null) return defaultValue;

        return serializableUtils.deserializeTimeZone((String) value);
    }

    public Calendar getValueOrDefault(
            @NonNull Map<String, Object> map,
            @NonNull String reference,
            @NonNull Class<Calendar> type,
            @Nullable Calendar defaultValue
    ){
        Object value = map.get(reference);
        if(value == null) return defaultValue;

        if(value instanceof String)
            return serializableUtils.deserializeCalendar((String) value);

        return defaultValue;
    }

    public String getValueOrDefault(
            @NonNull Map<String, Object> map,
            @NonNull String reference,
            @NonNull Class<String> type,
            @Nullable String defaultValue
    ){
        Object value = map.get(reference);
        if(value == null) return defaultValue;

        if(value instanceof String)
            return (String) value;

        return value.toString();
    }

    public Integer getValueOrDefault(
            @NonNull Map<String, Object> map,
            @NonNull String reference,
            @NonNull Class<Integer> type,
            @Nullable Integer defaultValue
    ){
        Object value = map.get(reference);
        if(value == null) return defaultValue;

        if(value instanceof Number)
            return ((Number) value).intValue();

        return defaultValue;
    }

    public Float getValueOrDefault(
            @NonNull Map<String, Object> map,
            @NonNull String reference,
            @NonNull Class<Float> type,
            @Nullable Float defaultValue
    ){
        Object value = map.get(reference);
        if(value == null) return defaultValue;

        if(value instanceof Number)
            return ((Number) value).floatValue();

        if(value instanceof String)
            try {
                return Float.parseFloat((String) value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }

        return defaultValue;
    }

    public Double getValueOrDefault(
            @NonNull Map<String, Object> map,
            @NonNull String reference,
            @NonNull Class<Double> type,
            @Nullable Double defaultValue
    ){
        Object value = map.get(reference);
        if(value == null) return defaultValue;

        if(value instanceof Number)
            return ((Number) value).doubleValue();

        if(value instanceof String)
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }

        return defaultValue;
    }

    public Long getValueOrDefault(
            @NonNull Map<String, Object> map,
            @NonNull String reference,
            @NonNull Class<Long> type,
            @Nullable Long defaultValue
    ){
        Object value = map.get(reference);
        if(value == null) return defaultValue;

        if(value instanceof Number)
            return ((Number) value).longValue();

        if(type == Long.class && value instanceof String){
            Pattern pattern = Pattern.compile("(0x|#)(\\w{2})?(\\w{6})", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher((String) value);

            // 0x000000 hexadecimal color conversion
            if(matcher.find()) {
                String transparency = matcher.group(2);
                String textValue = (transparency == null ? "FF" : transparency) + matcher.group(3);
                long finalValue = 0L;
                if(!StringUtils.getInstance().isNullOrEmpty(textValue)){
                    finalValue += Long.parseLong(textValue, 16);
                }
                return type.cast(finalValue);
            }
        }

        return defaultValue;
    }

    public Short getValueOrDefault(
            @NonNull Map<String, Object> map,
            @NonNull String reference,
            @NonNull Class<Short> type,
            @Nullable Short defaultValue
    ){
        Object value = map.get(reference);
        if(value == null) return defaultValue;

        if(value instanceof Number)
            return ((Number) value).shortValue();

        return defaultValue;
    }

    public Byte getValueOrDefault(
            @NonNull Map<String, Object> map,
            @NonNull String reference,
            @NonNull Class<Byte> type,
            @Nullable Byte defaultValue
    ){
        Object value = map.get(reference);
        if(value == null) return defaultValue;

        if(value instanceof Number)
            return ((Number) value).byteValue();

        return defaultValue;
    }

    public Boolean getValueOrDefault(
            @NonNull Map<String, Object> map,
            @NonNull String reference,
            @NonNull Class<Boolean> type,
            @Nullable Boolean defaultValue
    ){
        Object value = map.get(reference);
        if(value == null) return defaultValue;

        if(value instanceof Boolean)
            return (Boolean) value;

        return defaultValue;
    }

    @SuppressWarnings("unchecked")
    public long[] getValueOrDefault(
            @NonNull Map<String, Object> map,
            @NonNull String reference,
            @NonNull Class<long[]> type,
            @Nullable long[] defaultValue
    ){
        Object value = map.get(reference);
        if(value == null) return defaultValue;

        if(value instanceof List)
            return Longs.toArray((List)value);

        if(value instanceof long[])
            return (long[]) value;

        return defaultValue;
    }

    public List<Calendar> getValueOrDefaultCalendarList(
            @NonNull Map<String, Object> map,
            @NonNull String reference,
            @Nullable List<Calendar> defaultValue
    ){
        Object value = map.get(reference);
        if(value == null) return defaultValue;

        if (!List.class.isAssignableFrom(value.getClass())) {
            return defaultValue;
        }

        List<String> dateStrings = (List) value;
        List<Calendar> calendars = new ArrayList<>();

        for (String dateString : dateStrings) {
            Calendar calendar = serializableUtils.deserializeCalendar(dateString);
            calendars.add(calendar);
        }
        return calendars;
    }

    public List<String> getValueOrDefaultListString(
            @NonNull Map<String, Object> map,
            @NonNull String reference,
            @Nullable List<String> defaultValue
    ) {
        Object value = map.get(reference);

        // Initialize JsonUtils for parsing
        JsonUtils<Object> jsonUtils = new JsonUtils<>();

        // If the value is a JSON string, try to parse it as a List
        if (value instanceof String) {
            String stringValue = (String) value;

            // Handle JSON arrays represented as a string
            if (stringValue.startsWith("[")) {
                Object parsedObject = jsonUtils.fromJson(stringValue);

                // Check if the parsed object is a List of Strings
                if (parsedObject instanceof List) {
                    List<?> list = (List<?>) parsedObject;

                    // Ensure that the list contains only Strings
                    if (!list.isEmpty() && list.get(0) instanceof String) {
                        return (List<String>) list;
                    }
                }
            }

            // Handle comma-separated string values
            String[] items = stringValue.split(",");
            List<String> resultList = new ArrayList<>();
            for (String item : items) {
                resultList.add(item.trim());
            }

            return resultList.isEmpty() ? defaultValue : resultList;
        }

        // If the value is already a List, return it directly after type checking
        if (value instanceof List) {
            List<?> list = (List<?>) value;

            // Ensure the list contains Strings
            if (!list.isEmpty() && list.get(0) instanceof String) {
                return (List<String>) list;
            }
        }

        return defaultValue;
    }

    public List<Map<String, Object>> getValueOrDefaultListMap(
            @NonNull Map<String, Object> map,
            @NonNull String reference,
            @Nullable List<Map<String, Object>> defaultValue
    ) {
        Object value = map.get(reference);
        if (value == null) return defaultValue;

        // If the value is already a List, return it directly after type checking
        if (value instanceof List) {
            List<?> list = (List<?>) value;

            // Ensure the list contains Map<String, Object>
            if (!list.isEmpty() && list.get(0) instanceof Map) {
                return (List<Map<String, Object>>) list;
            } else {
                return defaultValue;
            }
        }

        // If the value is a JSON string, convert it to a List<Map<String, Object>>
        if (value instanceof String) {
            String stringValue = (String) value;

            // Handling JSON list
            if (stringValue.startsWith("[")) {
                JsonUtils<Object> jsonUtils = new JsonUtils<>();
                Object parsedObject = jsonUtils.fromJson(stringValue);

                // Ensure the parsed object is a List of Maps
                if (parsedObject instanceof List) {
                    List<?> list = (List<?>) parsedObject;

                    // Ensure the list contains Map<String, Object>
                    if (!list.isEmpty() && list.get(0) instanceof Map) {
                        return (List<Map<String, Object>>) list;
                    }
                }
            }
            return defaultValue;
        }

        return defaultValue;
    }

    public Map<String, Object> getValueOrDefaultMap(
            @NonNull Map<String, Object> map,
            @NonNull String reference,
            @Nullable Map<String, Object> defaultValue
    ) {
        // Retrieve the value associated with the reference key
        Object value = map.get(reference);

        // If the value is a string, try to parse it as a JSON object
        if (value instanceof String) {
            JsonUtils<Object> jsonUtils = new JsonUtils<>();
            // Parse the string to an object
            value = jsonUtils.fromJson((String) value);
        }

        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }

        // If the value is not a string or the parsed object is not a map, return the default value
        return defaultValue;
    }

    public Map<String, String> getValueOrDefaultStringMap(
            @NonNull Map<String, Object> map,
            @NonNull String reference,
            @Nullable Map<String, String> defaultValue
    ) {
        // Retrieve the value associated with the reference key
        Object value = map.get(reference);

        // If the value is a string, try to parse it as a JSON object
        if (value instanceof String) {
            JsonUtils<Object> jsonUtils = new JsonUtils<>();
            // Parse the string to an object
            value = jsonUtils.fromJson((String) value);
        }

        if (value instanceof Map) {
            //noinspection unchecked
            Map<String, Object> mapObj = (Map<String, Object>) value;

            // Convert the parsed map into a Map<String, String>
            Map<String, String> stringMap = new HashMap<>();
            for (Map.Entry<String, Object> entry : mapObj.entrySet()) {
                if (entry.getValue() instanceof String) {
                    stringMap.put(entry.getKey(), (String) entry.getValue());
                }
            }

            return stringMap.isEmpty() ? defaultValue : stringMap;
        }

        // If the value is not a string or the parsed object is not a map, return the default value
        return defaultValue;
    }

    public abstract void validate(Context context) throws AwesomeNotificationsException;

}
