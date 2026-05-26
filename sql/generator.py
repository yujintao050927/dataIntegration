import random

# =========================================
# 集成教务系统 SQL 测试数据自动生成器
# 作者：成员4
# 功能：
#   自动生成：
#   1. SQL Server insert_data.sql
#   2. Oracle insert_data.sql
#   3. MySQL insert_data.sql
#
# 数据规模：
#   50名学生
#   10门课程
#   每人5门选课
# =========================================


# =========================================
# 课程数据（统一）
# =========================================
courses = [
    ("C001", "JAVA程序", 4, "张老师", "A101", "Y"),
    ("C002", "数据库原理", 3, "李老师", "A102", "Y"),
    ("C003", "计算机网络", 3, "王老师", "A103", "Y"),
    ("C004", "操作系统", 4, "赵老师", "A104", "Y"),
    ("C005", "软件工程", 3, "刘老师", "A105", "Y"),
    ("C006", "编译原理", 4, "周老师", "A106", "N"),
    ("C007", "数据结构", 4, "吴老师", "A107", "N"),
    ("C008", "人工智能", 2, "陈老师", "A108", "N"),
    ("C009", "Web开发", 3, "杨老师", "A109", "N"),
    ("C010", "Linux系统", 2, "黄老师", "A110", "N"),
]

# 中文姓名池
names = [
    "张伟", "李娜", "王强", "刘洋", "陈晨",
    "赵敏", "黄涛", "杨帆", "吴倩", "周杰",
    "徐磊", "孙婷", "胡斌", "朱琳", "高翔",
    "林雪", "何超", "郭静", "马凯", "罗丹"
]

majors = [
    "计算机",
    "软件工程",
    "人工智能",
    "网络工程",
    "数据科学"
]


# =========================================
# 随机生成学生
# =========================================
def generate_students(prefix, count=50):
    students = []

    for i in range(1, count + 1):
        sid = f"{prefix}{i:03d}"
        name = random.choice(names) + str(i)
        gender = random.choice(["男", "女"])
        major = random.choice(majors)

        students.append({
            "id": sid,
            "name": name,
            "gender": gender,
            "major": major
        })

    return students


# =========================================
# 随机生成选课
# =========================================
def generate_choices(students):
    choices = []

    course_ids = [c[0] for c in courses]

    for stu in students:
        selected = random.sample(course_ids, 5)

        for cid in selected:
            score = random.randint(60, 100)

            choices.append({
                "student_id": stu["id"],
                "course_id": cid,
                "score": score
            })

    return choices


# =========================================
# SQL Server insert_data.sql
# =========================================
def generate_sqlserver():
    students = generate_students("A")
    choices = generate_choices(students)

    with open("sqlserver_insert_data.sql", "w", encoding="utf-8") as f:

        f.write("USE DepartmentA_DB;\nGO\n\n")

        # 账户
        f.write("-- Account\n")
        f.write("INSERT INTO Account VALUES ('admin','123456','root');\n")

        for stu in students:
            f.write(
                f"INSERT INTO Account VALUES "
                f"('{stu['id']}','123456','stu');\n"
            )

        # 学生
        f.write("\n-- Student\n")

        for stu in students:
            f.write(
                f"INSERT INTO Student VALUES "
                f"('{stu['id']}','{stu['name']}','{stu['gender']}','{stu['major']}','{stu['id']}');\n"
            )

        # 课程
        f.write("\n-- Course\n")

        for c in courses:
            f.write(
                f"INSERT INTO Course VALUES "
                f"('{c[0]}','{c[1]}','{c[2]}','{c[3]}','{c[4]}','{c[5]}');\n"
            )

        # 选课
        f.write("\n-- ChoiceCourse\n")

        for ch in choices:
            f.write(
                f"INSERT INTO ChoiceCourse VALUES "
                f"('{ch['course_id']}','{ch['student_id']}','{ch['score']}');\n"
            )

        f.write("GO\n")

    print("sqlserver_insert_data.sql 生成完成")


# =========================================
# Oracle insert_data.sql
# =========================================
def generate_oracle():
    students = generate_students("B")
    choices = generate_choices(students)

    with open("oracle_insert_data.sql", "w", encoding="utf-8") as f:

        # 学生
        f.write("-- Student_B\n")

        for stu in students:
            f.write(
                f"INSERT INTO Student_B VALUES "
                f"('{stu['id']}','{stu['name']}','{stu['gender']}','{stu['major']}','123456');\n"
            )

        # 账户
        f.write("\n-- Account_B\n")

        f.write(
            "INSERT INTO Account_B VALUES "
            "('admin','123456',1,NULL);\n"
        )

        for stu in students:
            f.write(
                f"INSERT INTO Account_B VALUES "
                f"('{stu['id']}','123456',2,'{stu['id']}');\n"
            )

        # 课程
        f.write("\n-- Course_B\n")

        for c in courses:
            f.write(
                f"INSERT INTO Course_B VALUES "
                f"('{c[0]}','{c[1]}','32','{c[2]}','{c[3]}','{c[4]}','{c[5]}');\n"
            )

        # 选课
        f.write("\n-- ChoiceCourse_B\n")

        for ch in choices:
            f.write(
                f"INSERT INTO ChoiceCourse_B VALUES "
                f"('{ch['course_id']}','{ch['student_id']}','{ch['score']}');\n"
            )

        f.write("\nCOMMIT;\n")

    print("oracle_insert_data.sql 生成完成")


# =========================================
# MySQL insert_data.sql
# =========================================
def generate_mysql():
    students = generate_students("C")
    choices = generate_choices(students)

    with open("mysql_insert_data.sql", "w", encoding="utf-8") as f:

        f.write("USE department_c_db;\n\n")

        # 账户
        f.write("-- Account_C\n")

        f.write(
            "INSERT INTO Account_C(acc,passwd) "
            "VALUES ('admin','123456');\n"
        )

        for stu in students:
            f.write(
                f"INSERT INTO Account_C(acc,passwd) "
                f"VALUES ('{stu['id']}','123456');\n"
            )

        # 学生
        f.write("\n-- Student_C\n")

        for stu in students:
            sex = "M" if stu["gender"] == "男" else "F"

            f.write(
                f"INSERT INTO Student_C VALUES "
                f"('{stu['id']}','{stu['name']}','{sex}','CS','123456');\n"
            )

        # 课程
        f.write("\n-- Course_C\n")

        for c in courses:
            f.write(
                f"INSERT INTO Course_C VALUES "
                f"('{c[0]}','{c[1]}',32,{c[2]},'{c[3]}','{c[4]}','{c[5]}');\n"
            )

        # 选课
        f.write("\n-- ChoiceCourse_C\n")

        for ch in choices:
            f.write(
                f"INSERT INTO ChoiceCourse_C VALUES "
                f"('{ch['course_id']}','{ch['student_id']}',{ch['score']});\n"
            )

    print("mysql_insert_data.sql 生成完成")


# =========================================
# 主程序
# =========================================
if __name__ == "__main__":

    random.seed(2026)

    generate_sqlserver()
    generate_oracle()
    generate_mysql()

    print("\n全部SQL文件生成完成！")