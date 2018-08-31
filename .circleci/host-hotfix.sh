#!/usr/bin/env bash

echo "127.0.0.1 fakelog.localhost" | sudo tee -a /etc/hosts
echo "127.0.0.1 adfs.fakelog.localhost" | sudo tee -a /etc/hosts
echo "127.0.0.1 cufs.fakelog.localhost" | sudo tee -a /etc/hosts
echo "127.0.0.1 uonetplus.fakelog.localhost" | sudo tee -a /etc/hosts
echo "127.0.0.1 uonetplus-opiekun.fakelog.localhost" | sudo tee -a /etc/hosts
