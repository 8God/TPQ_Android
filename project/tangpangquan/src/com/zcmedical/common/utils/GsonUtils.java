package com.zcmedical.common.utils;

import java.io.Reader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.zcmedical.tangpangquan.BuildConfig;

/**
 * <b>ClassName:</b> GsonUtils.java </br>
 * <b>Description:</b> Json转换工具 </br>
 * <b>Usage:</b> </br>
 * <b>Create Date:</b> 2014-7-10 </br>
 * <b>Update Date:</b> 2014-7-10 </br>
 * <b>Creator:</b> issaclam </br>
 * <b>Updator:</b> issaclam </br>
 */
public class GsonUtils {

    private static final String TAG = "GsonUtils.fromJson";

    private static Gson gson = new Gson();

    /**
     * 判断是否为json
     * @param json
     * @return true if string is Json
     */
    public static boolean isJson(String string) {
        boolean blnJson = !TextUtils.isEmpty(string);
        if (blnJson) {
            try {
                new JSONObject(string);
            } catch (JSONException e) {
                blnJson = false;
            }
        }
        return blnJson;
    }

    /**
     * Json转为Class
     * @param json
     * @param clazz
     * @return
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        Log.d("test", "is json? " + isJson(json));
        if (!isJson(json)) {
            return null;
        }
        if (BuildConfig.DEBUG) {
            Log.v(TAG, "json string =>>\n" + json);
        }

        T result = gson.fromJson(json, clazz);
        if (null != result) {
            if (BuildConfig.DEBUG) {
                Log.v(TAG, "result bean =>>\n" + result);
            }
        }
        return result;
    }

    public static <T> T fromType(String json, Type type) {
        if (!isJson(json)) {
            return null;
        }
        if (BuildConfig.DEBUG) {
            Log.v(TAG, "json string =>>\n" + json);
        }

        T result = gson.fromJson(json, type);
        if (null != result) {
            if (BuildConfig.DEBUG) {
                Log.v(TAG, "result bean =>>\n" + result);
            }
        }
        return result;
    }

    /**
     * Json转为List<>
     * @param json
     * @param clazz
     * @return
     */
    public static <T> List<T> fromJson2List(String json, Class<T> clazz) {
        return fromType2List(json,
                ((ParameterizedType) clazz.getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
    }

    /**
     * Json转为List<>
     * @param json
     * @param type
     * @return
     */
    public static <T> List<T> fromType2List(String json, Type type) {
        if (!isJson(json)) {
            return null;
        }
        if (BuildConfig.DEBUG) {
            Log.v(TAG, "json string =>>\n" + json);
        }

        List<T> result = gson.fromJson(json, type);
        if (null != result) {
            if (BuildConfig.DEBUG) {
                Log.v(TAG, "result bean =>>\n" + result);
            }
        }
        return result;
    }

    /**
     * 从Class转为String
     * @param jsonObject
     * @return Json String
     */
    public static String toJson(Object jsonObject) {
        String json = null;
        if (jsonObject != null) {
            if (jsonObject instanceof String) {
                json = (String) jsonObject;
            } else if (jsonObject instanceof JSONObject || jsonObject instanceof JSONArray) {
                json = jsonObject.toString();
            } else {
                json = gson.toJson(jsonObject);
            }
        }
        return json;
    }

    public static <T> T fromType(Reader reader, Type type) {
        T result = null;
        try {
            result = gson.fromJson(reader, type);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JsonIOException e) {
            e.printStackTrace();
        }
        if (null != result) {
            if (BuildConfig.DEBUG) {
                Log.v(TAG, "result bean =>>\n" + result);
            }
        }
        return result;
    }
}
