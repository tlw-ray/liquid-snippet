# Liquibase Spring示例

## 概述

在SpringBoot项目中使用LiquiBase，分析数据库版本差异。根据差异维护数据库结构。

- 使用Liquibase初始化数据库: LiquiApplication.java（参考: https://javadeveloperzone.com/spring-boot/spring-boot-liquibase-example/)
- 使用Liquibase区分数据库差异: LiquiCommand.java

## 概念

- 数据库对象：
![数据库对象](diagram/databaseStructure.png)
- 支持的数据库:
![支持数据库](diagram/supportedDatabase.png)


## 运行环境

- JDK8+
- Gradle
- SpringBoot
- Liquibase
- H2
    

## 1. 使用Liquibase初始化数据库

### 1.1 功能: 
- 使用Liquibase的描述文件(XML, JSON, YML)来创建数据库可以使用相同的描述文件在不同的数据库上创建数据库。
- Liquibase能够加载、使用、生成这些结构化的配置文件。
### 1.2 步骤: 
- 创建SpringBoot的Web项目并添加依赖项: h2, liquibase-core, springboot-starter-jdbc
- 配置Applications.properties
    ~~~Properties
    spring.datasource.driver-class-name=org.h2.Driver
    spring.datasource.url=jdbc:h2:mem:test;
    spring.datasource.username=sa
    spring.datasource.password=sa
    spring.h2.console.enabled=true
    
    logging.level.liquibase=INFO
    
    spring.liquibase.change-log=classpath:/changelog-master.xml
    ~~~
- 编写Liquibase的数据库创建xml脚本
    - 主文件: changelog-master.xml, 包含若干具体任务的脚本。
    - 建表: create-person-table-changelog-1.xml, 在给定的数据库创建表。
    - 插数据: insert-person-table-changelog-2.xml, 
    - 更新数据: update-person-table-precondition-3.xml
- 编写SpringBoot的启动代码： LiquiApplication.java。
- 启动项目会发现日志输出增加了建表、插数据、更新数据的语句。
- 访问http://localhost:8080/h2-console访问数据库(修改JDBC URL为: jdbc:h2:mem:test)会发现该数据库中已经存在PERSON表，其中包含一条数据。

## 2 使用Liquibase对比数据库

### 2.1 功能

对比两个数据库以文本或XML的形式输出结构上的差异。例如: 



- 创建SpringBoot的命令行启动程序: LiquiCommand.java
- 编写功能函数: diffCommand();
    - 建立数据库连接
        ~~~Java
        Connection connection1 = DriverManager.getConnection("jdbc:h2:mem:test", "sa", "sa");
        Connection connection2 = DriverManager.getConnection("jdbc:h2:mem:test1");
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