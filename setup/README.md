# Set Up 

## Table of contents
* [HDFS Installation](#hdfs-installation)
* [Yarn Configuration](#yarn-configuration)
* [Spark Installation](#spark-installation)
* [Scala Installation](#scala-installation)
* [SBT Installation](#sbt-installation)

## HDFS Installation
[source link](https://sparkbyexamples.com/hadoop/apache-hadoop-installation/)

**Add hostnames to /etc/hosts**<br>
In /etc/hosts file add the VM hostnames with their corresponding local network IPv4 addresses to ensure that name resolution is fast and consistent.

```bash
sudo vi /etc/hosts 
```
```bash
192.168.0.1 	snf-34594 
192.168.0.2 	snf-34596
```

Note: If present, the entry for the loopback address 127.0.1.1 must be deleted from the /etc/hosts file, otherwise communication between the datanodes and the namenode is blocked.

### SSH key creation
In Apache Hadoop HDFS, Secure Shell (SSH) keys are used for passwordless authentication between the nodes in a cluster. SSH keys provide a secure and automated way to establish trust between nodes, which is important when setting up and configuring a HDFS cluster.

**Master and Slave:**<br>
```bash
sudo apt-get install ssh
```
**Slave**<br>
```bash
mkdir ~/.ssh
chmod 700 ~/.ssh
touch ~/.ssh/authorized_keys
chmod 600 ~/.ssh/authorized_keys
```
Generate an SSH Key: Generate a public/private key pair on the master node using the ssh-keygen command.<br>
Copy the Public Key to the Slave: Copy the public key to the slave node, so that the master node can securely log into the slave node without a password. 

**Master**<br>
```bash
ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa
cat .ssh/id_rsa.pub >> ~/.ssh/authorized_keys
scp .ssh/authorized_keys snf-34594:/home/user/.ssh/authorized_keys
```
### JAVA Installation
**Master and Slave:**<br>
```bash
sudo apt-get -y install openjdk-8-jdk-headless
```
### Hadoop Installation
**Master and Slave:**<br>
```bash
wget http://apache.cs.utah.edu/hadoop/common/hadoop-3.3.4/hadoop-3.3.4.tar.gz
tar -xzf hadoop-3.3.4.tar.gz 
mv hadoop-3.3.4 hadoop

sudo usermod -aG user user
sudo chown user:root -R /usr/local/hadoop/
sudo chmod g+rwx -R /usr/local/hadoop/
sudo adduser user sudo
```
#### Hadoop Environment Variables
**Master and Slave:**<br>
```bash
vi ~/.bashrc 
```
Add the following at the bottom of the file:<br>
```bash
export HADOOP_HOME=/home/user/hadoop
export PATH=$PATH:$HADOOP_HOME/bin
export PATH=$PATH:$HADOOP_HOME/sbin
export HADOOP_MAPRED_HOME=${HADOOP_HOME}
export HADOOP_COMMON_HOME=${HADOOP_HOME}
export HADOOP_HDFS_HOME=${HADOOP_HOME}
export YARN_HOME=${HADOOP_HOME}
```
Reload the environment variables in bash:
```bash
source ~/.bashrc 
```
#### Hadoop Configuration
* **hadoop-env.sh**<br>
```bash
vi ~/hadoop/etc/hadoop/hadoop-env.sh
```
```bash
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
```
* **core-site.xml**<br>
```bash
vi ~/hadoop/etc/hadoop/core-site.xml
```
```bash
<configuration>
    <property>
        <name>fs.defaultFS</name>
        <value>hdfs://192.168.0.1:9000</value>
    </property>
</configuration>
```
* **hdfs-site.xml**<br>
```bash
vi ~/hadoop/etc/hadoop/hdfs-site.xml
```
```bash
<configuration>
    <property>
        <name>dfs.replication</name>
        <value>1</value>
    </property>
    <property>
        <name>dfs.namenode.name.dir</name>
        <value>file:///usr/local/hadoop/hdfs/data</value>
    </property>
    <property>
        <name>dfs.datanode.data.dir</name>
        <value>file:///usr/local/hadoop/hdfs/data</value>
    </property>
</configuration>
```
* **yarn-site.xml**<br>
```bash
vi ~/hadoop/etc/hadoop/yarn-site.xml
```
```bash
<configuration>
    <property>
        <name>yarn.nodemanager.aux-services</name>
        <value>mapreduce_shuffle</value>
    </property>
    <property>
        <name>yarn.nodemanager.aux-services.mapreduce.shuffle.class</name>
        <value>org.apache.hadoop.mapred.ShuffleHandler</value>
    </property>
    <property>
       <name>yarn.resourcemanager.hostname</name>
       <value>192.168.0.1</value>
    </property>
</configuration>
```
* **mapred-site.xml**<br>
```bash
vi ~/hadoop/etc/hadoop/mapred-site.xml
```
```bash
<configuration>
    <property>
        <name>mapreduce.jobtracker.address</name>
        <value>192.168.0.1:54311</value>
    </property>
    <property>
        <name>mapreduce.framework.name</name>
        <value>yarn</value>
    </property>
</configuration>
```
#### Create Data Folder
```bash
sudo mkdir -p /usr/local/hadoop/hdfs/data
sudo chown user:user -R /usr/local/hadoop/hdfs/data
chmod 700 /usr/local/hadoop/hdfs/data
```
#### Master και Workers Files
**Master and Slave:**<br>
```bash
vi ~/hadoop/etc/hadoop/masters
```
```bash
192.168.0.1
```
```bash
vi ~/hadoop/etc/hadoop/workers
```
```bash
192.168.0.1
192.168.0.2
```
### Format και start HDFS
**Master**<br>
```bash
hdfs namenode -format
start-dfs.sh
```

Note: In case there is a need to reformat HDFS, first execute the command "stop-dfs.sh", then delete the folders "usr/local/hadoop/hdfs/data/nameNode" and "usr/local/hadoop/hdfs/data/dataNode".

## Yarn Configuration 

[source link 1](https://sparkbyexamples.com/hadoop/yarn-setup-and-run-map-reduce-program/) <br>
[source link 2](https://docs.cloudera.com/HDPDocuments/HDP2/HDP-2.0.9.0/bk_installing_manually_book/content/rpm-chap1-11.html)

Yarn is installed on the system by default with the Apache Hadoop installation, so no additional installation is necessary, but some parameters related to the use of yarn and some settings related to memory management need to be configured. The configurations should be set both for Master and Slave machines.

### Configure yarn-site.xml
```bash
vi ~/hadoop/etc/hadoop/yarn-site.xml
```
```bash
<configuration>
    <property>
        <name>yarn.nodemanager.aux-services</name>
        <value>mapreduce_shuffle</value>
    </property>
    <property>
        <name>yarn.nodemanager.aux-services.mapreduce.shuffle.class</name>
        <value>org.apache.hadoop.mapred.ShuffleHandler</value>
    </property>
    <property>
       <name>yarn.resourcemanager.hostname</name>
       <value>192.168.0.1</value>
    </property>
    <property>
        <name>yarn.nodemanager.resource.memory-mb</name>
        <value>1536</value>
    </property>
    <property>
        <name>yarn.scheduler.maximum-allocation-mb</name>
        <value>1536</value>
    </property>
    <property>
        <name>yarn.scheduler.minimum-allocation-mb</name>
        <value>128</value>
    </property>
    <property>
        <name>yarn.nodemanager.vmem-check-enabled</name>
        <value>false</value>
    </property>
</configuration>
```

### Configure mapred-site.xml
```bash
vi ~/hadoop/etc/hadoop/mapred-site.xml
```
```bash
<configuration>
    <property>
        <name>mapreduce.jobtracker.address</name>
        <value>192.168.0.1:54311</value>
    </property>
    <property>
        <name>mapreduce.framework.name</name>
        <value>yarn</value>
    </property>
    <property>
        <name>yarn.app.mapreduce.am.resource.mb</name>
        <value>512</value>
    </property>
    <property>
        <name>mapreduce.map.memory.mb</name>
        <value>256</value>
    </property>
    <property>
        <name>mapreduce.reduce.memory.mb</name>
        <value>256</value>
    </property>
    <property>
        <name>yarn.app.mapreduce.am.env</name>
        <value>HADOOP_MAPRED_HOME=$HADOOP_MAPRED_HOME</value>
    </property>
    <property>
        <name>mapreduce.map.env</name>
        <value>HADOOP_MAPRED_HOME=$HADOOP_MAPRED_HOME</value>
    </property>
    <property>
        <name>mapreduce.reduce.env</name>
        <value>HADOOP_MAPRED_HOME=$HADOOP_MAPRED_HOME</value>
    </property>
</configuration>
```

## Spark Installation
[source link](https://sparkbyexamples.com/spark/spark-setup-on-hadoop-yarn/)

Spark will be installed on Hadoop Cluster so the following steps should be done after Apache Hadoop has been successfully installed and the necessary Yarn settings have been made.

```bash
wget https://archive.apache.org/dist/spark/spark-3.3.2/spark-3.3.2-bin-hadoop3.2.tgz
tar -xzf spark-3.3.2-bin-hadoop3.2.tgz
mv spark-3.3.2-bin-hadoop3.2 spark
```

### Environment Variables
```bash
vi ~/.bashrc 
```
```bash
export HADOOP_CONF_DIR=$HADOOP_HOME/etc/hadoop
export SPARK_HOME=/home/ubuntu/spark
export PATH=$PATH:$SPARK_HOME/bin
export LD_LIBRARY_PATH=$HADOOP_HOME/lib/native:$LD_LIBRARY_PATH
```

```bash
source ~/.bashrc 
```

### spark-defaults.conf 
```bash
vi ~/spark/conf/spark-defaults.conf 
```
Set spark.master to Yarn and configure History Server:
```bash
spark.master yarn
spark.driver.memory 512m
spark.yarn.am.memory 512m
spark.executor.memory 512m

spark.eventLog.enabled true
spark.eventLog.dir hdfs://192.168.0.1:9000/user/user/spark-logs
spark.history.fs.logDirectory hdfs://192.168.0.1:9000/user/user/spark-logs
```

Start History Server:
```bash
~/spark/sbin/start-history-server.sh
```
## Scala Installation
Spark version 3.3.1 requires Scala version 2.12/2.13. We install the latest version of Scala, which at the time of writing is version 2.13.10:
```bash
wget www.scala-lang.org/files/archive/scala-2.13.10.deb sudo dpkg -i scala-2.13.10.deb 
sudo apt-get update 
sudo apt-get install scala 
```
To check if the installation was successful run command: "scala -version".
The result should be:
```bash
Scala code runner version 2.13.10 -- Copyright 2002-2022
```

## SBT Installation

[source link](https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Linux.html)

SBT is an open-source application builder for Scala and Java. We use it to turn Scala code into executable JAR files. For the
install we run the following commands:

```bash
echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" \
| sudo tee /etc/apt/sources.list.d/sbt.list
echo "deb https://repo.scala-sbt.org/scalasbt/debian /" \
| sudo tee /etc/apt/sources.list.d/sbt_old.list
curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&\
search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" \
| sudo -H gpg --no-default-keyring --keyring \
gnupg-ring:/etc/apt/trusted.gpg.d/scalasbt-release.gpg --import
sudo chmod 644 /etc/apt/trusted.gpg.d/scalasbt-release.gpg
sudo apt-get update
sudo apt-get install sbt
