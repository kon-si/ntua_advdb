# install hdfs

*/etc/hosts*
Προσθέτουμε στο /etc/hosts τις IPv4 που έχουν στο localnet ο master και ο slave (διαφορετικά δεν μπορεί να κάνει resolve το host name)
sudo vi /etc/hosts 
(192.168.0.1 	snf-33932, 192.168.0.2 	snf-33933)

## *Δημιουργία SSH keys*
*Master and Slave:*
sudo apt-get install ssh

*Slave*
mkdir ~/.ssh
chmod 700 ~/.ssh
touch ~/.ssh/authorized_keys
chmod 600 ~/.ssh/authorized_keys

*Master*
ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa
cat .ssh/id_rsa.pub >> ~/.ssh/authorized_keys
scp .ssh/authorized_keys snf-33932:/home/user/.ssh/authorized_keys

## *Εγκατάσταση JAVA*
*Master and Slave:*
sudo apt-get -y install openjdk-8-jdk-headless

## *Εγκατάσταση Hadoop*
*Master and Slave:*
wget http://apache.cs.utah.edu/hadoop/common/hadoop-3.3.4/hadoop-3.3.4.tar.gz
tar -xzf hadoop-3.3.4.tar.gz 
mv hadoop-3.3.4 hadoop

### *Hadoop Environmental Variables*
