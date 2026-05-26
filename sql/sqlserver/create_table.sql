-- =========================================
-- 院系A（SQL Server）数据库建表脚本
-- 作者：成员4
-- 项目：基于XML数据集成的集成教务系统
-- =========================================


-- =========================================
-- 1. 创建数据库
-- =========================================
CREATE DATABASE DepartmentA_DB;
GO

USE DepartmentA_DB;
GO


-- =========================================
-- 2. 账户表 Account
-- 对应 PDF 表3-2
-- =========================================
CREATE TABLE Account (
                         account_name VARCHAR(10) PRIMARY KEY,   -- 账户名
                         password VARCHAR(6) NOT NULL,           -- 密码
                         permission CHAR(4) NOT NULL             -- 权限
);
GO


-- =========================================
-- 3. 学生表 Student
-- 对应 PDF 表3-3
-- =========================================
CREATE TABLE Student (
                         student_id VARCHAR(12) PRIMARY KEY,     -- 学号
                         student_name VARCHAR(10) NOT NULL,      -- 姓名
                         gender VARCHAR(2) NOT NULL,             -- 性别
                         department VARCHAR(10) NOT NULL,        -- 院系

                         account_name VARCHAR(10),               -- 关联账户

                         CONSTRAINT FK_Student_Account
                             FOREIGN KEY (account_name)
                                 REFERENCES Account(account_name)
);
GO


-- =========================================
-- 4. 课程表 Course
-- 对应 PDF 表3-4
-- =========================================
CREATE TABLE Course (
                        course_id VARCHAR(8) PRIMARY KEY,       -- 课程编号
                        course_name VARCHAR(10) NOT NULL,       -- 课程名称
                        credit VARCHAR(2) NOT NULL,             -- 学分
                        teacher VARCHAR(10) NOT NULL,           -- 授课老师
                        location VARCHAR(20) NOT NULL,          -- 授课地点
                        is_shared CHAR(1) NOT NULL              -- 是否共享(Y/N)
);
GO


-- =========================================
-- 5. 选课表 ChoiceCourse
-- 对应 PDF 表3-5
-- =========================================
CREATE TABLE ChoiceCourse (

                              course_id VARCHAR(8) NOT NULL,          -- 课程编号
                              student_id VARCHAR(12) NOT NULL,        -- 学生编号
                              score VARCHAR(3),                       -- 成绩

    -- 联合唯一约束
                              CONSTRAINT UQ_ChoiceCourse
                                  UNIQUE(course_id, student_id),

    -- 外键：课程
                              CONSTRAINT FK_ChoiceCourse_Course
                                  FOREIGN KEY (course_id)
                                      REFERENCES Course(course_id),

    -- 外键：学生
                              CONSTRAINT FK_ChoiceCourse_Student
                                  FOREIGN KEY (student_id)
                                      REFERENCES Student(student_id)
);
GO


-- =========================================
-- 6. 查看所有表（测试）
-- =========================================
SELECT * FROM Account;
SELECT * FROM Student;
SELECT * FROM Course;
SELECT * FROM ChoiceCourse;
GO