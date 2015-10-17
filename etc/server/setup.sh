#!/bin/bash


# ==========================
# Secure system & setup user
# ==========================

read -p "Server hostname: " HOSTNAME
read -p "New username: " USER_USERNAME
read -sp "Users password: " USER_PASSWORD; echo
read -p "Private IP: " PRIVATE_IP
read -p "URL to Users SSH keys: " USER_SSHKEY


# Enable firewall
ufw allow ssh # Enable SSH
ufw allow www # Allow www
ufw --force enable

# Turn off password authentication and root login for SSH
echo 'PasswordAuthentication no' >> /etc/ssh/sshd_config
sed -i 's/PermitRootLogin yes/PermitRootLogin no/g' /etc/ssh/sshd_config
service ssh restart

# Set up sudo user
if [ -n "$USER_USERNAME" ] && [ -n "$USER_PASSWORD" ]; then
    useradd --create-home --shell /bin/bash --user-group --groups sudo $USER_USERNAME
    echo "$USER_USERNAME:$USER_PASSWORD" | chpasswd

  if [ -n "$USER_SSHKEY" ]; then
    sudo -u "$USER_USERNAME" mkdir -p "/home/$USER_USERNAME/.ssh"
    sudo -u "$USER_USERNAME" touch "/home/$USER_USERNAME/.ssh/authorized_keys"
    sudo -u "$USER_USERNAME" curl -s "$USER_SSHKEY" > "/home/$USER_USERNAME/.ssh/authorized_keys"
    chmod 0600 "/home/$USER_USERNAME/.ssh/authorized_keys"
  fi
fi

# remove requirement for password to sudo
echo -e "\n$USER_USERNAME ALL=(ALL) NOPASSWD: ALL" >> /etc/sudoers

# =============
# set locales
# =============
locale-gen es_ES es_ES.UTF-8
dpkg-reconfigure locales

# =============
# set timezone
# =============
echo "Europe/Madrid" | tee /etc/timezone
dpkg-reconfigure --frontend noninteractive tzdata


# =============
# set hostname
# =============

# This sets the variable $IPADDR to the IP address the new Linode receives.
IPADDR=$(/sbin/ifconfig eth0 | awk '/inet / { print $2 }' | sed 's/addr://')

# Set the hostname
echo $HOSTNAME > /etc/hostname
hostname -F /etc/hostname
echo $IPADDR $HOSTNAME >> /etc/hosts

printf "\n\niface eth0 inet static\naddress $PRIVATE_IP\n" | tee -a /etc/network/interfaces
ifdown -a && ifup -a

#variables
activatorVersion="1.3.5"
sbtVersion="0.13.9"

echo "=========================================="
echo "Provision VM START"
echo "=========================================="
 
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
