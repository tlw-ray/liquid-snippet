package com.winning.ptc.liquid.snippet.a04_diff;

import com.alibaba.fastjson.JSON;
import com.winning.ptc.liquid.snippet.a02_diff.MyDiffCommand;
import liquibase.CatalogAndSchema;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.DatabaseFactory;
import liquibase.database.ObjectQuotingStrategy;
import liquibase.database.core.H2Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.diff.DiffGeneratorFactory;
import liquibase.diff.DiffResult;
import liquibase.diff.ObjectDifferences;
import liquibase.diff.compare.CompareControl;
import liquibase.diff.output.report.DiffToReport;
import liquibase.exception.DatabaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.snapshot.DatabaseSnapshot;
import liquibase.snapshot.InvalidExampleException;
import liquibase.snapshot.SnapshotControl;
import liquibase.snapshot.SnapshotGeneratorFactory;
import liquibase.structure.DatabaseObject;

import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

public class DiffDeep {

    public static void main(String[] args) throws SQLException, DatabaseException, InvalidExampleException {
        DatabaseSnapshot referenceDatabaseSnapshot = createSnapshot("jdbc:h2:./test", "sa", "sa");
        DatabaseSnapshot targetDatabaseSnapshot = createSnapshot("jdbc:h2:mem:test", null, null);
        DiffResult diffResult = DiffGeneratorFactory.getInstance().compare(referenceDatabaseSnapshot, targetDatabaseSnapshot, CompareControl.STANDARD);

//        System.out.println("Missing");
//        for(DatabaseObject databaseObject : diffResult.getMissingObjects()){
//            System.out.println(databaseObject.getObjectTypeName() + ": " + databaseObject.getName());
//        }
//        System.out.println("Changed");
//        for(Map.Entry<DatabaseObject, ObjectDifferences> changedObjects : diffResult.getChangedObjects().entrySet()){
//            System.out.println(changedObjects.getKey());
//            System.out.println(changedObjects.getValue());
//        }

        JSON.toJSONString(diffResult, true);
//        new DiffToReport(diffResult, System.out).print();

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
