# Yarn Configuration 

[source link 1](https://sparkbyexamples.com/hadoop/yarn-setup-and-run-map-reduce-program/) <br>
[source link 2](https://docs.cloudera.com/HDPDocuments/HDP2/HDP-2.0.9.0/bk_installing_manually_book/content/rpm-chap1-11.html)

Το Yarn εγκαθίσταται στο σύστημα by default με την εγκατάσταση του Apache Hadoop, επομένως δεν χρειάζεται πρόσθετη εγκατάσταση, χρειάζεται όμως η ρύθμιση μερικών παραμέτρων σχετικές με τη χρήση του yarn και ορισμένες ρυθμίσεις σχετικές με τη διαχείριση μνήμης.
Τα configurations που ακολουθούν, λοιπόν, ρυθμίζονται αφού έχει εγκατασταθεί με επιτυχία το Apache Hadoop και πρέπει να συμπληρωθούν κατάλληλα σε Master και Slave.

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

