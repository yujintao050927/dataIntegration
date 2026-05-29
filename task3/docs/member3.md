# 成员3说明（院系C + 统计功能 + 跨系退课 + 通信框架）

## A. MySQL表结构（PDF表3-10~3-12）

- 账户表 Account_C：acc(账户PK)、passwd(密码)、create_date
- 学生表 Student_C：sno(学号PK)、snm(姓名)、sex(性别)、sde(院系)、pwd(密码)
- 课程表 Course_C：cno(课程编号PK)、cnm(课程名称)、ctm(课时)、cpt(学分)、tec(教师)、pla(地点)、share_flag(是否共享Y/N)
- 选课表 ChoiceCourse_C：cno(课程编号)、sno(学号)、grd(成绩)，联合唯一约束(cno,sno)

脚本位置：

- `sql/mysql/create_table.sql`
- `sql/mysql/mysql_insert_data.sql`

初始化后：50名学生、10门课程（其中5门share_flag='Y'共享）、250条选课、管理员账号 `admin/123456`、学生账号默认 `学号/123456`。

## B. 院系C HTTP接口（dept-c-app）

默认端口：8083（可在 `deptc.properties` 修改）

- `GET /courses`：导出C课程XML（字段：Cno/Cnm/Cpt/Tec/Pla/share_flag）
- `GET /students`：导出C学生XML（字段：Sno/Snm/Sex/Sde）
- `GET /choices`：导出C选课XML（字段：Sno/Cno/Grd）
- `POST /crossSelect`：接收跨系选课请求并写入 `ChoiceCourse_C`；若学生不存在且请求里带student，会补写学生信息
- `POST /crossDrop`：接收跨系退课请求，删除 `ChoiceCourse_C` 中对应记录

## C. 集成服务器完整框架（integration-server）

默认端口：8090（可在 `integration-server.properties` 修改）

该模块为**统一的集成服务器入口**，包含以下路由：

### C1. POST /share —— 课程共享流程

1. 读取请求中的 `requester`（A/B/C，默认C）
2. 向其他两个院系的 `/courses` 请求课程XML
3. 对C院系原始XML过滤 `share_flag='Y'` 的课程（B/A默认全部保留）
4. 用 `formatClass.xsl` 将各院系XML转为统一格式，并标记 `sourceDept`
5. 用 `classTo{requester}.xsl` 将统一格式转为请求者本地格式
6. 返回转换后的课程XML

### C2. POST /crossSelect —— 跨系选课流程

与成员2的 `integration-crossselect` 逻辑一致，迁移到本模块以统一入口：

1. 读取请求中的 `student`、`class` 与 `targetDept`
2. `formatStudent.xsl` / `formatClass.xsl` 转为统一格式
3. 组装统一 `choice`（sid/cid）
4. 按 `targetDept` 选择 `studentTo{A|B|C}.xsl` / `classTo{A|B|C}.xsl` / `choiceTo{A|B|C}.xsl` 转为目标院系格式
5. 转发至对应配置：`deptA.crossSelectUrl` / `deptB.crossSelectUrl` / `deptC.crossSelectUrl`

### C3. POST /crossDrop —— 跨系退课流程

流程与跨系选课类似：

1. 读取请求中的 `student`、`class` 与 `targetDept`
2. 通过XSLT转为统一格式，再转为目标院系格式
3. 转发至对应配置：`deptA.crossDropUrl` / `deptB.crossDropUrl` / `deptC.crossDropUrl`
4. 目标院系的 `/crossDrop` 接口删除对应选课记录

### C4. GET /statistics —— 统计功能

1. 向A、B、C分别请求 `/students`、`/courses`、`/choices`
2. 用 `formatStudent.xsl`、`formatClass.xsl`、`formatClassChoice.xsl` 转为统一格式
3. 统计并返回XML：
   - `totalStudents`：总学生数（三院学生互不覆盖，直接求和）
   - `totalCourses`：总课程数（三院课程可能重叠，直接求和）
   - `totalChoices`：总选课人次（直接求和）
   - `details`：各院系明细

## D. 运行说明

### 1. 编译打包

在 `task3/` 下执行：

```bash
mvn -DskipTests package
```

输出可执行jar：

- `dept-c-app/target/dept-c-app-1.0.0-SNAPSHOT-shaded.jar`
- `integration-server/target/integration-server-1.0.0-SNAPSHOT-shaded.jar`

### 2. 运行（院系C）

1) 修改数据库连接配置：
   - `dept-c-app/src/main/resources/deptc.properties`（或在运行目录放同名 `deptc.properties` 覆盖）

2) 运行：

```bash
java -jar dept-c-app/target/dept-c-app-1.0.0-SNAPSHOT-shaded.jar
```

### 3. 运行（集成服务器）

1) 配置（可选）：
   - `integration-server/src/main/resources/integration-server.properties`
   - 或在运行目录放 `integration-server.properties` 覆盖

其中 `xslt.dir` 默认指向工作区根目录 `./xsl`。

2) 运行：

```bash
java -jar integration-server/target/integration-server-1.0.0-SNAPSHOT-shaded.jar
```

### 4. 院系C GUI 功能

- 学生登录后可查看**本院课程**、**共享课程**（从集成服务器 `/share` 拉取）、**我的选课**
- 支持本院选课/退课、跨系选课（选中共享课程后调用集成服务器 `/crossSelect`）
- 管理员登录后可查看全部学生、课程、选课数据

## E. 端口分配与依赖关系

| 服务 | 端口 | 说明 |
|------|------|------|
| 院系A | 8081 | 成员1负责（需配置到 integration-server.properties） |
| 院系B | 8082 | 成员2已交付 |
| 院系C | 8083 | 本模块 |
| 集成服务器 | 8090 | 本模块 |

启动顺序建议：先启动三个院系服务器（A/B/C），再启动集成服务器。
