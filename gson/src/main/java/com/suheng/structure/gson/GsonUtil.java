package com.suheng.structure.gson;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class GsonUtil {

    public static <T> T jsonToObject(String json, Class<T> classOfT) {
        Gson gson = new Gson();
        return gson.fromJson(json, classOfT);
    }

    public static <T> T jsonToObject(String json, Type typeOfT) {
        Gson gson = new Gson();
        return gson.fromJson(json, typeOfT);
    }

    public static String objectToJson(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    public static <T> T jsonToObj(String json) {
        Gson gson = new Gson();
        T t = gson.fromJson(json, new TypeToken<T>() {
        }.getType());
        return t;
    }
}
