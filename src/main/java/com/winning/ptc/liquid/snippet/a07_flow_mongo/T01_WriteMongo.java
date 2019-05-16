package com.winning.ptc.liquid.snippet.a07_flow_mongo;


import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.winning.ptc.liquid.snippet.Common;
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
    public static void main(String[] args) throws DatabaseException, InvalidExampleException {
//        String snapshotJSON = createSnapshot();
//        System.out.println(snapshotJSON);

        String snapshotJSON = "{na.me: 'aa'}";

        //SQLServer UUID 32位
        String uuid = "8a80cb816a86d8eb016a86dca6e30006";
        //Mongo UUID 24位
//        String uuid = "5ccf97a691a8dd0006199ba0";

        //Insert 插入前做转义编码
        long start = System.currentTimeMillis();
        insert(uuid, snapshotJSON);
        System.out.println("Insert spend: " + (System.currentTimeMillis() - start));

        //find 查询后做转义解码
        start = System.currentTimeMillis();
        String found = find(uuid);
        System.out.println(found);
        System.out.println("Find by id spend: " + (System.currentTimeMillis() - start));

        //delete
//        delete(uuid);
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
        return escapeDecode(json);
    }

    private static void insert(String uuid, String json){
        json = escapeEncode(json);
        System.out.println(json);
        Document document = Document.parse(json);
        document.put("_id", uuid);
        mongoCollection.insertOne(document);
    }

    public static String escapeEncode(String json){
        //        \  -->  \\
        //        $  -->  \u0024
        //        .  -->  \u002e
        return json.replaceAll("\\.", "___d___")
                .replaceAll("\\$", "___dl___")
                .replaceAll("\\\\", "___s___");
    }

    public static String escapeDecode(String json){
        return json.replaceAll("___d___", ".")
                .replaceAll("___dl___", "$")
                .replaceAll("___s___", "\\");
    }

    private static void delete(String uuid){
        Bson filter = createFilter(uuid);
        mongoCollection.deleteOne(filter);
    }

    private static Bson createFilter(String uuid){
        return Filters.eq("_id", uuid);
    }

}
