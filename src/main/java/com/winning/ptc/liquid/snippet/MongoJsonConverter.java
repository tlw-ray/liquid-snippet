package com.winning.ptc.liquid.snippet;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * MongoDB不支持json在字段名中包含'.', '$', '\\'等特殊字符
 * 参考: https://docs.mongodb.com/manual/core/document/
 * 要将Liquibase产生的JSON格式的Snapshot中包含'.'字符
 * 为了将这种json存储入MongoDB
 * 需要先将JSON的key中的'.'在存入前进行转换才能存储，否则会报异常。
 * 读取MongoDB中的snapshot后需要做转化才能够使用。
 * 目前是将'.'与"__d__"相互替换。
 */
public class MongoJsonConverter {

    private String DOT_ESCAPE = "__d__";

    public String encode(String json){
        return encode((JSONObject)JSONObject.parse(json)).toJSONString();
    }

    public String decode(String json){
        return decode((JSONObject)JSONObject.parse(json)).toJSONString();
    }

    protected JSONObject encode(JSONObject jsonObject){
        return process(jsonObject, "\\.", DOT_ESCAPE);
    }

    protected JSONObject decode(JSONObject jsonObject){
        return process(jsonObject, DOT_ESCAPE, ".");
    }

    private JSONObject process(JSONObject jsonObject, String regex, String replacement){
        Map<String, Object> contentMap = new HashMap();
        for(String key : jsonObject.keySet()){
            String newKey = key.replaceAll(regex, replacement);
            Object newValue = jsonObject.get(key);
            if(newValue instanceof JSONObject){
                newValue = process((JSONObject)newValue, regex, replacement);
            }
            contentMap.put(newKey, newValue);
        }
        return new JSONObject(contentMap);
    }
}
