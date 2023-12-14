#!/bin/bash

# Instalar Java
apt-get install -y default-jre

# Instalar MySQL
apt-get install -y mysql-server

# Cargar la base de datos
mysql -u root -p < /mysql/BBDD.sql

# Instalar tu aplicación
cp ./bin/PCF_Server_IntelliJ.jar /usr/local
