## 1. 在Spring项目中使用Liquibase初始化数据库

### 1.1 功能: 

- 使用Liquibase的描述文件(XML, JSON, YML)来创建数据库可以使用相同的描述文件在不同的数据库上创建数据库。
- Liquibase能够加载、使用、生成这些结构化的配置文件。

### 1.2 步骤: 

- 创建SpringBoot的Web项目并添加依赖项: h2, liquibase-core, springboot-starter-jdbc
- 配置Applications.properties
    ~~~Properties
    spring.datasource.driver-class-name=org.h2.Driver
    spring.datasource.url=jdbc:h2:./test;
    spring.datasource.username=sa
    spring.datasource.password=sa
    spring.h2.console.enabled=true
    
    logging.level.liquibase=INFO
    
    spring.liquibase.change-log=classpath:/changelog-master.xml
    ~~~
- 编写Liquibase的数据库创建xml脚本
    - 主文件: changelog-master.xml, 包含若干具体任务的脚本
    - 建表: create-person-table-changelog-1.xml, 在给定的数据库创建表
    - 插数据: insert-person-table-changelog-2.xml, 
    - 更新数据: update-person-table-precondition-3.xml
- 编写SpringBoot的启动代码： LiquiApplication.java
- 启动项目会发现日志输出增加了建表、插数据、更新数据的语句
- 访问http://localhost:8080/h2-console访问数据库(**注意: 修改JDBC URL为: jdbc:h2:./test**)会发现该数据库中已经存在PERSON表，其中包含一条数据
