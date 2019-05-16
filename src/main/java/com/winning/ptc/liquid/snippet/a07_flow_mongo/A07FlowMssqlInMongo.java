package com.winning.ptc.liquid.snippet.a07_flow_mongo;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.winning.ptc.liquid.snippet.Common;
import com.winning.ptc.liquid.snippet.MongoJsonConverter;
import com.winning.ptc.liquid.snippet.a08_system.model.DbvDiffResult;
import liquibase.CatalogAndSchema;
import liquibase.configuration.GlobalConfiguration;
import liquibase.configuration.LiquibaseConfiguration;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.OfflineConnection;
import liquibase.database.core.MSSQLDatabase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.diff.DiffGeneratorFactory;
import liquibase.diff.DiffResult;
import liquibase.diff.compare.CompareControl;
import liquibase.diff.output.DiffOutputControl;
import liquibase.diff.output.changelog.DiffToChangeLog;
import liquibase.diff.output.report.DiffToReport;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.exception.LiquibaseParseException;
import liquibase.parser.core.ParsedNode;
import liquibase.parser.core.ParsedNodeException;
import liquibase.resource.ResourceAccessor;
import liquibase.serializer.ChangeLogSerializerFactory;
import liquibase.serializer.SnapshotSerializerFactory;
import liquibase.snapshot.*;
import org.apache.commons.io.input.CharSequenceInputStream;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Statement;
import java.util.Map;

public class A07FlowMssqlInMongo {

