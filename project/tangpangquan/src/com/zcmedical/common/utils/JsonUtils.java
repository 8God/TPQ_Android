package com.zcmedical.common.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class JsonUtils {

    public static int getErrorCode(String jsonStr) {
        int error_code = 1;
        try {
            JSONObject jo = new JSONObject(jsonStr);
            error_code = jo.getInt("error_code");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return error_code;
    }

    public static int getCount(String jsonStr) {
        int count = 0;
        try {
            JSONObject jo = new JSONObject(jsonStr);
            count = jo.getInt("count");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public static String getErrorMessage(String jsonStr) {
        String error_code = "获取数据错误";
        try {
            JSONObject jo = new JSONObject(jsonStr);
            error_code = jo.getString("error_message");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return error_code;
    }

    public static JSONObject getDataAsJSONObject(String jsonStr) {
        if (jsonStr != null) {
            try {
                JSONObject responseJo = new JSONObject(jsonStr);
                if (responseJo.has("data")) {
                    return responseJo.getJSONObject("data");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static JSONArray getDataAsJSONArray(String jsonStr) {
        if (jsonStr != null) {
            try {
                JSONObject responseJo = new JSONObject(jsonStr);
                if (responseJo.has("data")) {
                    return responseJo.getJSONArray("data");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public static JSONArray getJsonArray(JSONObject jo, String key) {
        JSONArray jsonArray = null;
        try {
            jsonArray = jo.getJSONArray(key);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return jsonArray;
    }

    public static int getJsonInt(JSONObject jo, String key) {
        int i = 0;
        try {
            i = jo.getInt(key);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return i;
    }

    public static int getJsonInt(JSONObject jo, String key, int defaultInt) {
        int i;
        try {
            i = jo.getInt(key);
        } catch (Exception e) {
            // e.printStackTrace();
            i = defaultInt;
        }
        return i;
    }

    public static boolean getJsonBol(JSONObject jo, String key) {
        boolean isBool = false;
        try {
            isBool = jo.getBoolean(key);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return isBool;
    }

    public static String getJsonString(JSONObject jo, String key) {
        String str = null;
        try {
            str = jo.getString(key);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return str;
    }

    public static JSONObject getJsonobject(JSONArray array, int index) {
        JSONObject jo = null;
        try {
            jo = array.getJSONObject(index);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
        }
        return jo;
    }


    public static JSONObject getJson(String result) {
        JSONObject object = null;
        try {
            object = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    public static String getResult(String jsonStr) {
        String result = "";
        try {
            JSONObject jo = new JSONObject(jsonStr);
            result = jo.getString("result");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getOjectString(String jsonStr , String objName) {
        String result = "";
        try {
            JSONObject jo = new JSONObject(getResult(jsonStr));
            result = jo.getString(objName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getSthObjectString(String jsonStr , String objName) {
        String result = "";
        try {
            JSONObject jo = new JSONObject(jsonStr);
            result = jo.getString(objName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static int getStatus(String jsonStr) {
        int status = 0;
        try {
            JSONObject jo = new JSONObject(jsonStr);
            status = jo.getInt("status");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }

}
