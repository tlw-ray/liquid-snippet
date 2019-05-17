package com.winning.ptc.liquid.snippet.a07_flow_mongo;

import com.mongodb.MongoClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@SpringBootApplication
@Component
public class T02_SpringMongo {
    //通过SpringBoot注入芒果的连接
    public static void main(String[] args){
        ApplicationContext applicationContext = SpringApplication.run(T02_SpringMongo.class, args);
        MongoClient mongoClient = applicationContext.getBean(MongoClient.class);
        for(String name:mongoClient.listDatabaseNames()) {
            System.out.println(name);
        }
    }
}
