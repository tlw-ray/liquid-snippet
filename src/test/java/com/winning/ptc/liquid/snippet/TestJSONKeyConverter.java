package com.winning.ptc.liquid.snippet;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

public class TestJSONKeyConverter {
    @Test
    public void test(){
        String json = "{'na.me':'name1.', cls: {'na.me': 'na.me2'}}";
        JSONObject jsonObject = (JSONObject) JSONObject.parse(json);
        JSONKeyConverter jsonKeyConverter = new JSONKeyConverter();
        jsonObject = jsonKeyConverter.encode(jsonObject);
        System.out.println(jsonObject.toJSONString());
    }
}
