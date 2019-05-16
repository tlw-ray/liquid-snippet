package com.winning.ptc.liquid.snippet;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 转换JSON的key中的.为其它字符
 */
public class JSONKeyConverter {

    private String DOT_ESCAPE = "__d__";
    public JSONObject encode(JSONObject jsonObject){
        return process(jsonObject, "\\.", DOT_ESCAPE);
    }

    public JSONObject decode(JSONObject jsonObject){
        return process(jsonObject, DOT_ESCAPE, ".");
    }

    private JSONObject process(JSONObject jsonObject, String regex, String replacement){
        Map<String, Object> contentMap = new HashMap();
        for(String key : jsonObject.keySet()){
            String newKey = key.replaceAll(regex, replacement);
            Object newValue = jsonObject.get(key);
            if(newValue instanceof JSONObject){
                newValue = encode((JSONObject)newValue);
            }
            contentMap.put(newKey, newValue);
        }
        return new JSONObject(contentMap);
    }

    @FunctionalInterface
    interface KeyConverter{
        String convert(String regex, String replaced);
    }
}
