@Echo Off
Set /P psw= Insert PostgreSQL root password: 
psql -U postgres -a -f scriptPostgreSQL.sql
Set /P var = Script executed. Press ENTER to exit...