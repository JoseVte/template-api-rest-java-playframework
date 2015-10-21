#!/bin/bash

echo "=========================================="
echo "Provision VM START"
echo "=========================================="
# Setup script designed to get a Ubuntu 14.04 LTS server
# up and running with secure defaults.

# =================
# Google DNS Server
# =================

DNS=$(cat <<EOF
nameserver 8.8.8.8
EOF
)
echo "${DNS}" > /etc/resolv.conf

#variables
activatorVersion="1.3.5"
sbtVersion="0.13.9"
 
# =============
# Update system
# =============
 
# get system up to date
apt-get update && apt-get upgrade -y
add-apt-repository ppa:webupd8team/java
apt-get -y -q update

# =============
# Base packages
# =============
 
# install base packages
apt-get -y -q install software-properties-common htop
apt-get -y -q install build-essential
apt-get -y -q install tcl8.5


###############################################
# Install Java 8
###############################################
# sudo apt-get install -y openjdk-8-jdk
echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections
apt-get -y -q install oracle-java8-installer
update-java-alternatives -s java-8-oracle

###############################################
# Install Git
###############################################
apt-get -y install git

###############################################
# Install PostgreSQL
###############################################
apt-get -y install postgresql postgresql-contrib postgresql-client-common postgresql-common

###############################################
# Install SBT
###############################################
echo "Download SBT..."
wget http://dl.bintray.com/sbt/debian/sbt-$sbtVersion.deb
dpkg -i sbt-$sbtVersion.deb
apt-get update
apt-get install sbt
rm sbt-$sbtVersion.deb

echo "SBT done."
# Use node as default JavaScript Engine
echo "export SBT_OPTS=\"\$SBT_OPTS -Dsbt.jse.engineType=Node\"" >> ~/.bashrc

###############################################
# Reset bash
###############################################
source ~/.bashrc

###############################################
# Show installation summary
###############################################
echo "=========================================="
echo "Provision VM summary"
echo "=========================================="
echo "Dependencies installed:"
echo " "
echo "jdk version:"
javac -version
echo " "
echo "PostgreSQL version"
psql --version
echo " "
echo "=========================================="
echo "Provision VM finished"
echo "=========================================="
