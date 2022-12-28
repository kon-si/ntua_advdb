# install hdfs

**/etc/hosts**<br>
Προσθέτουμε στο /etc/hosts τις IPv4 που έχουν στο localnet ο master και ο slave (διαφορετικά δεν μπορεί να κάνει resolve το host name)
sudo vi /etc/hosts 
(192.168.0.1 	snf-33932, 192.168.0.2 	snf-33933)

## Δημιουργία SSH keys
**Master and Slave:**<br>
sudo apt-get install ssh

**Slave**<br>
mkdir ~/.ssh
chmod 700 ~/.ssh
touch ~/.ssh/authorized_keys
chmod 600 ~/.ssh/authorized_keys

**Master**<br>
ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa
cat .ssh/id_rsa.pub >> ~/.ssh/authorized_keys
scp .ssh/authorized_keys snf-33932:/home/user/.ssh/authorized_keys

## Εγκατάσταση JAVA
**Master and Slave:**<br>
sudo apt-get -y install openjdk-8-jdk-headless

## Εγκατάσταση Hadoop
**Master and Slave:**<br>
wget http://apache.cs.utah.edu/hadoop/common/hadoop-3.3.4/hadoop-3.3.4.tar.gz
tar -xzf hadoop-3.3.4.tar.gz 
mv hadoop-3.3.4 hadoop

### Hadoop Environmental Variables
**Master and Slave:**<br>
vi ~/.bashrc (τα προσθέτουμε στο τέλος του αρχείου)
export HADOOP_HOME=/home/ubuntu/hadoop
export PATH=$PATH:$HADOOP_HOME/bin
export PATH=$PATH:$HADOOP_HOME/sbin
export HADOOP_MAPRED_HOME=${HADOOP_HOME}
export HADOOP_COMMON_HOME=${HADOOP_HOME}
export HADOOP_HDFS_HOME=${HADOOP_HOME}
export YARN_HOME=${HADOOP_HOME}
source ~/.bashrc (επαναφόρτωση των μεταβλητών στο bash session)

### Hadoop Configuration
* **hadoop-env.sh**<br>
vi ~/hadoop/etc/hadoop/hadoop-env.sh
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64

* **core-site.xml**<br>
vi ~/hadoop/etc/hadoop/core-site.xml
	<configuration>
    <property>
        <name>fs.defaultFS</name>
        <value>hdfs://192.168.0.1:9000</value>
    </property>
</configuration>

* **hdfs-site.xml**<br>
vi ~/hadoop/etc/hadoop/hdfs-site.xml
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

* **yarn-site.xml**<br>
vi ~/hadoop/etc/hadoop/yarn-site.xml
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

* **mapred-site.xml**<br>
vi ~/hadoop/etc/hadoop/mapred-site.xml
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

### Create Data Folder
sudo mkdir -p /usr/local/hadoop/hdfs/data
sudo chown user:user -R /usr/local/hadoop/hdfs/data
chmod 700 /usr/local/hadoop/hdfs/data

### Αρχεία Master και Workers
**Master and Slave:**<br>
vi ~/hadoop/etc/hadoop/masters<br>
192.168.0.1<br>
vi ~/hadoop/etc/hadoop/workers<br>
192.168.0.1<br>
192.168.0.2<br>

## Format και εκκίνηση HDFS
**Master**<br>
hdfs namenode -format<br>
start-dfs.sh
