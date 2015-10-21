#####################################################################################

# Create Permissions

GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY '';
FLUSH PRIVILEGES;

#####################################################################################

# Create Database

DROP DATABASE IF EXISTS play;
CREATE DATABASE play;
USE play;

#####################################################################################

