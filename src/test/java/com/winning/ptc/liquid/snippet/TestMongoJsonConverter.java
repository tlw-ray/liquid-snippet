package com.winning.ptc.liquid.snippet;

import org.junit.Assert;
import org.junit.Test;

public class TestMongoJsonConverter {
    @Test
    public void testEncode(){
        //原有JSON
        String jsonStringOrigin = "{\"na.me\":\"name1.\",\"cls\":{\"na.me\":\"na.me2\"}}";
        //对字段名中包含.的部分编码后的JSON
        String jsonStringEncoded = "{\"na__d__me\":\"name1.\",\"cls\":{\"na__d__me\":\"na.me2\"}}";
        //JSON 头部转换器
        MongoJsonConverter mongoJsonConverter = new MongoJsonConverter();

        //Test encode
        String jsonStringEncoded_ = mongoJsonConverter.encode(jsonStringOrigin);
        System.out.println("JSON Encoded: " + jsonStringEncoded_);
        Assert.assertEquals(jsonStringEncoded, jsonStringEncoded_);

        //Test decode
        String jsonStringDecoded_ = mongoJsonConverter.decode(jsonStringEncoded);
        System.out.println("JSON Decoded: " + jsonStringDecoded_);
        Assert.assertEquals(jsonStringOrigin, jsonStringDecoded_);
    }

}
