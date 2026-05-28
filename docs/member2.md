# 成员2说明（院系B + XSLT + 跨系选课）

## A. Oracle表结构（PDF表3-6~3-9）

- 账户表：账号、密码、权限、学号外键
- 学生表：学号、姓名、性别、专业、院系
- 课程表：课程号、课程名、学时、学分、教师、地点、属性
- 选课表：课程号、学号、得分

脚本位置：

- `sql/oracle/department_b_schema.sql`
- `sql/oracle/department_b_seed.sql`

## B. XSLT文件（PDF表3-16）

`xsl/` 下共12个文件：

- `formatClass.xsl`：各系课程XML -> 统一课程XML
- `formatStudent.xsl`：各系学生XML -> 统一学生XML
- `formatClassChoice.xsl`：各系选课XML -> 统一选课XML

- `studentToA.xsl` / `studentToB.xsl` / `studentToC.xsl`
- `classToA.xsl` / `classToB.xsl` / `classToC.xsl`
- `choiceToA.xsl` / `choiceToB.xsl` / `choiceToC.xsl`

### 统一格式（约定）

- 课程：`<classes><class><id/><name/><score/><teacher/><location/></class></classes>`
- 学生：`<students><student><id/><name/><sex/><major/></student></students>`
- 选课：`<choices><choice><sid/><cid/><score/></choice></choices>`

### 元素映射（PDF表3-17~3-19）

- 课程（表3-17）
  - `id` -> A:`课程编号` B:`课程号` C:`Cno`
  - `name` -> A:`课程名称` B:`课程名` C:`Cnm`
  - `score` -> A:`学分` B:`学分` C:`Cpt`
  - `teacher` -> A:`课程教师/任课教师` B:`教师` C:`Tec`
  - `location` -> A:`上课地点` B:`地点` C:`Pla`

- 学生（表3-18）
  - `id` -> A:`学号` B:`学号` C:`Sno`
  - `name` -> A:`姓名` B:`姓名` C:`Snm`
  - `sex` -> A:`性别` B:`性别` C:`Sex`
  - `major` -> A:`院系` B:`专业` C:`Sde`

- 选课（表3-19）
  - `sid` -> A:`学号` B:`学号` C:`Sno`
  - `cid` -> A:`课程编号` B:`课程编号` C:`Cno`
  - `score` -> A:`成绩` B:`得分` C:`Grd`

## C. 院系B HTTP接口（dept-b-app）

默认端口：8082（可在 `deptb.properties` 修改）

- `GET /courses`：导出B课程XML（字段：课程号/课程名/学时/学分/教师/地点/属性）
- `GET /students`：导出B学生XML（字段：学号/姓名/性别/专业/院系）
- `GET /choices`：导出B选课XML（字段：学号/课程编号/得分）
- `POST /crossSelect`：接收跨系选课请求并写入 `B_CHOICE`；若学生不存在且请求里带student，会补写学生信息

## D. 集成侧跨系选课流程（integration-crossselect）

默认端口：8090（可在 `integration-crossselect.properties` 修改）

- `POST /crossSelect`：
  1) 读取请求中的 `student` 与 `class`（允许A/B/C/统一字段命名）
  2) `formatStudent.xsl` / `formatClass.xsl` 转为统一格式
  3) 组装统一 `choice`（sid/cid）
  4) 按 `targetDept` 选择 `studentTo{A|B|C}.xsl` / `classTo{A|B|C}.xsl` / `choiceTo{A|B|C}.xsl` 转为目标院系格式
  5) 转发至对应配置：
     - `deptA.crossSelectUrl` / `deptB.crossSelectUrl` / `deptC.crossSelectUrl`

说明：若请求未提供 `targetDept`，默认按 `B` 处理；当 `targetDept=A/C` 时，需要在 `integration-crossselect.properties` 中补充对应URL。

### 请求示例（发给集成侧 /crossSelect）

```xml
<crossSelectRequest>
  <targetDept>B</targetDept>
  <student>
    <学号>B00000001</学号>
    <姓名>学生01</姓名>
    <性别>男</性别>
    <专业>计算机科学</专业>
  </student>
  <class>
    <课程号>B0001</课程号>
    <课程名>数据结构</课程名>
    <学分>3</学分>
    <教师>王老师</教师>
    <地点>教1-101</地点>
  </class>
</crossSelectRequest>
```
