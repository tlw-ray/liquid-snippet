package com.winning.ptc.liquid.snippet.a07_system;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.DefaultJSONParser;

import java.util.Map;
import java.util.TreeMap;

public class Test02 {

    Map<String, Map> newJsonObject = new TreeMap();

    public static void main(String[] args){
        String json = "{'na.me':'aaa', cc: {dd: 'ee', ff: {gg : 'hh'}}}";
        DefaultJSONParser defaultJSONParser = new DefaultJSONParser(json);
        JSONObject jsonObject = (JSONObject)defaultJSONParser.parse(json);
        System.out.println(jsonObject);
        processJSON(jsonObject, "", (prefix, key) -> {
            String newKey = key.replaceAll("\\.", "___d___");
            System.out.print(prefix + key + " : ");
            return newKey;
        });
    }
    private static void processJSON(JSONObject jsonObject, String prefix, ProcessKey processKey){
        for(String key:jsonObject.keySet()){
            Object value = jsonObject.get(key);
            String newKey = processKey.process(prefix, key);

            if(value instanceof JSONObject){
                System.out.println();
                processJSON((JSONObject)value, prefix + "\t", processKey);
            }else{
                System.out.println(jsonObject.get(key));
            }
        }
    }

    @FunctionalInterface
    interface ProcessKey{
        String process(String prefix, String key);
    }
}
