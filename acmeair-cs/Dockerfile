FROM 192.168.0.190:5000/mongo:web

ADD Dockerfile /root/Dockerfile

ADD startMongo.sh /usr/startMongo.sh

RUN rm -rf /opt/ibm/wlp/usr/servers/defaultServer/server.xml
ADD server.xml /opt/ibm/wlp/usr/servers/defaultServer/server.xml

RUN installUtility install --acceptLicense defaultServer
RUN rm -rf /opt/ibm/wlp/usr/servers/defaultServer/workarea

ADD ./build/libs/*.war /opt/ibm/wlp/usr/servers/defaultServer/apps

EXPOSE 80

ENV AUTH_SERVICE=nginx1/acmeair
ENV MONGO_HOST=cs
ENV MONGO_DBNAME=acmeair_customerdb

ENTRYPOINT ["/bin/bash","/usr/startMongo.sh"]
