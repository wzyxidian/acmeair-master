FROM 192.168.0.190:5000/web:lib

#RUN mkdir /mongodb
#RUN mkdir /mongodb/data
#RUN mkdir /mongodb/log
#RUN touch /mongodb/log/mongodb.log

ADD Dockerfile /root/Dockerfile

ADD startMongo.sh /usr/startMongo.sh

RUN rm -rf /opt/ibm/wlp/usr/servers/defaultServer/server.xml
ADD server.xml /opt/ibm/wlp/usr/servers/defaultServer/server.xml

#RUN installUtility install --acceptLicense defaultServer
RUN rm -rf /opt/ibm/wlp/usr/servers/defaultServer/workarea

ADD ./build/libs/*.war /opt/ibm/wlp/usr/servers/defaultServer/apps

EXPOSE 80

ENV AUTH_SERVICE=as/acmeair-as
ENV MONGO_HOST=booking_db1
ENV MONGO_DBNAME=acmeair_bookingdb

CMD ["/opt/ibm/wlp/bin/server", "run", "defaultServer"]

