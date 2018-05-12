#!/bin/bash
echo Insert MySQL root password: 
read P
mysql -u root -p$P < ./scriptMySQL.sql