package com.winning.ptc.liquid.snippet.a07_system;


import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

public class T01_WriteMongo {
    static MongoClient mongoClient = new MongoClient("172.16.6.161", 27017);
    static MongoDatabase mongoDatabase = mongoClient.getDatabase("ods_dbv");
    static MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("snapshot");
    public static void main(String[] args){
        //SQLServer UUID 32位
//        String uuid = "8a80cb816a86d8eb016a86dca6e30001";
        //Mongo UUID 24位
        String uuid = "5ccf97a691a8dd0006199ba0";

        //Insert
        insert(uuid);

        //find
//        Document document = find(uuid);
//        System.out.println(document);

        //delete
//        delete(uuid);

        mongoClient.close();
    }

    private static Document find(String uuid){
        Bson filter = createFilter(uuid);
        FindIterable<Document> findIterable = mongoCollection.find(filter);
        return findIterable.first();
    }

    private static void insert(String uuid){
        Document document = Document.parse("{'name':'test', 'age':30}");
        document.put("_id", uuid);
        mongoCollection.insertOne(document);
    }

    private static void delete(String uuid){
        Bson filter = createFilter(uuid);
        mongoCollection.deleteOne(filter);
    }

    private static Bson createFilter(String uuid){
        return Filters.eq("_id", uuid);
    }
}
