#!/bin/bash

echo "******ENABLING BOTTLED WATER EXTENSION******"

echo "starting postgres"
gosu postgres pg_ctl -w start

echo "setting up bottled water"
gosu postgres psql -h localhost -p 5432 -U postgres -a -f /docker-entrypoint-initdb.d/bottled-water-setup.sql

echo "stopping postgres"
gosu postgres pg_ctl stop
echo "stopped postgres"

echo ""
echo "******BOTTLED WATER EXTENSION ENABLED******"
