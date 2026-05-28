@echo off
echo 正在启动集成服务器...
echo.
echo 请确保已编译项目
echo.
mvn exec:java -Dexec.mainClass="com.educ.integration.server.IntegrationServer"
pause
