@echo off
cd /d E:\inteconsole\console
mvn -q -DskipTests clean spring-boot:run
pause
