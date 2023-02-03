# SBT Installation

[source link](https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Linux.html)


Το SBT είναι open-source εργαλείο δημιουργίας εφαρμογών για Scala και Java. Το
χρησιμοποιούμε ώστε να μετατρέψουμε τον κώδικα Scala σε εκτελέσιμα αρχεία JAR. Για να το
εγκαταστήσουμε τρέχουμε τις εξής εντολές:

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
```
