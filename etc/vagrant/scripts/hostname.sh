#!/bin/bash

#-------------------------------------------------------------------------------
# SET VAGRANT HOST NAME
#-------------------------------------------------------------------------------
#
# This file is used to create and set the vagrant host name
#
file="etc/vagrant/config/hostname"
if [ ! -f $file ]; then
    read -p "Enter hostname: " host
    echo -n $host > $file
    echo -n $host
else
    host=$(head -n 1 $file)
    echo -n $host
fi