package com.winning.ptc.liquid.snippet.a07_system;

public class Test01 {
    public static void main(String[] args){
        String json = "na.me";
        String e1 = T01_WriteMongo.escapeEncode(json);
        System.out.println(e1);

        String e2 = T01_WriteMongo.escapeDecode(json);
        System.out.println(e2);
    }
}
