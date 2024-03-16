package me.carda.awesome_notifications.core.models;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.primitives.Longs;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
import me.carda.awesome_notifications.core.logs.Logger;
import me.carda.awesome_notifications.core.utils.JsonUtils;
import me.carda.awesome_notifications.core.utils.SerializableUtils;
import me.carda.awesome_notifications.core.utils.StringUtils;

public abstract class AbstractModel implements Cloneable {
    static final String TAG = "AbstractModel";
    protected final SerializableUtils serializableUtils;
    protected final StringUtils stringUtils;

    public static Map<String, Object> defaultValues = new HashMap<>();
    private final Gson gson = new Gson();

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
        return JsonUtils.toJson(this.toMap());
    }

    protected AbstractModel templateFromJson(String json) {
        if(json == null || json.isEmpty()) return null;
        Map<String, Object> map = JsonUtils.fromJson(json);
        return this.fromMap(map);
    }

    public AbstractModel getClone () {
        try {
            return (AbstractModel)this.clone();
        }
        catch (CloneNotSupportedException ex) {
            String message =  ex.getMessage();
            if (message == null) message = "unknown";
            Logger.getInstance().e(TAG, message, ex);
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
            @Nullable List<?> value
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
            @Nullable Map<?,?> value
    ){
        if (value == null) return;
        if (value.isEmpty()) return;

        Map<String, Object> serializedMap = new HashMap<>();
        for(Map.Entry<?,?> objEntry : value.entrySet()) {
            Object innerValue = objEntry.getValue();
            if (innerValue != null)
                if (innerValue instanceof AbstractModel)
                    serializedMap.put((String) objEntry.getKey(), ((AbstractModel)innerValue).toMap());
                else
                    serializedMap.put((String) objEntry.getKey(), innerValue);
        }

        mapData.put(
                reference,
                serializedMap);
    }

    // ***********************  INPUT SERIALIZATION METHODS   *********************************

    public <T extends SafeEnum> T getValueOrDefaultSafeEnum(
            @NonNull Map<String, Object> map,
            @NonNull String reference,
            @NonNull Class<T> enumClass,
            @NonNull T[] elements,
            @Nullable T defaultValue
    ){
        Object value = map.get(reference);
        if(value == null) return defaultValue;
        if(enumClass.isInstance(value)) return enumClass.cast(value);
        if(value instanceof String) {
            for (T element : elements) {
                if (element.getSafeName().equals((String) value)){
                    return element;
                }
            }
        }
        return defaultValue;
    }

    @Nullable
    public Serializable getValueOrDefault(
            @NonNull Map<String, Object> map,
            @NonNull String reference,
            @NonNull Class<Serializable> type,
            @Nullable Serializable defaultValue
    ){
        Object value = map.get(reference);
        if(value == null) return defaultValue;
        if(value instanceof Serializable) return (Serializable) value;
        return defaultValue;
    }

    @Nullable
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

    @Nullable
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

    @Nullable
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

    @Nullable
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

    @Nullable
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

    @Nullable
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

    @Nullable
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

    @Nullable
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

    @Nullable
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

    @Nullable
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

    @Nullable
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
            return Longs.toArray((List<Number>)value);

        if(value instanceof long[])
            return (long[]) value;

        return defaultValue;
    }

    @Nullable
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

        List<?> dateStrings = (List<?>) value;
        List<Calendar> calendars = new ArrayList<>();

        for (Object object : dateStrings) {
            if (!(object instanceof String)) continue;
            Calendar calendar = serializableUtils.deserializeCalendar((String) object);
            calendars.add(calendar);
        }
        return calendars;
    }

    @Nullable
    public <T> List<T> getValueOrDefaultList(
            @NonNull Map<String, Object> map,
            @NonNull String reference,
            @NonNull Class<T> clazz,
            @Nullable List<T> defaultValue
    ) {
        Object value = map.get(reference);
        if (value == null) return defaultValue;

        if (value instanceof List<?>) {
            final List<?> rawList = (List<?>) value;
            final List<T> response = new ArrayList<>();
            for (Object object : rawList) {
                if (object == null || !clazz.isInstance(object)) continue;
                response.add(clazz.cast(object));
            }
            return response;
        }

        if (value instanceof String) {
            String stringValue = (String) value;
            if (stringValue.startsWith("[")) {
                // Handling the case where the value is already a list
                Type listType = new TypeToken<List<T>>(){}.getType();
                try {
                    List<T> list = gson.fromJson(stringValue, listType);
                    return list != null ? list : defaultValue;
                } catch (Exception e) {
                    return defaultValue;
                }
            }
            String[] items = stringValue.split(",");
            List<T> resultList = new ArrayList<>();
            for (String item : items) {
                try {
                    T castedItem = clazz.cast(item.trim());
                    resultList.add(castedItem);
                } catch (ClassCastException e) {
                    String message = e.getMessage();
                    if (message == null) message = "unknown";
                    Logger.getInstance().e(TAG, message, e);
                }
            }
            return resultList.isEmpty() ? defaultValue : resultList;
        }
        return defaultValue;
    }

    @Nullable
    public <T, K> Map<T, K> getValueOrDefaultMap(
            @NonNull Map<String, Object> map,
            @NonNull String reference,
            @Nullable Map<T, K> defaultValue
    ) {
        Object value = map.get(reference);
        if (value == null) {
            return defaultValue;
        }

        Type mapType = new TypeToken<Map<T, K>>(){}.getType();
        try {
            Map<T, K> mapObj = gson.fromJson(gson.toJson(value), mapType);
            return mapObj != null ? mapObj : defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public abstract void validate(Context context) throws AwesomeNotificationsException;

}
