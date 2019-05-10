package com.winning.ptc.liquid.snippet.a03_snapshot;

import com.winning.ptc.liquid.snippet.Common;
import liquibase.CatalogAndSchema;
import liquibase.command.CommandExecutionException;
import liquibase.command.core.SnapshotCommand;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.ObjectQuotingStrategy;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ResourceAccessor;
import liquibase.serializer.SnapshotSerializerFactory;
import liquibase.snapshot.DatabaseSnapshot;
import liquibase.snapshot.InvalidExampleException;
import liquibase.snapshot.SnapshotControl;
import liquibase.snapshot.SnapshotGeneratorFactory;

import java.sql.SQLException;

public class Snapshot {
    public static void main(String[] args) throws SQLException, CommandExecutionException, LiquibaseException {
        ResourceAccessor resourceAccessor = Common.getResourceAccessor();
        //建立数据库连接
        Database database = DatabaseFactory.getInstance().openDatabase(
                "jdbc:sqlserver://172.16.6.161:1433;DatabaseName=ods", "sa", "@Welcome161",
                "com.microsoft.sqlserver.jdbc.SQLServerDriver", "liquibase.database.core.MSSQLDatabase", null, null, resourceAccessor);


        //调用方式1: SnapshotCommand
//        commandCall(h2Database1);

        //调用方式2: Function
        functionCall(database);
    }

    private static void commandCall(Database Database1) throws CommandExecutionException, LiquibaseException {
        //通过SnapshotCommand接口调用
        SnapshotCommand snapshotCommand = new SnapshotCommand();
        snapshotCommand.setDatabase(Database1);
//        snapshotCommand.setSerializerFormat("json");
        snapshotCommand.setSerializerFormat("yml");
//        snapshotCommand.setSerializerFormat("xml");
//        snapshotCommand.setSerializerFormat("txt");
        SnapshotCommand.SnapshotCommandResult snapshotCommandResult = snapshotCommand.execute();
        System.out.println(snapshotCommandResult.print());
    }

    private static void functionCall(Database Database1) throws DatabaseException, InvalidExampleException {
        //通过SnapshotGeneratorFactory和SnapshotController调用
        SnapshotControl snapshotControl = new SnapshotControl(Database1);
        CatalogAndSchema[] schemas = new CatalogAndSchema[]{Database1.getDefaultSchema()};
        Database1.setObjectQuotingStrategy(ObjectQuotingStrategy.QUOTE_ALL_OBJECTS);
        DatabaseSnapshot snapshot = SnapshotGeneratorFactory.getInstance().createSnapshot(schemas, Database1, snapshotControl);
        String format = "yml"; //txt, yml
        String string = SnapshotSerializerFactory.getInstance().getSerializer(format).serialize(snapshot, true);
        System.out.println(string);
    }
}
