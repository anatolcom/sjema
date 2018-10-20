package ru.anatol.sjema;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.nio.charset.Charset;

public class JsonUtil {

    //---------------------------------------------------------------------------
    private static JSONTokener getJSONTokener(byte[] content, Charset charset) throws JSONException {
        return new JSONTokener(new String(BomUtil.removeBOM(content), charset));
    }

    //---------------------------------------------------------------------------
    public static JSONObject parseJsonAsObject(byte[] content, Charset charset) throws JSONException {
        return new JSONObject(getJSONTokener(content, charset));
    }


    //---------------------------------------------------------------------------
    public static JSONArray parseJsonAsArray(byte[] content, Charset charset) throws JSONException {
        return new JSONArray(getJSONTokener(content, charset));
    }

    //---------------------------------------------------------------------------
    public static JSONObject getJSONObject(JSONObject json, String key, boolean required) throws JSONException {
        if (json == null) throw new IllegalArgumentException("json = null");
        if (json.has(key)) return json.getJSONObject(key);
        if (required) throw new JSONException("value with key: \"" + key + "\" not found");
        return null;
    }

    //---------------------------------------------------------------------------
    public static JSONArray getJSONArray(JSONObject json, String key, boolean required) throws JSONException {
        if (json == null) throw new IllegalArgumentException("json = null");
        if (json.has(key)) return json.getJSONArray(key);
        if (required) throw new JSONException("value with key: \"" + key + "\" not found");
        return null;
    }

    //---------------------------------------------------------------------------
    public static Object get(JSONObject json, String key, boolean required, Object alter) throws JSONException {
        if (json == null) throw new IllegalArgumentException("json = null");
        if (json.has(key)) return json.get(key);
        if (required) throw new JSONException("value with key: \"" + key + "\" not found");
        return alter;
    }

    //---------------------------------------------------------------------------
    public static Object get(JSONObject json, String key, boolean required) throws JSONException {
        return get(json, key, required, null);
    }

    //---------------------------------------------------------------------------
    public static String getString(JSONObject json, String key, boolean required, String alter) throws JSONException {
        if (json == null) throw new IllegalArgumentException("json = null");
        if (json.has(key)) return json.getString(key);
        if (required) throw new JSONException("value with key: \"" + key + "\" not found");
        return alter;
    }

    //---------------------------------------------------------------------------
    public static String getString(JSONObject json, String key, boolean required) throws JSONException {
        return getString(json, key, required, null);
    }

    //---------------------------------------------------------------------------
    public static Integer getInt(JSONObject json, String key, boolean required, Integer alt) throws JSONException {
        if (json == null) throw new IllegalArgumentException("json = null");
        if (json.has(key)) return json.getInt(key);
        if (required) throw new JSONException("value with key: \"" + key + "\" not found");
        return alt;
    }

    //---------------------------------------------------------------------------
    public static Integer getInt(JSONObject json, String key, boolean required) throws JSONException {
        return getInt(json, key, required, null);
    }

    //---------------------------------------------------------------------------
    public static Long getLong(JSONObject json, String key, boolean required, Long alt) throws JSONException {
        if (json == null) throw new IllegalArgumentException("json = null");
        if (json.has(key)) return json.getLong(key);
        if (required) throw new JSONException("value with key: \"" + key + "\" not found");
        return alt;
    }

    //---------------------------------------------------------------------------
    public static Long getLong(JSONObject json, String key, boolean required) throws JSONException {
        return getLong(json, key, required, null);
    }

    //---------------------------------------------------------------------------
    public static Boolean getBoolean(JSONObject json, String key, boolean required, Boolean alt) throws JSONException {
        if (json == null) throw new IllegalArgumentException("json = null");
        if (json.has(key)) return json.getBoolean(key);
        if (required) throw new JSONException("value with key: \"" + key + "\" not found");
        return alt;
    }

    //---------------------------------------------------------------------------
    public static Boolean getBoolean(JSONObject json, String key, boolean required) throws JSONException {
        return getBoolean(json, key, required, null);
    }

    //---------------------------------------------------------------------------
//    public static List<String> jsonArrayStrToStrings(String jsonArrayStr) throws JSONException {
//        JSONArray jsonArray = getJSONArray(jsonArrayStr);
//        List<String> strings = new ArrayList<>();
//        for (int q = 0; q < jsonArray.length(); q++) strings.add(jsonArray.getString(q));
//        return strings;
//    }
//---------------------------------------------------------------------------
}
