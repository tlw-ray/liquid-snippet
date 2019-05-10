package com.winning.ptc.liquid.snippet.a05_init;

import com.winning.ptc.liquid.snippet.Common;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ResourceAccessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class InitDatabase {
    //TODO 失败 没有任何更新产生
    public static void main(String[] args) throws SQLException, LiquibaseException, IOException {
        ResourceAccessor resourceAccessor = Common.getResourceAccessor();
        Connection connection = DriverManager.getConnection("jdbc:h2:./test3");
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
        DatabaseChangeLog databaseChangeLog = new DatabaseChangeLog("create-person1-table-changelog-1.xml");
        for(InputStream inputStream:resourceAccessor.getResourcesAsStream("create-person1-table-changelog-1.xml")){
            System.out.println("---");
            if(inputStream != null){
                try(InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        System.out.println(line);
                    }
                }
            }
        }
        Liquibase liquibase = new Liquibase(databaseChangeLog, resourceAccessor, database);
        liquibase.update(new Contexts());
    }
}
