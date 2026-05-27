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
