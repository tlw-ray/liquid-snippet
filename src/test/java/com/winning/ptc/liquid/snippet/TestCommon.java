package com.winning.ptc.liquid.snippet;

import org.junit.Assert;
import org.junit.Test;

public class TestCommon {
    @Test
    public void testGenerateUUID(){
        String uuid = Common.generateUUID();
        System.out.println(uuid);
        Assert.assertEquals(32, uuid.length());
    }
}
