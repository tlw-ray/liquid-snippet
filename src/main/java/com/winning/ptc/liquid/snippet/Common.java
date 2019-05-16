package com.winning.ptc.liquid.snippet;

import com.mongodb.client.model.Filters;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.CompositeResourceAccessor;
import liquibase.resource.FileSystemResourceAccessor;
import liquibase.resource.ResourceAccessor;
import org.bson.conversions.Bson;

import java.util.UUID;

public class Common {
    public static ResourceAccessor getResourceAccessor(){
        Thread currentThread = Thread.currentThread();
        ClassLoader contextClassLoader = currentThread.getContextClassLoader();
        ResourceAccessor threadClFO = new ClassLoaderResourceAccessor(contextClassLoader);
        ResourceAccessor clFO = new ClassLoaderResourceAccessor();
        ResourceAccessor fsFO = new FileSystemResourceAccessor();
        return new CompositeResourceAccessor(clFO, fsFO, threadClFO);
    }

    public static String generateUUID(){
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replaceAll("-", "");
    }

    public static Bson createUuidFilter(String uuid){
        return Filters.eq("_id", uuid);
    }
}
