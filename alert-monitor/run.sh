#!/bin/bash
# Startup script for Main
export APPNAME=AlertMonitor
export JAVA_HOME=/smartpay/java
export JMXTRANS_HOME=/smartpay/jmxtrans
export HOME=/smartpay/alert/
cd $HOME
export HOST=localhost
export PORT=2104
export JMXHOST=localhost
export JMXPORT=12104
export MIN_THREADS=32
export MAX_THREADS=256
export APPENV=prod
export XMX=1G
export CONF=conf
export JARFILE=alert-monitor-0.0.1-SNAPSHOT.jar
pid_file=tmp/service.pid
log_file=tmp/service.log

# Arguments to pass to the JVM
JVM_OPTS=" \
-server \
-Dapplication.name=$APPNAME \
-Dforeground=true \
-Xmx$XMX \
-XX:NewRatio=1 \
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
-javaagent:$JMXTRANS_HOME/jmxtrans-agent-1.2.6.jar=$JMXTRANS_HOME/jmxtrans-agent.xml "
if [ -x $JAVA_HOME/bin/java ]; then
    JAVA=$JAVA_HOME/bin/java
else
    JAVA=`which java`
fi

case "$1" in
    start)
        # Main startup
        echo -n "Starting $APPNAME (port $PORT): "
        if [ -f $pid_file ]
        then
            echo "Already running"
            exit 1
        fi
        nohup $JAVA $JVM_OPTS -jar $JARFILE > $log_file 2>&1 &
        echo $! >  $pid_file
        echo "OK, Process started"
        ;;
    stop)
        # Main shutdown
        if [ -f $pid_file ];
        then
            pid=`cat $pid_file`
            kill -15 $pid
            sleep 5
            ps -p $pid > /dev/null
            if [ $? -eq 0 ];
            then
                kill -9 $pid
            fi
            rm -rif $pid_file
        else
            echo "Process wasn't running"
        fi
        ;;
    reload|restart)
        $0 stop
        sleep 1
        $0 start
        ;;
    status)
        ;;
    *)
        echo "Usage: `basename $0` start|stop|restart|reload"
        exit 1
esac

exit 0