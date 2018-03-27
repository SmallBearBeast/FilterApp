package com.example.jason.heartratedetection.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2017/7/10.
 */

public class SPUtil {
    public static final String SETTING = "IM";

    /**
     * 将数据保存到 IM 文件中
     *
     * @param context
     * @param key
     * @param value
     */
    public static void saveToIM(Context context, String key, Object value) {
        String type = value.getClass().getSimpleName();
        SharedPreferences preferences = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        switch (type) {
            case "Boolean":
                editor.putBoolean(key, (Boolean) value);
                break;

            case "Integer":
                editor.putInt(key, (Integer) value);
                break;

            case "String":
                editor.putString(key, (String) value);
                break;
        }
        editor.commit();
    }

    /**
     * 从 IM 文件中 取出数据
     *
     * @param context
     * @param key
     * @param defaultValue
     * @return
     */
    public static Object getFromIM(Context context, String key, Object defaultValue) {
        String type = defaultValue.getClass().getSimpleName();
        SharedPreferences preferences = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE);
        Object result = null;
        switch (type) {
            case "Boolean":
                result = preferences.getBoolean(key, (Boolean) defaultValue);
                break;

            case "Integer":
                result = preferences.getInt(key, (Integer) defaultValue);
                break;

            case "String":
                result = preferences.getString(key, (String) defaultValue);
                break;
        }
        return result;
    }
}

