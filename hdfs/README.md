# HDFS Installation
[source link](https://sparkbyexamples.com/hadoop/apache-hadoop-installation/)

**Add hostnames to /etc/hosts**<br>
In /etc/hosts file add the VM hostnames with their corresponding local network IPv4 addresses to ensure that name resolution is fast and consistent.

```bash
sudo vi /etc/hosts 
```
```bash
192.168.0.1 	snf-34594 
192.168.0.2 	snf-33933
```

Note: If present, the entry for the loopback address 127.0.1.1 must be deleted from the /etc/hosts file, otherwise communication between the datanodes and the namenode is blocked.

## SSH key creation
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
**Master**<br>
```bash
ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa
cat .ssh/id_rsa.pub >> ~/.ssh/authorized_keys
scp .ssh/authorized_keys snf-34594:/home/user/.ssh/authorized_keys
```
## Εγκατάσταση JAVA
**Master and Slave:**<br>
```bash
sudo apt-get -y install openjdk-8-jdk-headless
```
## Εγκατάσταση Hadoop
**Master and Slave:**<br>
```bash
wget http://apache.cs.utah.edu/hadoop/common/hadoop-3.3.4/hadoop-3.3.4.tar.gz
tar -xzf hadoop-3.3.4.tar.gz 
mv hadoop-3.3.4 hadoop
```
### Hadoop Environmental Variables
**Master and Slave:**<br>
```bash
vi ~/.bashrc 
```
(τα προσθέτουμε στο τέλος του αρχείου)<br>
```bash
export HADOOP_HOME=/home/user/hadoop
export PATH=$PATH:$HADOOP_HOME/bin
export PATH=$PATH:$HADOOP_HOME/sbin
export HADOOP_MAPRED_HOME=${HADOOP_HOME}
export HADOOP_COMMON_HOME=${HADOOP_HOME}
export HADOOP_HDFS_HOME=${HADOOP_HOME}
export YARN_HOME=${HADOOP_HOME}
```
επαναφόρτωση των μεταβλητών στο bash session:
```bash
source ~/.bashrc 
```
### Hadoop Configuration
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
### Create Data Folder
```bash
sudo mkdir -p /usr/local/hadoop/hdfs/data
sudo chown user:user -R /usr/local/hadoop/hdfs/data
chmod 700 /usr/local/hadoop/hdfs/data
```
### Αρχεία Master και Workers
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
## Format και εκκίνηση HDFS
**Master**<br>
```bash
hdfs namenode -format
start-dfs.sh
```
# Yarn Configuration 

[source link 1](https://sparkbyexamples.com/hadoop/yarn-setup-and-run-map-reduce-program/) <br>
[source link 2](https://docs.cloudera.com/HDPDocuments/HDP2/HDP-2.0.9.0/bk_installing_manually_book/content/rpm-chap1-11.html)

Yarn is installed on the system by default with the Apache Hadoop installation, so no additional installation is necessary, but some parameters related to the use of yarn and some settings related to memory management need to be configured. The configurations should be set both for Master and Slave machines.

## Configure yarn-site.xml
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

## Configure mapred-site.xml
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
