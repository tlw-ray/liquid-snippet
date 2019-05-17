package com.winning.ptc.liquid.snippet.a04_diff;

import liquibase.CatalogAndSchema;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.ObjectQuotingStrategy;
import liquibase.diff.DiffGeneratorFactory;
import liquibase.diff.DiffResult;
import liquibase.diff.compare.CompareControl;
import liquibase.diff.output.report.DiffToReport;
import liquibase.exception.DatabaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.snapshot.DatabaseSnapshot;
import liquibase.snapshot.InvalidExampleException;
import liquibase.snapshot.SnapshotControl;
import liquibase.snapshot.SnapshotGeneratorFactory;

import java.sql.SQLException;

//对数据快照做对比
public class DiffSnapshot {

    public static void main(String[] args) throws SQLException, DatabaseException, InvalidExampleException {
        DatabaseSnapshot referenceDatabaseSnapshot = createSnapshot("jdbc:h2:./test", "sa", "sa");
        DatabaseSnapshot targetDatabaseSnapshot = createSnapshot("jdbc:h2:mem:test", null, null);
        DiffResult diffResult = DiffGeneratorFactory.getInstance().compare(referenceDatabaseSnapshot, targetDatabaseSnapshot, CompareControl.STANDARD);

        //只输出Missing的对象
//        System.out.println("Missing");
//        for(DatabaseObject databaseObject : diffResult.getMissingObjects()){
//            System.out.println(databaseObject.getObjectTypeName() + ": " + databaseObject.getName());
//        }
//
        //只输出Changed的对象
//        System.out.println("Changed");
//        for(Map.Entry<DatabaseObject, ObjectDifferences> changedObjects : diffResult.getChangedObjects().entrySet()){
//            System.out.println(changedObjects.getKey());
//            System.out.println(changedObjects.getValue());
//        }

        //输出所有报告
        new DiffToReport(diffResult, System.out).print();

    }

    private static DatabaseSnapshot createSnapshot(String url, String userName, String password) throws DatabaseException, InvalidExampleException {
        Database database = DatabaseFactory.getInstance().openDatabase(url, userName, password, null, new ClassLoaderResourceAccessor(ClassLoader.getSystemClassLoader()));
        SnapshotControl snapshotControl = new SnapshotControl(database);
        CatalogAndSchema[] schemas = new CatalogAndSchema[]{database.getDefaultSchema()};
        database.setObjectQuotingStrategy(ObjectQuotingStrategy.QUOTE_ALL_OBJECTS);
        DatabaseSnapshot snapshot = SnapshotGeneratorFactory.getInstance().createSnapshot(schemas, database, snapshotControl);
        return snapshot;
    }
}
