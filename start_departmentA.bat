@echo off
echo 正在启动院系A教务系统...
echo.
echo 请确保已编译项目
echo.
mvn exec:java -Dexec.mainClass="com.educ.departmentA.Main"
pause
