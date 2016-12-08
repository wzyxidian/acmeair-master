#!/bin/bash
cd /root/mongodb-3.0.7/bin
./mongod -f /docker/mongodb/mongodb.conf &
sleep 5s
MongoDB='./mongo 127.0.0.1:30000'
$MongoDB << EOF
use acmeair_customerdb
exit;
EOF