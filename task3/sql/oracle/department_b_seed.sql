-- 院系B（Oracle）初始化数据脚本
-- 目标：50名学生、10门课程、每人5门选课（共250条选课）

-- 课程（10门）
INSERT INTO B_COURSE(CNO, CNM, HOURS, CREDIT, TEACHER, LOCATION, ATTR) VALUES ('B0001', '数据结构', '48', '3', '王老师', '教1-101', '必');
INSERT INTO B_COURSE(CNO, CNM, HOURS, CREDIT, TEACHER, LOCATION, ATTR) VALUES ('B0002', '操作系统', '48', '3', '李老师', '教1-102', '必');
INSERT INTO B_COURSE(CNO, CNM, HOURS, CREDIT, TEACHER, LOCATION, ATTR) VALUES ('B0003', '数据库原理', '48', '3', '张老师', '教1-103', '必');
INSERT INTO B_COURSE(CNO, CNM, HOURS, CREDIT, TEACHER, LOCATION, ATTR) VALUES ('B0004', '计算机网络', '48', '3', '赵老师', '教1-104', '必');
INSERT INTO B_COURSE(CNO, CNM, HOURS, CREDIT, TEACHER, LOCATION, ATTR) VALUES ('B0005', '编译原理', '48', '3', '钱老师', '教1-105', '必');
INSERT INTO B_COURSE(CNO, CNM, HOURS, CREDIT, TEACHER, LOCATION, ATTR) VALUES ('B0006', '人工智能导论', '32', '2', '孙老师', '教2-201', '选');
INSERT INTO B_COURSE(CNO, CNM, HOURS, CREDIT, TEACHER, LOCATION, ATTR) VALUES ('B0007', '软件工程', '32', '2', '周老师', '教2-202', '选');
INSERT INTO B_COURSE(CNO, CNM, HOURS, CREDIT, TEACHER, LOCATION, ATTR) VALUES ('B0008', '机器学习', '32', '2', '吴老师', '教2-203', '选');
INSERT INTO B_COURSE(CNO, CNM, HOURS, CREDIT, TEACHER, LOCATION, ATTR) VALUES ('B0009', '信息安全', '32', '2', '郑老师', '教2-204', '选');
INSERT INTO B_COURSE(CNO, CNM, HOURS, CREDIT, TEACHER, LOCATION, ATTR) VALUES ('B0010', '分布式系统', '32', '2', '冯老师', '教2-205', '选');

-- 学生（50名）
DECLARE
  i NUMBER := 1;
  v_sno VARCHAR2(9);
  v_name VARCHAR2(10);
  v_sex VARCHAR2(2);
BEGIN
  WHILE i <= 50 LOOP
    v_sno := 'B' || LPAD(TO_CHAR(i), 8, '0'); -- B00000001 ~ B00000050
    v_name := '学生' || LPAD(TO_CHAR(i), 2, '0');
    v_sex := CASE WHEN MOD(i, 2) = 0 THEN '女' ELSE '男' END;

    INSERT INTO B_STUDENT(SNO, SNM, SEX, MAJOR, DEPT)
    VALUES (v_sno, v_name, v_sex, '计算机科学', '院系B');

    i := i + 1;
  END LOOP;
END;
/

-- 账户：管理员 + 学生账号
INSERT INTO B_ACCOUNT(ACC, PASSWD, ROLE, SNO) VALUES ('adminB', 'admin123', 0, NULL);

DECLARE
  i NUMBER := 1;
  v_sno VARCHAR2(9);
BEGIN
  WHILE i <= 50 LOOP
    v_sno := 'B' || LPAD(TO_CHAR(i), 8, '0');
    INSERT INTO B_ACCOUNT(ACC, PASSWD, ROLE, SNO)
    VALUES (v_sno, '123456', 1, v_sno);
    i := i + 1;
  END LOOP;
END;
/

-- 选课：每人5门（轮转分配）
DECLARE
  i NUMBER := 1;
  k NUMBER;
  v_sno VARCHAR2(9);
  v_cno VARCHAR2(5);
  v_course_idx NUMBER;
BEGIN
  WHILE i <= 50 LOOP
    v_sno := 'B' || LPAD(TO_CHAR(i), 8, '0');
    k := 0;
    WHILE k < 5 LOOP
      v_course_idx := MOD(i + k - 1, 10) + 1; -- 1..10
      v_cno := 'B' || LPAD(TO_CHAR(v_course_idx), 4, '0'); -- B0001..B0010
      INSERT INTO B_CHOICE(SNO, CNO, SCORE) VALUES (v_sno, v_cno, NULL);
      k := k + 1;
    END LOOP;
    i := i + 1;
  END LOOP;
END;
/

COMMIT;
