package personalprojects.seakyluo.randommenu.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class JsonUtils {

    private static final Gson gson = new Gson();

    public static String toJson(Object obj){
        try {
            return gson.toJson(obj);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T fromJson(String json, TypeToken<T> token){
        try {
            return gson.fromJson(json, token.getType());
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T fromJson(String json, Class<T> claz){
        try {
            return gson.fromJson(json, claz);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T copy(T obj){
        if (obj == null) return null;
        String json = toJson(obj);
        return fromJson(json, (Class<T>) obj.getClass());
    }
}
