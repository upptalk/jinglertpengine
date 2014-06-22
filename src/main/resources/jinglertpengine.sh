#!/bin/bash
#JINGLERTPENGINE_HOME=$HOME/jinglertpengine
JINGLERTPENGINE_HOME="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && cd .. && pwd )"

case "$1" in
start)
        if [ -e $JINGLERTPENGINE_HOME/jinglertpengine.pid ]; then
                echo "jinglertpengine already running.";
        else
                echo "Starting jinglertpengine..."
                java -Djinglertpengine.home=${JINGLERTPENGINE_HOME} -server -XX:+UseThreadPriorities -XX:ThreadPriorityPolicy=42 -Xms512M -Xmx512M -Xmn256M -XX:PermSize=64m -XX:MaxPermSize=128m -XX:+HeapDumpOnOutOfMemoryError -Xss512k -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:SurvivorRatio=8 -XX:MaxTenuringThreshold=1 -XX:CMSInitiatingOccupancyFraction=75 -XX:+UseCMSInitiatingOccupancyOnly -Djava.net.preferIPv4Stack=true -Dcom.sun.management.jmxremote.port=7399 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Dlog4j.configuration=file:${JINGLERTPENGINE_HOME}/conf/log4j.xml -jar ${JINGLERTPENGINE_HOME}/jinglertpengine.jar start &
                pid=$!
                echo ${pid} > $JINGLERTPENGINE_HOME/jinglertpengine.pid
        fi

;;
stop)

        if [ -e $JINGLERTPENGINE_HOME/jinglertpengine.pid ]; then
                echo "Killing jinglertpengine...";
                kill `cat $JINGLERTPENGINE_HOME/jinglertpengine.pid`;
                rm -f $JINGLERTPENGINE_HOME/jinglertpengine.pid;
        else
                echo "jinglertpengine is not running.";
        fi

;;
restart)
    $0 stop
    $0 start
;;
status)
        if [ -e $JINGLERTPENGINE_HOME/jinglertpengine.pid ]; then
                echo "jinglertpengine running. PID " `cat $JINGLERTPENGINE_HOME/jinglertpengine.pid`;
        else
                echo "jinglertpengine is not running.";
        fi
;;
*)

echo 'Usage: (start|stop|restart|status)'
                echo ' '                
                echo 'Options:'
                echo '  start           - Starts Jingle RTP Engine using the configuration files'
                echo '  stop            - Stops Jingle RTP Engine'
                echo '  restart         - Restart Jingle RTP Engine'
                echo '  status          - Shows the status of Jingle RTP Engine'
esac