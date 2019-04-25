# Liquibase Spring示例

## 概述

在SpringBoot项目中使用LiquiBase，分析数据库版本差异。根据差异维护数据库结构。

- 使用Liquibase初始化数据库: LiquiApplication.java（参考: https://javadeveloperzone.com/spring-boot/spring-boot-liquibase-example/)
- 使用Liquibase区分数据库差异: LiquiCommand.java

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