FROM java:8
ENV APPNAME=alert-monitoring
ENV JAVA_HOME=/smartpay/java
ENV JMXTRANS_HOME=/smartpay/jmxtrans
ENV HOME=/smartpay/apps/prod.alert-monitoring
ENV PORT=5692
ENV HOST=localhost
ENV MIN_THREADS=20
ENV MAX_THREADS=200
ENV JMXHOST=localhost
ENV JMXPORT=15692
ENV APPENV=prod
ENV XMX=1G
ENV CONF=conf
ENV JARFILE=smartpost-app-service.jar
ENV LOGBACK=conf/logback.xml
ENV pid_file=tmp/service.pid
ENV log_file=tmp/service.log

ENV JVM_OPTS=" \
-Dapplication.name=$APPNAME \
-Dforeground=true \
-Xmx$XMX \
-XX:SurvivorRatio=8 \
-XX:+UseParNewGC \
-XX:+UseConcMarkSweepGC \
-XX:+CMSParallelRemarkEnabled \
-XX:MaxTenuringThreshold=1 \
-XX:CMSInitiatingOccupancyFraction=75 \
-XX:+UseCMSInitiatingOccupancyOnly \
-XX:+HeapDumpOnOutOfMemoryError \
-Djava.rmi.server.hostname=$JMXHOST \
-Dcom.sun.management.jmxremote.port=$JMXPORT \
-Dcom.sun.management.jmxremote.ssl=false \
-Dcom.sun.management.jmxremote.authenticate=false \
-Dconfig=$CONF \
-Dapppath=$HOME \
-Dappenv=$APPENV \
-Dspring.profiles.active=$APPENV \
-Dserver.port=$PORT \
-DminThread=$MIN_THREADS \
-DmaxThread=$MAX_THREADS \
-Dlog.file=log/$APPNAME \
-Dlogback.configurationFile=$LOGBACK \
-DenableSSL=true \
-javaagent:$JMXTRANS_HOME/jmxtrans-agent-1.2.6.jar=$JMXTRANS_HOME/jmxtrans-agent.xml "
ENV TZ=Asia/Ho_Chi_Minh
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
EXPOSE $PORT
RUN useradd -ms /bin/bash sdeploy
WORKDIR /home/sdeploy
WORKDIR /$HOME/logs
WORKDIR $JMXTRANS_HOME/smartpay
ADD jmxtrans-agent-1.2.6.jar $JMXTRANS_HOME
ADD jmxtrans-agent.xml $JMXTRANS_HOME
WORKDIR $HOME
ADD . $HOME
RUN chown -R sdeploy:sdeploy $HOME;chown -R sdeploy:sdeploy $JMXTRANS_HOME; chown -R sdeploy:sdeploy $HOME/logs
RUN chmod 755 $HOME; chmod 755 $JMXTRANS_HOME; chmod 755 $HOME/logs
USER sdeploy
CMD java $JVM_OPTS -jar $JARFILE > $log_file