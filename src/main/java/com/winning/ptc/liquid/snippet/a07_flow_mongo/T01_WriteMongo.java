package com.winning.ptc.liquid.snippet.a07_flow_mongo;


import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.winning.ptc.liquid.snippet.Common;
import com.winning.ptc.liquid.snippet.MongoJsonConverter;
import liquibase.CatalogAndSchema;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.ObjectQuotingStrategy;
import liquibase.exception.DatabaseException;
import liquibase.resource.ResourceAccessor;
import liquibase.serializer.SnapshotSerializerFactory;
import liquibase.snapshot.DatabaseSnapshot;
import liquibase.snapshot.InvalidExampleException;
import liquibase.snapshot.SnapshotControl;
import liquibase.snapshot.SnapshotGeneratorFactory;
import org.bson.Document;
import org.bson.conversions.Bson;

public class T01_WriteMongo{
    static MongoClient mongoClient = new com.mongodb.MongoClient("172.16.6.161", 27017);
    static MongoDatabase mongoDatabase = mongoClient.getDatabase("ods_dbv");
    static MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("snapshot");
    static MongoJsonConverter mongoJsonConverter = new MongoJsonConverter();
    public static void main(String[] args){
        //从数据库建立Snapshot
//        String snapshotJSON = createSnapshot();
//        System.out.println(snapshotJSON);

        //一个模拟的键带'.'字符的json
        String snapshotJSON = "{'na.me': 'aa'}";

        //Java UUID 32位
        String uuid = "8a80cb816a86d8eb016a86dca6e30006";

        //写入数据库耗时
        long start = System.currentTimeMillis();
        insert(uuid, snapshotJSON);
        System.out.println("Insert spend: " + (System.currentTimeMillis() - start));

        //根据UUID查询耗时
        start = System.currentTimeMillis();
        String found = find(uuid);
        System.out.println(found);
        System.out.println("Find by uuid spend: " + (System.currentTimeMillis() - start));

        //根据UUID删除耗时
        start = System.currentTimeMillis();
        delete(uuid);
        System.out.println("Delete by uuid spend: " + (System.currentTimeMillis() - start));
    }

    private static String createSnapshot() throws DatabaseException, InvalidExampleException {
        ResourceAccessor resourceAccessor = Common.getResourceAccessor();
        //建立数据库连接
        Database database = DatabaseFactory.getInstance().openDatabase(
                "jdbc:sqlserver://172.16.6.161:1433;DatabaseName=ods", "sa", "@Welcome161",
                "com.microsoft.sqlserver.jdbc.SQLServerDriver", "liquibase.database.core.MSSQLDatabase", null, null, resourceAccessor);
        SnapshotControl snapshotControl = new SnapshotControl(database);
        CatalogAndSchema[] schemas = new CatalogAndSchema[]{database.getDefaultSchema()};
        database.setObjectQuotingStrategy(ObjectQuotingStrategy.QUOTE_ALL_OBJECTS);
        DatabaseSnapshot snapshot = SnapshotGeneratorFactory.getInstance().createSnapshot(schemas, database, snapshotControl);
        String format = "json"; //txt, yml
        return SnapshotSerializerFactory.getInstance().getSerializer(format).serialize(snapshot, true);
    }

    private static String find(String uuid){
        Bson filter = createFilter(uuid);
        FindIterable<Document> findIterable = mongoCollection.find(filter);
        String json = findIterable.first().toJson();
        //查询后转义: 将字段中的'__d__'替换为'.'
        return mongoJsonConverter.decode(json);
    }

    private static void insert(String uuid, String json){
        //写入前转义: 将字段名中的'.'替换为'__d__'
        json = mongoJsonConverter.encode(json);
        System.out.println(json);
        Document document = Document.parse(json);
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
