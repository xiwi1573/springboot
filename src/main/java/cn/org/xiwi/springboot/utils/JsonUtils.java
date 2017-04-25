package cn.org.xiwi.springboot.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

/**
 * Created by lenovo on 2016/8/2.
 */
public class JsonUtils {


    public static <T> T fromJson(ToolType type, String json, Class<T> classz) {
        T t = null;
        if (StringUtils.isEmpty(json)) {
            return t;
        }
        if (type == ToolType.GSON) {

            try {
                t = new Gson().fromJson(json, classz);
            }catch (JsonSyntaxException e){
                e.printStackTrace();
            }

        } else if (type == ToolType.FASTJSON) {
            try {
                t = JSON.parseObject(json, classz);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return t;
    }

    public static String toJson(ToolType type, Object obj) {
        if (type == ToolType.GSON) {

            return new Gson().toJson(obj);

        } else if (type == ToolType.FASTJSON) {
            try {
                return JSON.toJSONString(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static <T> List<T> getList(ToolType type, String jsonString, Class<T> cls) {
        List<T> list = null;
        if (type == ToolType.GSON) {

            try {
                list = new Gson().fromJson(jsonString, new TypeToken<List<T>>() {
                }.getType());
            }catch (JsonSyntaxException e){
                e.printStackTrace();
            }

        } else if (type == ToolType.FASTJSON) {
            try {
                list = JSON.parseArray(jsonString, cls);
            } catch (Exception e) {
                list = null;
                e.printStackTrace();
            }
        }
        return list;
    }

    public static <T> List<Map<String, T>> getListMap(ToolType type, String jsonString) {
        List<Map<String, T>> list = null;
        if (type == ToolType.GSON) {
            try {
                list = new Gson().fromJson(jsonString,new TypeToken<List<Map<String, T>>>() {}.getType());
            }catch (JsonSyntaxException e){
                e.printStackTrace();
            }
        } else if (type == ToolType.FASTJSON) {
            try {
                // 两种写法
                // list = JSON.parseObject(jsonString, new
                // TypeReference<List<Map<String, Object>>>(){}.getType());
                list = JSON.parseObject(jsonString, new TypeReference<List<Map<String, T>>>() {});
            } catch (Exception e) {
                list = null;
                e.printStackTrace();
            }
        }
        return list;
    }

    public static enum ToolType {
        GSON,
        FASTJSON
    }
}
