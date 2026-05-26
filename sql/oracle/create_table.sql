-- =========================================
-- 院系B（Oracle）数据库建表脚本
-- 作者：成员4
-- 项目：基于XML数据集成的集成教务系统
-- =========================================


-- =========================================
-- 1. 账户表 Account_B
-- 对应 PDF 表3-6
-- =========================================
CREATE TABLE Account_B (

                           account_name VARCHAR2(12) PRIMARY KEY,  -- 账户名
                           password VARCHAR2(12) NOT NULL,         -- 密码
                           level_num NUMBER(2) NOT NULL,           -- 级别

                           student_id VARCHAR2(9)                  -- 客体（关联学生）
);




-- =========================================
-- 2. 学生表 Student_B
-- 对应 PDF 表3-7
-- =========================================
CREATE TABLE Student_B (

                           student_id VARCHAR2(9) PRIMARY KEY,     -- 学号
                           student_name VARCHAR2(10) NOT NULL,     -- 姓名
                           gender VARCHAR2(2) NOT NULL,            -- 性别
                           major VARCHAR2(16) NOT NULL,            -- 专业
                           password VARCHAR2(6) NOT NULL           -- 密码
);




-- =========================================
-- 3. 给 Account_B 添加外键
-- 因为 Student_B 必须先创建
-- =========================================
ALTER TABLE Account_B
    ADD CONSTRAINT FK_AccountB_StudentB
        FOREIGN KEY (student_id)
            REFERENCES Student_B(student_id);




-- =========================================
-- 4. 课程表 Course_B
-- 对应 PDF 表3-8
-- =========================================
CREATE TABLE Course_B (

                          course_id VARCHAR2(5) PRIMARY KEY,      -- 编号
                          course_name VARCHAR2(16) NOT NULL,      -- 名称
                          class_time VARCHAR2(2) NOT NULL,        -- 课时
                          credit VARCHAR2(1) NOT NULL,            -- 学分
                          teacher VARCHAR2(10) NOT NULL,          -- 老师
                          location VARCHAR2(20) NOT NULL,         -- 地点
                          is_shared CHAR(1) NOT NULL              -- 是否共享(Y/N)
);




-- =========================================
-- 5. 选课表 ChoiceCourse_B
-- 对应 PDF 表3-9
-- =========================================
CREATE TABLE ChoiceCourse_B (

                                course_id VARCHAR2(5) NOT NULL,         -- 课程编号
                                student_id VARCHAR2(9) NOT NULL,        -- 学号
                                score VARCHAR2(3),                      -- 得分

    -- 联合唯一约束
                                CONSTRAINT UQ_ChoiceCourse_B
                                    UNIQUE(course_id, student_id),

    -- 外键：课程
                                CONSTRAINT FK_ChoiceCourseB_CourseB
                                    FOREIGN KEY (course_id)
                                        REFERENCES Course_B(course_id),

    -- 外键：学生
                                CONSTRAINT FK_ChoiceCourseB_StudentB
                                    FOREIGN KEY (student_id)
                                        REFERENCES Student_B(student_id)
);




-- =========================================
-- 6. 测试查询
-- =========================================
SELECT * FROM Account_B;
SELECT * FROM Student_B;
SELECT * FROM Course_B;
SELECT * FROM ChoiceCourse_B;