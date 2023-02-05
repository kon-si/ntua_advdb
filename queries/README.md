# Query Execution
Every query is package as an application within the [queries.scala](https://github.com/kon-si/ntua_atds/blob/master/queries/queries.scala) file. Our applications depend on the Spark API so we need an sbt configuration file, [build.sbt](https://github.com/kon-si/ntua_atds/blob/master/queries/build.sbt), which explains the applicatons dependencies. For sbt to work correctly, we need to layout queries.scala and build.sbt according to the typical directory structure. The directory structure should look like this:

```bash
$ find .
.
./build.sbt
./src
./src/main
./src/main/scala
./src/main/scala/queries.scala
```
Once that is in place, we can create a JAR package containing the application’s code using the command:
```bash 
$ sbt package 
```

To run our program we use the file [run_queries.sh](https://github.com/kon-si/ntua_atds/blob/master/queries/run_queries.sh). This bash script executes a loop with 20 iteration of all the queries from the JAR executable using the spark-submit script.
