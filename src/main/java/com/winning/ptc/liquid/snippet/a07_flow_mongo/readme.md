## SQLServer数据库快照存放在Mongo以便对之前的快照做对比

### 对比数据库当前和历史结构

要对比单个数据库不同时间的结构变化，需要先建立数据库快照，并将快照存盘。
在之后的某个时间对已经存储的快照进行对比，可以了解两个快照间数据库结构发生的变化。
下面会使用Mongo数据库作为快照的存储方案，实现对比数据库当前和历史的结构。

### 基本流程

- 连库
- 建表
- 做快照1并将快照存入Mongo数据库备案
- 改表
- 做快照2并将快照存入Mongo数据库备案
- 从mongo数据库加载并对比快照1, 2 生成报告和可执行语句
- 生成快照比对报告
- 生成SQL补丁

### 难点

- 问题: Liquibase的json形式快照中键存在'.'字符，这种形式的JSON存入MongoDB会报异常。

        Exception in thread "main" java.lang.IllegalArgumentException: Invalid BSON field

- 解决: 需要对JSON写入和读出时做处理可以使用MongoJsonConverter.java在将json存入mongoDB前做encode，从mongoDB取出时做decode通过替换json的所有字段中的'.'字符为'___d___'来实现。

    示例代码见T01_WriteMongo.java: 

    - 写入前转义: 
    
    ~~~java
    private static void insert(String uuid, String json){
        //写入前转义，将字段名中的'.'替换为'___d___'
        json = mongoJsonConverter.encode(json);
        System.out.println(json);
        Document document = Document.parse(json);
        document.put("_id", uuid);
        mongoCollection.insertOne(document);
    }
    ~~~

    - 查询后转义: 
    
    ~~~java
    private static String find(String uuid){
        Bson filter = createFilter(uuid);
        FindIterable<Document> findIterable = mongoCollection.find(filter);
        String json = findIterable.first().toJson();
        //查询后转义: 将字段中的'__d__'替换为'.'
        return mongoJsonConverter.decode(json);
    }
    ~~~



### 扩展