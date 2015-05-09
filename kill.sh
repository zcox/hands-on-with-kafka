#!/bin/bash

doc kill
doc rm --force
boot2docker ssh sudo rm -rf /mnt/sda1/kafka
echo "deleted kafka logs dir /mnt/sda1/kafka"
