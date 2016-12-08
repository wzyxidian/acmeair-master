FROM websphere-liberty:common

ADD Dockerfile /root/Dockerfile

ADD startMongo.sh /usr/startMongo.sh

RUN rm -rf /opt/ibm/wlp/usr/servers/defaultServer/server.xml
ADD server.xml /opt/ibm/wlp/usr/servers/defaultServer/server.xml

RUN installUtility install --acceptLicense defaultServer
RUN rm -rf /opt/ibm/wlp/usr/servers/defaultServer/workarea

ADD ./build/libs/*.war /opt/ibm/wlp/usr/servers/defaultServer/apps

EXPOSE 80

ENV AUTH_SERVICE=nginx1/acmeair
ENV MONGO_HOST=bs
ENV MONGO_DBNAME=acmeair_bookingdb

CMD ["/opt/ibm/wlp/bin/server", "run", "defaultServer"]
ENTRYPOINT["/bin/bash","/usr/startMongo.sh"]

