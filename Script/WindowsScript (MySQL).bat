@Echo Off
Set /P psw= Insert MySQL root password: 
mysql -u root -p%psw% < scriptMySQL.sql
Set /P var = Script executed. Press ENTER to exit...