# Task3（成员2交付：院系B + XSLT转换 + 跨系选课流程）

本工作区当前实现了成员2需要交付的内容：

- 院系B（Oracle）数据库脚本（建表 + 初始化数据）
- 院系B可运行程序（Swing GUI + HTTP接口 + DOM4J导出XML + JDBC写库）
- XSLT转换文件（共12个，见 `xsl/`）
- 集成侧“跨系选课”流程代码（可运行HTTP服务，实现 `/crossSelect`；支持targetDept=A/B/C，默认B）

## 目录

- `sql/oracle/`：Oracle建表与初始化数据脚本
- `xsl/`：XSLT转换文件（PDF表3-16~3-19对应）
- `dept-b-app/`：院系B应用（端口默认8082）
- `integration-crossselect/`：集成侧跨系选课模块（端口默认8090）
- `integration-xslt/`：XSLT转换工具库（供集成侧使用）

## 1. Oracle初始化（院系B）

1) 在Oracle中创建/选择一个Schema（例如使用 `system` 仅为演示；实际建议使用单独用户）。

2) 依次执行：

- `sql/oracle/department_b_schema.sql`
- `sql/oracle/department_b_seed.sql`

初始化后：50名学生、10门课程、250条选课、管理员账号 `adminB/admin123`、学生账号默认 `学号/123456`。

## 2. 编译打包

在工作区根目录执行：

- `mvn -DskipTests package`

输出可执行jar：

- `dept-b-app/target/dept-b-app-1.0.0-SNAPSHOT-shaded.jar`
- `integration-crossselect/target/integration-crossselect-1.0.0-SNAPSHOT-shaded.jar`

## 3. 运行（院系B）

1) 修改数据库连接配置：

- `dept-b-app/src/main/resources/deptb.properties`（或在运行目录放同名 `deptb.properties` 覆盖）

2) 运行：

- `java -jar dept-b-app/target/dept-b-app-1.0.0-SNAPSHOT-shaded.jar`

院系B对外HTTP接口：

- `GET  /courses`：返回B院系课程XML
- `GET  /students`：返回B院系学生XML
- `GET  /choices`：返回B院系选课XML
- `POST /crossSelect`：接收跨系选课请求（B格式）并写入数据库

## 4. 运行（集成侧跨系选课模块）

1) 配置（可选）：

- `integration-crossselect/src/main/resources/integration-crossselect.properties`
- 或在运行目录放 `integration-crossselect.properties` 覆盖

其中 `xslt.dir` 默认指向工作区根目录 `./xsl`。

2) 运行：

- `java -jar integration-crossselect/target/integration-crossselect-1.0.0-SNAPSHOT-shaded.jar`

接口：

- `POST /crossSelect`：接收“源院系格式”的 `student/class`，转统一格式，再转目标院系格式（A/B/C），最后转发到对应院系的 `/crossSelect`。

说明：如果请求中未携带 `targetDept`，默认按 `B` 处理；当 `targetDept=A` 或 `C` 时，需要在 `integration-crossselect.properties` 中配置 `deptA.crossSelectUrl` / `deptC.crossSelectUrl`。

XSLT文件清单与字段映射说明见 `docs/member2.md`。
