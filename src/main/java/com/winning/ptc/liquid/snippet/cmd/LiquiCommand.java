package com.winning.ptc.liquid.snippet.cmd;

import liquibase.command.CommandExecutionException;
import liquibase.command.core.DiffCommand;
import liquibase.command.core.DiffToChangeLogCommand;
import liquibase.command.core.GenerateChangeLogCommand;
import liquibase.database.DatabaseConnection;
import liquibase.database.core.H2Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.diff.DiffResult;
import liquibase.diff.compare.CompareControl;
import liquibase.diff.output.DiffOutputControl;
import liquibase.exception.DatabaseException;
import liquibase.snapshot.InvalidExampleException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;

import javax.xml.bind.JAXB;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LiquiCommand{

    public static void main(String[] args) throws CommandExecutionException, SQLException {
        //操作1: 获得数据库之间的差异的文本描述
        diffCommand();

        //操作2: 获得差异的XML
//        diffToChangeLogCommand();

        //操作3: 获得差异的XML
//        generateChangeLogCommand();

        //操作4: 自定义的对比操作，输出DiffResult
//        myDiffCommand();
    }

    private static void diffCommand() throws SQLException, CommandExecutionException {
        System.out.println("\n\n\nDiffCommand:");
        DiffCommand diffCommand = new DiffCommand();
        //可添加监听回调，在触发对比前输出一段文字
//        diffCommand.setSnapshotListener(new SnapshotListener() {
//            @Override
//            public void willSnapshot(DatabaseObject example, Database database) {
//                System.out.println("------willSnapshot: " + example + "\t" + database);
//            }
//
//            @Override
//            public void finishedSnapshot(DatabaseObject example, DatabaseObject snapshot, Database database) {
//
//            }
//        });
        initializeAndRunDiffCommand(diffCommand);
    }

    private static void diffToChangeLogCommand() throws CommandExecutionException, SQLException {
        System.out.println("\n\n\nDiffToChangeLogCommand:");
        DiffToChangeLogCommand diffCommand = new DiffToChangeLogCommand();
        diffCommand.setDiffOutputControl(new DiffOutputControl());
        initializeAndRunDiffCommand(diffCommand);
    }

    private static void generateChangeLogCommand() throws CommandExecutionException, SQLException {
        System.out.println("\n\n\nGenerateChangeLogCommand:");
        GenerateChangeLogCommand diffCommand = new GenerateChangeLogCommand();
        diffCommand.setDiffOutputControl(new DiffOutputControl());
        initializeAndRunDiffCommand(diffCommand);
    }

    private static void myDiffCommand() throws CommandExecutionException, SQLException, DatabaseException, InvalidExampleException {
        System.out.println("\n\n\nMyDiffCommand:");
        MyDiffCommand myDiffCommand = new MyDiffCommand();
        Connection connection1 = DriverManager.getConnection("jdbc:h2:./test", "sa", "sa");
        Connection connection2 = DriverManager.getConnection("jdbc:h2:mem:test");
        DatabaseConnection databaseConnection1 = new JdbcConnection(connection1);
        DatabaseConnection databaseConnection2 = new JdbcConnection(connection2);
        H2Database h2Database1 = new H2Database();
        h2Database1.setConnection(databaseConnection1);
        H2Database h2Database2 = new H2Database();
        h2Database2.setConnection(databaseConnection2);
        myDiffCommand.setReferenceDatabase(h2Database1)
                .setTargetDatabase(h2Database2)
                .setOutputStream(System.out)
                .setCompareControl(CompareControl.STANDARD);
        DiffResult diffResult = myDiffCommand.createDiffResult();
        new MyDiffToReport(diffResult, System.out).print();
        JAXB.marshal(diffResult, System.out);
    }

    private static void initializeAndRunDiffCommand(DiffCommand diffCommand) throws SQLException, CommandExecutionException {
        //建立数据库连接
        Connection connection1 = DriverManager.getConnection("jdbc:h2:./test", "sa", "sa");
        Connection connection2 = DriverManager.getConnection("jdbc:h2:mem:test");

        //封装为Liquibase可接受的连接对象
        DatabaseConnection databaseConnection1 = new JdbcConnection(connection1);
        DatabaseConnection databaseConnection2 = new JdbcConnection(connection2);

        //封装为Liquibase可对比用的数据库对象
        H2Database h2Database1 = new H2Database();
        h2Database1.setConnection(databaseConnection1);
        H2Database h2Database2 = new H2Database();
        h2Database2.setConnection(databaseConnection2);

        //进行对比
        diffCommand.setReferenceDatabase(h2Database1)           //设定参照数据库
                .setTargetDatabase(h2Database2)                 //设定目标数据库
                .setOutputStream(System.out)                    //输出到控制台
                .setCompareControl(CompareControl.STANDARD)     //以标准方式对比
                .execute();                                     //执行对比
    }
}
