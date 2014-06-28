Upptalk JingleRTPEngine
=======================


Jinglertpengine XMPP component is a standalone Java application and requires JavaSE 7+ to run.
It acts as a bridge between [Jingle Nodes](http://xmpp.org/extensions/xep-0278.html) clients and [mediaproxy-ng](https://github.com/sipwise/mediaproxy-ng) for enabling RTP proxy with
in kernel packet forwarding among other features.

## Requirements

### Mediaproxy-ng

Clone mediaproxy-ng branch [2.3](https://github.com/sipwise/mediaproxy-ng/tree/2.3):

```
> git clone https://github.com/sipwise/mediaproxy-ng.git

```

Compile & install according to instructions [here](https://github.com/sipwise/mediaproxy-ng/tree/2.3#on-a-debian-system).

* For some Debian versions you may find some trouble compiling the kernel module of media-proxy-ng 
against the current linux kernel. For solving it you may install linux headers and create a symbolic to 
the linux sources. Example:

```
> sudo dpkg -i ngcp-mediaproxy-ng-kernel-dkms_2.9.9_all.deb
 ... compiling ERROR
  
> sudo apt-get install linux-headers-3.2.0-4-amd64
> sudo ln -s /usr/src/linux-headers-3.2.0-4-amd64  /lib/modules/3.2.0-4-amd64/build    

```

Modify mediaproxy-ng parameters  on ```/etc/default/ngcp-mediaproxy-ng-daemon``` setting the parameter 
```b2b-url``` for pointing to jinglertpengine XMLRPC callback address, and IP addresses:

```
RUN_MEDIAPROXY=yes
LISTEN_TCP=25060
LISTEN_UDP=12222
LISTEN_NG=2223
ADDRESS=178.33.162.38
# ADV_ADDRESS=...
# ADDRESS_IPV6=...
# ADV_ADDRESS_IPV6=...
TIMEOUT=60
SILENT_TIMEOUT=3600
PIDFILE=/var/run/ngcp-mediaproxy-ng-daemon.pid
FORK=yes
TOS=184
TABLE=0
NO_FALLBACK=yes
PORT_MIN=10000
PORT_MAX=60000
# REDIS=127.0.0.1:6379
# REDIS_DB=1
B2B_URL=http://127.0.0.1:8080/xmlrpc
# LOG_LEVEL=6
```

## Building/Installing

Clone/build jinglertpengine from git:

```
> git clone https://github.com/upptalk/jinglertpengine.git
> cd jinglertpengine
> mvn clean compile package assembly:single

```

Unzip the produced zip file into its destination folder

```
> cd <folder_xyz>
> unzip jinglertpengine.zip
```

Configure the instance parameters in the files ```conf/jinglertpengine-properties.xml```.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd">
<properties>

    <entry key="xmpp.server">localhost</entry>
    <entry key="xmpp.port">8889</entry>
    <entry key="jinglertpengine.subdomain">jinglenodes</entry>
    <entry key="jinglertpengine.domain">ym.test.ms</entry>
    <entry key="jinglertpengine.password">secret</entry>

    <entry key="mediaproxy.server1.host">localhost</entry>
    <entry key="mediaproxy.server1.port">2223</entry>

    <entry key="mediaproxy.server2.host">localhost</entry>
    <entry key="mediaproxy.server2.port">2224</entry>

    <entry key="mediaproxy.xmlrpc.port">8080</entry>

</properties>
```

```com.upptalk.jinglertpengine.ng.NgClient``` bean configuration on ```conf/jinglertpengine.xml``` should reflect
the number of mediaproxy-ng processes on the server:

```xml

    ...

    <bean id="ngClient" class="com.upptalk.jinglertpengine.ng.NgClient">
        <property name="servers" >
            <list>
                <bean class="java.net.InetSocketAddress">
                    <constructor-arg value="${mediaproxy.server1.host}"/>
                    <constructor-arg value="${mediaproxy.server1.port}"/>
                </bean>
                <bean class="java.net.InetSocketAddress">
                    <constructor-arg value="${mediaproxy.server2.host}"/>
                    <constructor-arg value="${mediaproxy.server2.port}"/>
                </bean>
            </list>
        </property>
    </bean>

    ...

```

### Files and directories

The folder structure and its resources:

```
<jinglertpengine-root-folder>
  |
  |-- jinglerptengine.jar  --> main executable file
  |-- <bin>
  |     |
  |     |-- jinglertpengine.sh --> start up/stop script
  |-- <conf>
  |     |
  |     |-- jinglertpengine.xml  --> configuration files
  |     |-- jinglertpengine-properties.xml
  |     |-- log4j.xml
  |-- <lib>
  |     |
  |     |-- apache-log4j-extras-1.2.17.jar  --> libraries
  |     |-- commons-io-1.3.2.jar
  |     |-- ...
  |-- <log>
  |     |
  |     |-- jinglertpengine.log    --> main log file
  |     |-- jinglertpengine-metrics.log  --> metrics
```
