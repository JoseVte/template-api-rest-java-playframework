#!/bin/bash

# ==========================
# Get setup config data
# ==========================

read -p "Server hostname: " HOSTNAME
read -p "New username: " USER_USERNAME
read -sp "Users password: " USER_PASSWORD; echo
read -p "Private IP: " PRIVATE_IP
read -p "URL to Users SSH keys: " USER_SSHKEY

# ==========================
# Enable firewall
# ==========================

ufw allow ssh # Enable SSH
ufw allow www # Allow www
ufw --force enable

# =======================================================
# Turn off password authentication and root login for SSH
# =======================================================

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

# =============
# Update system
# =============

# get system up to date
apt-get update && apt-get upgrade -y
