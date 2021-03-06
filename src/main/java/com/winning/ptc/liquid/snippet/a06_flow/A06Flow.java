package com.winning.ptc.liquid.snippet.a06_flow;

import com.winning.ptc.liquid.snippet.Common;
import liquibase.CatalogAndSchema;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.diff.DiffGeneratorFactory;
import liquibase.diff.DiffResult;
import liquibase.diff.compare.CompareControl;
import liquibase.diff.output.DiffOutputControl;
import liquibase.diff.output.changelog.DiffToChangeLog;
import liquibase.diff.output.report.DiffToReport;
import liquibase.exception.LiquibaseException;
import liquibase.parser.core.yaml.YamlSnapshotParser;
import liquibase.resource.ResourceAccessor;
import liquibase.serializer.ChangeLogSerializerFactory;
import liquibase.serializer.SnapshotSerializerFactory;
import liquibase.snapshot.DatabaseSnapshot;
import liquibase.snapshot.SnapshotControl;
import liquibase.snapshot.SnapshotGeneratorFactory;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class A06Flow {
    public static void main(String[] args) throws Exception {
        String dbName = "test4";

        //0. 删除之前创建的文件
        message("删文件");
        File h2File = new File(dbName + ".h2.db");
        File traceFile = new File(dbName + ".trace.db");
        h2File.deleteOnExit();
        traceFile.deleteOnExit();

        //1. 建库
        message("建库");
        ResourceAccessor resourceAccessor = Common.getResourceAccessor();
        Connection connection = DriverManager.getConnection("jdbc:h2:./" + dbName);
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));

        //2. 建表
        message("建表");
        String createTable1 = "CREATE TABLE TEST1(ID INT PRIMARY KEY, NAME VARCHAR(255), DESC VARCHAR(255))";
        try(Statement statement = connection.createStatement()) {
            boolean result = statement.execute(createTable1);
            System.out.println("Create table return: " + result);
        }

        //3. 做快照1
        message("做快照1");
        File snapshotFile1 = new File("snap1.yml");
        makeSnapshot(database, snapshotFile1);

        //4. 改表
        message("改表");
        String createTable1_1 = "ALTER TABLE TEST1 ALTER COLUMN NAME VARCHAR(32)";
        try(Statement statement = connection.createStatement()) {
            boolean result = statement.execute(createTable1_1);
            System.out.println("Create table return: " + result);
        }
        String createTable2 = "CREATE TABLE TEST2(ID INT PRIMARY KEY, NAME VARCHAR(255))";
        try(Statement statement = connection.createStatement()) {
            boolean result = statement.execute(createTable2);
            System.out.println("Create table return: " + result);
        }

        //5. 做快照2
        message("做快照2");
        File snapshotFile2 = new File("snap2.yml");
        makeSnapshot(database, snapshotFile2);

        //6. 加载并对比快照1, 2 生成报告和可执行语句
        message("加载快照");
        YamlSnapshotParser yamlSnapshotParser = new YamlSnapshotParser();
        DatabaseSnapshot databaseSnapshot1 = yamlSnapshotParser.parse(snapshotFile1.getPath(), resourceAccessor);
        DatabaseSnapshot databaseSnapshot2 = yamlSnapshotParser.parse(snapshotFile2.getPath(), resourceAccessor);
        DiffResult diffResult = DiffGeneratorFactory.getInstance().compare(databaseSnapshot1, databaseSnapshot2, CompareControl.STANDARD);

        message("生成快照对比报告");
        new DiffToReport(diffResult, System.out).print();

        DiffOutputControl diffOutputControl = new DiffOutputControl();
        DiffToChangeLog diffToChangeLog = new DiffToChangeLog(diffResult, diffOutputControl);
        message("生成正向变化记录SQL");
        diffToChangeLog.print("changelog.mssql.sql", ChangeLogSerializerFactory.getInstance().getSerializer("sql"));

        DiffResult diffResult1 = DiffGeneratorFactory.getInstance().compare(databaseSnapshot2, databaseSnapshot1, CompareControl.STANDARD);
        DiffToChangeLog diffToChangeLog1 = new DiffToChangeLog(diffResult1, diffOutputControl);
        message("生成逆向变化记录SQL");
        diffToChangeLog1.print("changelog1.mssql.sql", ChangeLogSerializerFactory.getInstance().getSerializer("sql"));
    }

    private static void makeSnapshot(Database database, File snapshotFile) throws LiquibaseException, IOException {
        SnapshotControl snapshotControl = new SnapshotControl(database);
        CatalogAndSchema[] schemas = new CatalogAndSchema[]{database.getDefaultSchema()};
        DatabaseSnapshot snapshot = SnapshotGeneratorFactory.getInstance().createSnapshot(schemas, database, snapshotControl);
        String snapshotYml = SnapshotSerializerFactory.getInstance().getSerializer("yml").serialize(snapshot, true);
        FileUtils.write(snapshotFile, snapshotYml, "utf-8");
    }

    private static void message(String message){
        System.out.println("----------" + message + "---------");
    }
}
