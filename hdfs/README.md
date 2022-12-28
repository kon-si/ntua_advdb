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
* **hadoop-env.sh**
vi ~/hadoop/etc/hadoop/hadoop-env.sh
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64

* **core-site.xml**
vi ~/hadoop/etc/hadoop/core-site.xml
	<configuration>
    <property>
        <name>fs.defaultFS</name>
        <value>hdfs://192.168.0.1:9000</value>
    </property>
</configuration>

* 
