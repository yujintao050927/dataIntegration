# 基于XML数据集成的教务管理系统

## 项目简介

本项目是一个基于XML数据集成技术的多院系教务管理系统，实现了不同院系（使用不同数据库系统）之间的课程共享、跨院选课、跨院退课和统计功能。

## 系统架构

- **院系A**: 使用 SQL Server 数据库
- **院系B**: 使用 Oracle 数据库  
- **院系C**: 使用 MySQL 数据库
- **集成服务器**: 负责数据集成、格式转换、请求转发

## 分工情况

### 成员一（当前完成部分）
- ✅ 院系A的完整教务系统实现
- ✅ SQL Server 数据库连接和DAO层
- ✅ XML生成和解析（使用DOM4J）
- ✅ HTTP客户端，与集成服务器通信
- ✅ GUI界面（登录、课程查看、选课/退课、共享课程）
- ✅ XSD Schema文件定义
- ✅ XML验证模块（基于Xerces）
- ✅ 课程共享流程实现
- ✅ 集成服务器框架

### 其他成员
- **成员二**: 负责院系B和XSLT转换
- **成员三**: 负责院系C和统计功能
- **成员四**: 负责数据准备、文档和测试

## 目录结构

```
big/
├── src/main/java/
│   ├── com/educ/departmentA/          # 院系A代码
│   │   ├── model/                    # 数据模型
│   │   ├── dao/                      # 数据访问层
│   │   ├── util/                     # 工具类
│   │   ├── xml/                      # XML处理
│   │   ├── http/                     # HTTP通信
│   │   ├── gui/                      # 图形界面
│   │   └── Main.java                 # 主程序入口
│   └── com/educ/integration/         # 集成服务器代码
│       ├── validation/               # XML验证
│       ├── xslt/                     # XSLT转换
│       └── server/                   # HTTP服务器
├── sql/                              # SQL脚本
│   ├── mysql/                        # MySQL脚本
│   ├── oracle/                       # Oracle脚本
│   └── sqlserver/                    # SQL Server脚本
├── xsd/                              # XML Schema定义
├── xsl/                              # XSLT转换文件
├── docs/                             # 文档目录
├── pom.xml                           # Maven配置
├── build.bat                         # 编译脚本
├── start_departmentA.bat             # 启动院系A
├── start_integration_server.bat      # 启动集成服务器
└── README.md
```

## 快速开始

### 环境要求
- JDK 11+
- Maven 3.6+
- SQL Server (院系A)
- Oracle (院系B)
- MySQL (院系C)

### 1. 数据库准备

首先执行各院系的SQL脚本创建数据库和表结构，并插入测试数据：

- **院系A**: 执行 `sql/sqlserver/create_table.sql` 和 `sql/sqlserver/sqlserver_insert_data.sql`
- **院系B**: 执行 `sql/oracle/create_table.sql` 和 `sql/oracle/oracle_insert_data.sql`
- **院系C**: 执行 `sql/mysql/create_table.sql` 和 `sql/mysql/mysql_insert_data.sql`

### 2. 配置数据库连接

修改 `src/main/java/com/educ/departmentA/util/DBUtil.java` 中的数据库连接信息：

```java
private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=DepartmentA_DB;encrypt=false;trustServerCertificate=true;";
private static final String USER = "your_username";
private static final String PASSWORD = "your_password";
```

### 3. 编译项目

使用Maven编译项目：

```bash
mvn clean compile
```

或在Windows上双击运行 `build.bat`

### 4. 启动集成服务器

```bash
mvn exec:java -Dexec.mainClass="com.educ.integration.server.IntegrationServer"
```

或在Windows上双击运行 `start_integration_server.bat`

集成服务器将在端口 8090 上启动。

### 5. 启动院系A系统

```bash
mvn exec:java -Dexec.mainClass="com.educ.departmentA.Main"
```

或在Windows上双击运行 `start_departmentA.bat`

### 6. 使用系统

#### 测试账号
- **管理员**: 账号 `admin`，密码 `123456`
- **学生**: 账号 `A001` - `A050`，密码 `123456`

#### 功能说明
1. **登录**: 使用学生账号或管理员账号登录
2. **本院课程**: 查看本院所有课程并进行选课
3. **我的选课**: 查看已选课程并进行退课
4. **共享课程**: 查看其他院系的共享课程，进行跨院选课（需要集成服务器运行）

## 核心功能

### 1. XML数据交换
- 使用DOM4J进行XML的生成和解析
- 使用XSD Schema进行XML格式验证
- 使用XSLT进行不同院系数据格式的转换

### 2. 课程共享流程
参考PDF图3-13的流程：
1. 院系A向集成服务器请求共享课程
2. 集成服务器向院系B、C请求课程数据
3. 各院系返回XML格式的课程数据
4. 集成服务器验证XML格式
5. 使用XSLT转换为统一格式
6. 合并数据后再转换为院系A的格式
7. 返回给院系A

### 3. HTTP通信
- 使用Java内置的HttpURLConnection进行HTTP通信
- 集成服务器使用com.sun.net.httpserver提供HTTP服务
- 所有请求和响应都使用XML格式

## 测试数据

系统已预置以下测试数据：
- 每个院系50名学生
- 每个院系10门课程（其中5门为共享课程）
- 每个学生5门选课

## 技术栈

- **编程语言**: Java 11
- **构建工具**: Maven
- **XML处理**: DOM4J 2.1.4
- **XML验证**: Xerces 2.12.2
- **GUI框架**: Java Swing
- **数据库**: 
  - SQL Server (院系A)
  - Oracle (院系B)
  - MySQL (院系C)
- **HTTP服务**: com.sun.net.httpserver

## 注意事项

1. 确保数据库服务已启动且可正常连接
2. 集成服务器需要在使用共享课程、跨院选课等功能前启动
3. 修改数据库连接配置后需要重新编译项目
4. 各院系需要实现自己的HTTP服务接口供集成服务器调用（当前使用模拟数据）

## 开发计划

- [ ] 完善院系B和院系C的实现
- [ ] 实现真实的院系间HTTP通信
- [ ] 完善统计功能和可视化展示
- [ ] 添加更多单元测试和集成测试
- [ ] 优化GUI界面和用户体验

## 许可证

本项目仅供课程学习使用。

## 联系方式

如有问题，请联系项目组成员。
# IntegratedEducationalSystem

本仓库为“基于XML数据集成的集成教务系统”项目骨架与各成员交付物汇总。

## 成员2交付物（院系B + XSLT + 跨系选课）

- XSLT 转换文件（12个）：`xsl/`
- Oracle（院系B）建表与种子数据脚本：`sql/oracle/department_b_schema.sql`、`sql/oracle/department_b_seed.sql`
- 可运行源码（Maven，多模块）：`task3/`
	- `dept-b-app`：院系B应用（GUI + HTTP接口 + XML导出 + JDBC写库）
	- `integration-xslt`：XSLT 转换工具库
	- `integration-crossselect`：集成侧跨系选课模块（HTTP `/crossSelect`，支持 targetDept=A/B/C，默认B）

### 构建

在 `task3/` 下执行：

`mvn -DskipTests package`

### 运行

运行前按需修改：

- `task3/dept-b-app/src/main/resources/deptb.properties`
- `task3/integration-crossselect/src/main/resources/integration-crossselect.properties`

更多说明见：`docs/member2.md` 与 `task3/README.md`。
