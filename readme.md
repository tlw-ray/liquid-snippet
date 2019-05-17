# Liquibase Spring示例

## 概述

在SpringBoot项目中使用LiquiBase，分析数据库版本差异。根据差异维护数据库结构。

- 使用Liquibase初始化数据库: LiquiApplication.java（参考: https://javadeveloperzone.com/spring-boot/spring-boot-liquibase-example/)
- 使用Liquibase区分数据库差异: LiquiDiff.java

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
- MongoDB(用于存放快照)
    
## [1. 在Spring项目中使用Liquibase初始化数据库](src/main/java/com/winning/ptc/liquid/snippet/a01_init/readme.md)
## [2. 使用Liquibase对比数据库](src/main/java/com/winning/ptc/liquid/snippet/a02_diff/readme.md)
## [3. 使用Liquibase建立数据库快照](src/main/java/com/winning/ptc/liquid/snippet/a03_snapshot/readme.md)
## [4. Diff 通过底层API对快照进行对比](src/main/java/com/winning/ptc/liquid/snippet/a04_diff/readme.md)
## [5. Init 根据改变日志创建数据库](src/main/java/com/winning/ptc/liquid/snippet/a05_init/readme.md)
## [6. 数据库版本差异检查的完整实现](src/main/java/com/winning/ptc/liquid/snippet/a06_flow/readme.md)
## [7. SQLServer数据库快照存放在Mongo以便对之前的快照做对比](src/main/java/com/winning/ptc/liquid/snippet/a07_flow_mongo/readme.md)
## [8. 数据库版本差异检查系统RESTful服务](src/main/java/com/winning/ptc/liquid/snippet/a08_system/readme.md)
