## 6. 数据库版本差异检查的完整实现

基本流程: 

1. 删库
2. 建库
3. 生成快照1
4. 建表
5. 生成快照2
6. 对比快照1和快照2
7. 生成可阅读报告和数据库结构SQL同步语句