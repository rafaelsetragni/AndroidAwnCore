package me.carda.awesome_notifications.core.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.carda.awesome_notifications.core.exceptions.ExceptionCode;
import me.carda.awesome_notifications.core.exceptions.ExceptionFactory;

public class JsonUtils<T> {

    private static final String TAG = "JsonUtils";

    /**
     * Converts a JSON string into a Map<String, T> or List<T> based on the structure of the JSON.
     * Catches any exceptions and returns null if the operation fails.
     * @param jsonData The JSON string to convert.
     * @return A Map<String, T> or List<T> representing the JSON structure or null if an exception occurs.
     */
    public Object fromJson(String jsonData) {
        try {
            if (jsonData.trim().startsWith("{")) {
                JSONObject jsonObject = new JSONObject(jsonData);
                return jsonToMap(jsonObject);
            } else if (jsonData.trim().startsWith("[")) {
                JSONArray jsonArray = new JSONArray(jsonData);
                return jsonToList(jsonArray);
            }
        } catch (JSONException e) {
            ExceptionFactory
                    .getInstance()
                    .registerNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_INVALID_ARGUMENTS,
                            ExceptionCode.DETAILED_INVALID_FORMAT,
                            e);
        }
        return null;
    }

    /**
     * Converts a Map<String, T> or List<T> into a JSON string.
     * Catches any exceptions and returns null if the operation fails.
     * @param object The Map<String, T> or List<T> to convert.
     * @return A JSON string representing the object or null if an exception occurs.
     */
    public String toJson(Object object) {
        try {
            if (object instanceof Map) {
                JSONObject jsonObject = mapToJson((Map<String, T>) object);
                return jsonObject.toString();
            } else if (object instanceof List) {
                JSONArray jsonArray = listToJson((List<T>) object);
                return jsonArray.toString();
            }
        } catch (JSONException e) {
            ExceptionFactory
                    .getInstance()
                    .registerNewAwesomeException(
                            TAG,
                            ExceptionCode.CODE_INVALID_ARGUMENTS,
                            ExceptionCode.DETAILED_INVALID_FORMAT,
                            e);
        }
        return null;
    }

    // Helper method to convert JSONObject to Map<String, T>
    private Map<String, T> jsonToMap(JSONObject jsonObject) throws JSONException {
        Map<String, T> map = new HashMap<>();
        Iterator<String> keys = jsonObject.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            Object value = jsonObject.get(key);

            if (value instanceof JSONObject) {
                map.put(key, (T) jsonToMap((JSONObject) value));
            } else if (value instanceof JSONArray) {
                map.put(key, (T) jsonToList((JSONArray) value));
            } else {
                map.put(key, (T) value);
            }
        }

        return map;
    }

    // Helper method to convert JSONArray to List<T>
    private List<T> jsonToList(JSONArray array) throws JSONException {
        List<T> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONObject) {
                list.add((T) jsonToMap((JSONObject) value));
            } else if (value instanceof JSONArray) {
                list.add((T) jsonToList((JSONArray) value));
            } else {
                list.add((T) value);
            }
        }
        return list;
    }

    // Helper method to convert Map<String, T> to JSONObject
    private JSONObject mapToJson(Map<String, T> map) throws JSONException {
        JSONObject jsonObject = new JSONObject();

        for (Map.Entry<String, T> entry : map.entrySet()) {
            String key = entry.getKey();
            T value = entry.getValue();

            if (value instanceof Map) {
                jsonObject.put(key, mapToJson((Map<String, T>) value));
            } else if (value instanceof List) {
                jsonObject.put(key, listToJson((List<T>) value));
            } else {
                jsonObject.put(key, value);
            }
        }

        return jsonObject;
    }

    // Helper method to convert List<T> to JSONArray
    private JSONArray listToJson(List<T> list) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (T value : list) {
            if (value instanceof Map) {
                jsonArray.put(mapToJson((Map<String, T>) value));
            } else if (value instanceof List) {
                jsonArray.put(listToJson((List<T>) value));
            } else {
                jsonArray.put(value);
            }
        }
        return jsonArray;
    }
}