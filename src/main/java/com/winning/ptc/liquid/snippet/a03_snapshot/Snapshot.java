package com.winning.ptc.liquid.snippet.a03_snapshot;

import liquibase.CatalogAndSchema;
import liquibase.command.CommandExecutionException;
import liquibase.command.core.SnapshotCommand;
import liquibase.database.DatabaseConnection;
import liquibase.database.ObjectQuotingStrategy;
import liquibase.database.core.H2Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.serializer.SnapshotSerializerFactory;
import liquibase.snapshot.DatabaseSnapshot;
import liquibase.snapshot.InvalidExampleException;
import liquibase.snapshot.SnapshotControl;
import liquibase.snapshot.SnapshotGeneratorFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Snapshot {
    public static void main(String[] args) throws SQLException, CommandExecutionException, LiquibaseException {
        //建立数据库连接
        Connection connection1 = DriverManager.getConnection("jdbc:h2:./test", "sa", "sa");

        //封装为Liquibase可接受的连接对象
        DatabaseConnection databaseConnection1 = new JdbcConnection(connection1);

        //封装为Liquibase可对比用的数据库对象
        H2Database h2Database1 = new H2Database();
        h2Database1.setConnection(databaseConnection1);

        //调用方式1: SnapshotCommand
//        commandCall(h2Database1);

        //调用方式2: Function
        functionCall(h2Database1);
    }

    private static void commandCall(H2Database h2Database1) throws CommandExecutionException, LiquibaseException {
        //通过SnapshotCommand接口调用
        SnapshotCommand snapshotCommand = new SnapshotCommand();
        snapshotCommand.setDatabase(h2Database1);
//        snapshotCommand.setSerializerFormat("json");
        snapshotCommand.setSerializerFormat("yml");
//        snapshotCommand.setSerializerFormat("xml");
//        snapshotCommand.setSerializerFormat("txt");
        SnapshotCommand.SnapshotCommandResult snapshotCommandResult = snapshotCommand.execute();
        System.out.println(snapshotCommandResult.print());
    }

    private static void functionCall(H2Database h2Database1) throws DatabaseException, InvalidExampleException {
        //通过SnapshotGeneratorFactory和SnapshotController调用
        SnapshotControl snapshotControl = new SnapshotControl(h2Database1);
        CatalogAndSchema[] schemas = new CatalogAndSchema[]{h2Database1.getDefaultSchema()};
        h2Database1.setObjectQuotingStrategy(ObjectQuotingStrategy.QUOTE_ALL_OBJECTS);
        DatabaseSnapshot snapshot = SnapshotGeneratorFactory.getInstance().createSnapshot(schemas, h2Database1, snapshotControl);
        String format = "yml"; //txt, yml
        String string = SnapshotSerializerFactory.getInstance().getSerializer(format).serialize(snapshot, true);
        System.out.println(string);
    }
}
