## 4. Diff 通过底层API对快照进行对比

- 对参考数数据库和目标数据库做快照

~~~java
DatabaseSnapshot referenceDatabaseSnapshot = createSnapshot("jdbc:h2:./test", "sa", "sa");
DatabaseSnapshot targetDatabaseSnapshot = createSnapshot("jdbc:h2:mem:test", null, null);
~~~
        
- 将两个快照做对比

~~~java
DiffResult diffResult = DiffGeneratorFactory.getInstance().compare(referenceDatabaseSnapshot, targetDatabaseSnapshot, CompareControl.STANDARD);
~~~

- 输出对比结果报告

~~~java
new DiffToReport(diffResult, System.out).print();
~~~

### 内部实现

- 对数据库做快照

~~~java
private static DatabaseSnapshot createSnapshot(String url, String userName, String password) throws DatabaseException, InvalidExampleException {
    //打开数据库
    Database database = DatabaseFactory.getInstance().openDatabase(url, userName, password, null, new ClassLoaderResourceAccessor(ClassLoader.getSystemClassLoader()));
    //对数据库建立快照
    SnapshotControl snapshotControl = new SnapshotControl(database);
    //选择要快照的schema
    CatalogAndSchema[] schemas = new CatalogAndSchema[]{database.getDefaultSchema()};
    //设定引号策略
    database.setObjectQuotingStrategy(ObjectQuotingStrategy.QUOTE_ALL_OBJECTS);
    //为该数据库的schema根据snapshotControl做快照
    DatabaseSnapshot snapshot = SnapshotGeneratorFactory.getInstance().createSnapshot(schemas, database, snapshotControl);
    return snapshot;
}
~~~

### 拓展

- 根据diffResult输出指定内容

    - 只输出Missing的对象

~~~java
System.out.println("Missing");
for(DatabaseObject databaseObject : diffResult.getMissingObjects()){
    System.out.println(databaseObject.getObjectTypeName() + ": " + databaseObject.getName());
}
~~~

    - 只输出Changed的对象
    
~~~java
System.out.println("Changed");
for(Map.Entry<DatabaseObject, ObjectDifferences> changedObjects : diffResult.getChangedObjects().entrySet()){  
  System.out.println(changedObjects.getKey());
  System.out.println(changedObjects.getValue());
}
~~~