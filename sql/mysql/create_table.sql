-- =========================================
-- 院系C（MySQL）数据库建表脚本
-- 作者：成员4
-- 项目：基于XML数据集成的集成教务系统
-- =========================================


-- =========================================
-- 1. 创建数据库
-- =========================================
CREATE DATABASE IF NOT EXISTS department_c_db
    DEFAULT CHARACTER SET utf8mb4;

USE department_c_db;



-- =========================================
-- 2. 账户表 Account_C
-- 对应 PDF 表3-10
-- =========================================
CREATE TABLE Account_C (

                           acc VARCHAR(12) PRIMARY KEY,            -- 账户
                           passwd VARCHAR(12) NOT NULL,            -- 密码
                           create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);



-- =========================================
-- 3. 学生表 Student_C
-- 对应 PDF 表3-11
-- =========================================
CREATE TABLE Student_C (

                           sno VARCHAR(9) PRIMARY KEY,             -- 学号
                           snm VARCHAR(10) NOT NULL,               -- 姓名
                           sex VARCHAR(1) NOT NULL,                -- 性别
                           sde VARCHAR(6) NOT NULL,                -- 院系
                           pwd CHAR(6) NOT NULL                    -- 密码

);



-- =========================================
-- 4. 课程表 Course_C
-- 对应 PDF 表3-12
-- =========================================
CREATE TABLE Course_C (

                          cno CHAR(4) PRIMARY KEY,                -- 课程编号
                          cnm VARCHAR(10) NOT NULL,               -- 课程名称
                          ctm INTEGER NOT NULL,                   -- 课时
                          cpt INTEGER NOT NULL,                   -- 学分
                          tec VARCHAR(20) NOT NULL,               -- 教师
                          pla VARCHAR(18) NOT NULL,               -- 地点
                          share_flag CHAR(1) NOT NULL             -- 是否共享(Y/N)

);



-- =========================================
-- 5. 选课表 ChoiceCourse_C
-- 对应 PDF 表3-11
-- =========================================
CREATE TABLE ChoiceCourse_C (

                                cno CHAR(4) NOT NULL,                   -- 课程编号
                                sno CHAR(9) NOT NULL,                   -- 学号
                                grd INTEGER,                            -- 成绩

    -- 联合唯一约束
                                CONSTRAINT uq_choicecourse_c
                                    UNIQUE(cno, sno),

    -- 外键：课程
                                CONSTRAINT fk_choicecourse_c_course
                                    FOREIGN KEY (cno)
                                        REFERENCES Course_C(cno),

    -- 外键：学生
                                CONSTRAINT fk_choicecourse_c_student
                                    FOREIGN KEY (sno)
                                        REFERENCES Student_C(sno)

);



-- =========================================
-- 6. 测试查询
-- =========================================
SELECT * FROM Account_C;
SELECT * FROM Student_C;
SELECT * FROM Course_C;
SELECT * FROM ChoiceCourse_C;