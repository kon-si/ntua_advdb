# Spark Installation
[source link](https://sparkbyexamples.com/spark/spark-setup-on-hadoop-yarn/)

Η εγκατάσταση του Spark θα γίνει πάνω στο Hadoop Cluster γι' αυτό και τα παρακάτω βήματα θα πρέπει να γίνουν αφότου έχει εγκατασταθεί με επιτυχία το Apache Hadoop και έχουν γίνει οι απαραίτητες ρυθμίσεις για το Yarn.

```bash
wget https://archive.apache.org/dist/spark/spark-3.3.2/spark-3.3.2-bin-hadoop3.2.tgz
tar -xzf spark-3.3.2-bin-hadoop3.2.tgz
mv spark-3.3.2-bin-hadoop3.2 spark
```

## Ρύθμιση Μεταβλητών Περιβάλλοντος
```bash
vi ~/.bashrc 
```
```bash
export HADOOP_CONF_DIR=$HADOOP_HOME/etc/hadoop
export SPARK_HOME=/home/ubuntu/spark
export PATH=$PATH:$SPARK_HOME/bin
export LD_LIBRARY_PATH=$HADOOP_HOME/lib/native:$LD_LIBRARY_PATH
```

επαναφόρτωση των μεταβλητών στο bash session:
```bash
source ~/.bashrc 
```

## spark-defaults.conf 
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
