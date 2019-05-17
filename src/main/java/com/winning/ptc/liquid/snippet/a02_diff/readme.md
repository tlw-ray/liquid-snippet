## 2 使用Liquibase对比数据库

### 2.1 功能

对比两个数据库以文本或XML的形式输出结构上的差异。例如: 


- 创建SpringBoot的命令行启动程序: LiquiDiff.java

- 编写功能函数: diffCommand();

    - 建立两个不同的数据库连接，一个是被之前web项目初始化过的，一个没有进行过任何操作的内存库
    
        ~~~Java
        Connection connection1 = DriverManager.getConnection("jdbc:h2:./test", "sa", "sa");
        Connection connection2 = DriverManager.getConnection("jdbc:h2:mem:test");
        ~~~
  
    - 封装为Liquibase可接受的连接对象
    
        ~~~Java
        DatabaseConnection databaseConnection1 = new JdbcConnection(connection1);
        DatabaseConnection databaseConnection2 = new JdbcConnection(connection2);
        ~~~
  
    - 封装为Liquibase可对比用的数据库对象
    
        ~~~Java
        H2Database h2Database1 = new H2Database();
        h2Database1.setConnection(databaseConnection1);
        H2Database h2Database2 = new H2Database();
        h2Database2.setConnection(databaseConnection2);
        ~~~
  
    - 进行对比
    
        ~~~Java
        diffCommand.setReferenceDatabase(h2Database1)           //设定参照数据库
              .setTargetDatabase(h2Database2)                 //设定目标数据库
              .setOutputStream(System.out)                    //输出到控制台
              .setCompareControl(CompareControl.STANDARD)     //以标准方式对比
              .execute();   
        ~~~
        
    - 输出结果
    
        ~~~TXT
        Reference Database: SA @ jdbc:h2:./test (Default Schema: PUBLIC)
        Comparison Database:  @ jdbc:h2:mem:test (Default Schema: PUBLIC)
        Compared Schemas: PUBLIC
        Product Name: EQUAL
        Product Version: EQUAL
        Missing Catalog(s): NONE
        Unexpected Catalog(s): NONE
        Changed Catalog(s): NONE
        Missing Column(s): 
             PUBLIC.PERSON.ADDRESS
             PUBLIC.DATABASECHANGELOG.AUTHOR
             PUBLIC.DATABASECHANGELOG.COMMENTS
             PUBLIC.DATABASECHANGELOG.CONTEXTS
             PUBLIC.DATABASECHANGELOG.DATEEXECUTED
             PUBLIC.DATABASECHANGELOG.DEPLOYMENT_ID
             PUBLIC.DATABASECHANGELOG.DESCRIPTION
             PUBLIC.DATABASECHANGELOG.EXECTYPE
             PUBLIC.DATABASECHANGELOG.FILENAME
             PUBLIC.DATABASECHANGELOG.ID
             PUBLIC.DATABASECHANGELOGLOCK.ID
             PUBLIC.PERSON.ID
             PUBLIC.DATABASECHANGELOG.LABELS
             PUBLIC.DATABASECHANGELOG.LIQUIBASE
             PUBLIC.DATABASECHANGELOGLOCK.LOCKED
             PUBLIC.DATABASECHANGELOGLOCK.LOCKEDBY
             PUBLIC.DATABASECHANGELOGLOCK.LOCKGRANTED
             PUBLIC.DATABASECHANGELOG.MD5SUM
             PUBLIC.PERSON.NAME
             PUBLIC.DATABASECHANGELOG.ORDEREXECUTED
             PUBLIC.DATABASECHANGELOG.TAG
        Unexpected Column(s): NONE
        Changed Column(s): NONE
        Missing Foreign Key(s): NONE
        Unexpected Foreign Key(s): NONE
        Changed Foreign Key(s): NONE
        Missing Index(s): 
             PRIMARY_KEY_8 UNIQUE  ON PUBLIC.PERSON(ID)
             PRIMARY_KEY_D UNIQUE  ON PUBLIC.DATABASECHANGELOGLOCK(ID)
        Unexpected Index(s): NONE
        Changed Index(s): NONE
        Missing Primary Key(s): 
             PK_DATABASECHANGELOGLOCK on PUBLIC.DATABASECHANGELOGLOCK(ID)
             PK_PERSON on PUBLIC.PERSON(ID)
        Unexpected Primary Key(s): NONE
        Changed Primary Key(s): NONE
        Missing Schema(s): NONE
        Unexpected Schema(s): NONE
        Changed Schema(s): NONE
        Missing Sequence(s): NONE
        Unexpected Sequence(s): NONE
        Changed Sequence(s): NONE
        Missing Stored Procedure(s): NONE
        Unexpected Stored Procedure(s): NONE
        Changed Stored Procedure(s): NONE
        Missing Table(s): 
             DATABASECHANGELOG
             DATABASECHANGELOGLOCK
             PERSON
        Unexpected Table(s): NONE
        Changed Table(s): NONE
        Missing Unique Constraint(s): NONE
        Unexpected Unique Constraint(s): NONE
        Changed Unique Constraint(s): NONE
        Missing View(s): NONE
        Unexpected View(s): NONE
        Changed View(s): NONE
        ~~~
        
 - 使用自定义的MyDiffToReport获得中文报告
 
    - 实现在myDiffCommand()方法
    
        - 通过MyDiffCommand.java将DiffCommand类的createDiffResult()方法开放
        
        - 通过MyDiffCommand.createDiffResult()获得的DiffResult对象，通过MyDiffToReport生成中文报告