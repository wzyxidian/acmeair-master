#!/bin/bash
cd /software/mongodb-3.0.7/bin
./mongod -f /docker/mongodb/mongodb.conf &
sleep 20s
MongoDB='./mongo 127.0.0.1:27017'
$MongoDB << EOF
use acmeair_flightdb
exit;
EOF
/opt/ibm/wlp/bin/server run defaultServer