package com.winning.ptc.liquid.snippet;

import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.CompositeResourceAccessor;
import liquibase.resource.FileSystemResourceAccessor;
import liquibase.resource.ResourceAccessor;

public class Common {
    public static ResourceAccessor getResourceAccessor(){
        Thread currentThread = Thread.currentThread();
        ClassLoader contextClassLoader = currentThread.getContextClassLoader();
        ResourceAccessor threadClFO = new ClassLoaderResourceAccessor(contextClassLoader);
        ResourceAccessor clFO = new ClassLoaderResourceAccessor();
        ResourceAccessor fsFO = new FileSystemResourceAccessor();
        return new CompositeResourceAccessor(clFO, fsFO, threadClFO);
    }
}
