#!/bin/bash
cd /software/mongodb-3.0.7/bin
./mongod -f /docker/mongodb/mongodb.conf &
MongoDB='./mongo 127.0.0.1:27017'
$MongoDB << EOF
use acmeair_bookingdb
exit;
EOF
while [ $? != 0 ]
do
   MongoDB='./mongo 127.0.0.1:27017'
   $MongoDB << EOF
   use acmeair_bookingdb
   exit;
EOF
done
/opt/ibm/wlp/bin/server run defaultServer