    static MongoClient mongoClient = new com.mongodb.MongoClient("172.16.6.161", 27017);
    static MongoDatabase mongoDatabase = mongoClient.getDatabase("ods_dbv");
    static MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("snapshot");
    static MongoJsonConverter mongoJsonConverter = new MongoJsonConverter();
    public static void main(String[] args) throws Exception {
        //1. 连库
        message("连库");
        ResourceAccessor resourceAccessor = Common.getResourceAccessor();
        //建立数据库连接
        JdbcConnection jdbcConnection = (JdbcConnection)DatabaseFactory.getInstance().openConnection(
                "jdbc:sqlserver://172.16.6.161:1433;DatabaseName=ods", "sa", "@Welcome161",
                "com.microsoft.sqlserver.jdbc.SQLServerDriver", "liquibase.database.core.MSSQLDatabase", null, null, resourceAccessor);

        MSSQLDatabase mssqlDatabase = new MSSQLDatabase();
        mssqlDatabase.setConnection(jdbcConnection);

        //2. 删表
//        message("删表");
//        String dropTable = "IF EXISTS TEST1 DROP TABLE TEST1";
//        try(Statement statement = jdbcConnection.createStatement()){
//            boolean result = statement.execute(dropTable);
//            System.out.println("Drop table return: " + result);
//        }

        //3. 建表
        message("建表");
        String createTable1 = "CREATE TABLE TEST1(ID INT PRIMARY KEY, NAME VARCHAR(255), DESCRIPTION VARCHAR(255))";
        try(Statement statement = jdbcConnection.createStatement()) {
            boolean result = statement.execute(createTable1);
            System.out.println("Create table return: " + result);
        }

        //4. 做快照1
        message("做快照1");
        String snapshotUuid1 = createSnapshotAndSaveToMongo(mssqlDatabase);


        //5. 改表
        message("改表");
        String createTable1_1 = "ALTER TABLE TEST1 ALTER COLUMN NAME VARCHAR(32)";
        try(Statement statement = jdbcConnection.createStatement()) {
            boolean result = statement.execute(createTable1_1);
            System.out.println("Create table return: " + result);
        }
        String createTable2 = "CREATE TABLE TEST2(ID INT PRIMARY KEY, NAME VARCHAR(255))";
        try(Statement statement = jdbcConnection.createStatement()) {
            boolean result = statement.execute(createTable2);
            System.out.println("Create table return: " + result);
        }

        //6. 做快照2
        message("做快照2");
        String snapshotUuid2 = createSnapshotAndSaveToMongo(mssqlDatabase);

        //7. 加载并对比快照1, 2 生成报告和可执行语句
        message("加载快照");
        DatabaseSnapshot databaseSnapshot1 = loadSnapshotFromMongo(snapshotUuid1);
        DatabaseSnapshot databaseSnapshot2 = loadSnapshotFromMongo(snapshotUuid2);
        DiffResult diffResult = DiffGeneratorFactory.getInstance().compare(databaseSnapshot1, databaseSnapshot2, CompareControl.STANDARD);

        message("生成快照对比报告");
        DbvDiffResult dbvDiffResult = new DbvDiffResult();
        try(
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PrintStream printStream = new PrintStream(byteArrayOutputStream)){
            new DiffToReport(diffResult, printStream).print();
            byte[] bytes = byteArrayOutputStream.toByteArray();
            String report = new String(bytes, "utf8");
            dbvDiffResult.setReport(report);
        }


        DiffOutputControl diffOutputControl = new DiffOutputControl();
//        DiffToChangeLog diffToChangeLog = new DiffToChangeLog(diffResult, diffOutputControl);
//        message("生成变化记录脚本YML");
////        diffToChangeLog.print(System.out, ChangeLogSerializerFactory.getInstance().getSerializer("yml"));
//        message("生成正向变化记录SQL");
//        diffToChangeLog.print("changelog.mssql.sql", ChangeLogSerializerFactory.getInstance().getSerializer("sql"));
//
//        message("生成变化记录XML");
////        diffToChangeLog.print(System.out, ChangeLogSerializerFactory.getInstance().getSerializer("xml"));
//        message("生成变化记录JSON");
////        diffToChangeLog.print(System.out, ChangeLogSerializerFactory.getInstance().getSerializer("json"));
//        message("生成变化记录TXT");
////        diffToChangeLog.print(System.out, ChangeLogSerializerFactory.getInstance().getSerializer("xx.txt"));
//
        DiffResult diffResult1 = DiffGeneratorFactory.getInstance().compare(databaseSnapshot2, databaseSnapshot1, CompareControl.STANDARD);
        DiffToChangeLog diffToChangeLog1 = new DiffToChangeLog(diffResult1, diffOutputControl);
//        message("生成逆向变化记录SQL");
////        diffToChangeLog1.print("changelog1.mssql.sql", ChangeLogSerializerFactory.getInstance().getSerializer("sql"));

        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(byteArrayOutputStream)) {
            diffToChangeLog1.setChangeSetPath("changelog.mssql.sql");
            diffToChangeLog1.print(printStream, ChangeLogSerializerFactory.getInstance().getSerializer("sql"));
            byte[] bytes = byteArrayOutputStream.toByteArray();
            String sqlReport = new String(bytes, "utf8");
            dbvDiffResult.setPatchQuery(sqlReport);
            printStream.close();
        }
        System.out.println(dbvDiffResult.getPatchQuery());
        System.out.println(dbvDiffResult.getReport());
    }

    private static String createSnapshotAndSaveToMongo(Database database) throws LiquibaseException {
        String jsonSnapshot = makeJsonSnapshot(database);
        String uuid = saveToMongo(jsonSnapshot);
        return uuid;
    }

    private static String makeJsonSnapshot(Database database) throws LiquibaseException {
        SnapshotControl snapshotControl = new SnapshotControl(database);
        CatalogAndSchema[] schemas = new CatalogAndSchema[]{database.getDefaultSchema()};
        DatabaseSnapshot snapshot = SnapshotGeneratorFactory.getInstance().createSnapshot(schemas, database, snapshotControl);
        return SnapshotSerializerFactory.getInstance().getSerializer("json").serialize(snapshot, true);
    }

    private static String saveToMongo(String snapshot){
        String uuid = Common.generateUUID();
        String mongoJson = mongoJsonConverter.encode(snapshot);
        Document document = Document.parse(mongoJson);
        document.put("_id", uuid);
        mongoCollection.insertOne(document);
        return uuid;
    }

    private static DatabaseSnapshot loadSnapshotFromMongo(String uuid) throws UnsupportedEncodingException, LiquibaseParseException, DatabaseException, InvalidExampleException, ParsedNodeException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        //从mongoDB根据UUID获取快照内容
        Bson filter = Common.createUuidFilter(uuid);
        Document document = mongoCollection.find(filter).first();
        String json = document.toJson();
        json =  mongoJsonConverter.decode(json);

        //将json
        Yaml yaml = new Yaml(new SafeConstructor());
        //TODO close
        CharSequenceInputStream charSequenceInputStream = new CharSequenceInputStream(json, "utf-8");
        InputStreamReader inputStreamReader = new InputStreamReader(charSequenceInputStream, ((GlobalConfiguration)LiquibaseConfiguration.getInstance().getConfiguration(GlobalConfiguration.class)).getOutputEncoding());
        Map parsedYaml = yaml.load(inputStreamReader);
        Map rootList = (Map)parsedYaml.get("snapshot");
        if (rootList == null) {
            throw new LiquibaseParseException("Could not find root snapshot node");
        }
        String shortName = (String)((Map)rootList.get("database")).get("shortName");
        Database database = DatabaseFactory.getInstance().getDatabase(shortName).getClass().getConstructor().newInstance();
        database.setConnection(new OfflineConnection("offline:" + shortName, (ResourceAccessor)null));
        DatabaseSnapshot snapshot = new RestoredDatabaseSnapshot(database);
        ParsedNode snapshotNode = new ParsedNode((String)null, "snapshot");
        snapshotNode.setValue(rootList);
        Map metadata = (Map)rootList.get("metadata");
        if (metadata != null) {
            snapshot.getMetadata().putAll(metadata);
        }
        snapshot.load(snapshotNode, Common.getResourceAccessor());
        return  snapshot;
    }

    private static void message(String message){
        System.out.println("----------" + message + "---------");
    }
}
