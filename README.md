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

Compile & install according to instructions [here](https://github.com/sipwise/mediaproxy-ng/tree/2.3#manual-compilation).

Create a startup script on Linux setting the parameter ```b2b-url``` for pointing to jinglertpengine killed sessions XML RPC
callback address:
```
# this only needs to be one once after system (re-) boot
modprobe xt_MEDIAPROXY
iptables -I INPUT -p udp -j MEDIAPROXY --id 0
ip6tables -I INPUT -p udp -j MEDIAPROXY --id 0

# ensure that the table we want to use doesn't exist - usually needed after a daemon
# restart, otherwise will error
echo 'del 0' > /proc/mediaproxy/control

# start daemon
/usr/bin/mediaproxy-ng --table=0 --ip=127.0.0.1  \
--listen-ng=127.0.0.1:2223 --tos=184 --pidfile=/var/run/mediaproxyng.pid --no-fallback --b2b-url=http://localhost:8080/xmlrpc
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
