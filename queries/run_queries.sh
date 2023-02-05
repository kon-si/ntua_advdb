#!/bin/bash

for i in {0..20}
do
  spark-submit --deploy-mode client --num-executors 1 --executor-cores 4 --class query1 target/scala-2.12/atds_queries_2.12-1.0.jar
  spark-submit --deploy-mode client --num-executors 1 --executor-cores 4 --class query2 target/scala-2.12/atds_queries_2.12-1.0.jar
  spark-submit --deploy-mode client --num-executors 1 --executor-cores 4 --class query3_df target/scala-2.12/atds_queries_2.12-1.0.jar
  spark-submit --deploy-mode client --num-executors 1 --class query3_rdd target/scala-2.12/atds_queries_2.12-1.0.jar
  spark-submit --deploy-mode client --num-executors 1 --executor-cores 4 --class query4 target/scala-2.12/atds_queries_2.12-1.0.jar
  spark-submit --deploy-mode client --num-executors 1 --executor-cores 4 --class query5 target/scala-2.12/atds_queries_2.12-1.0.jar
done

for i in {0..20}
do
   spark-submit --deploy-mode client --num-executors 2 --class query1 target/scala-2.12/atds_queries_2.12-1.0.jar
   spark-submit --deploy-mode client --num-executors 2 --class query2 target/scala-2.12/atds_queries_2.12-1.0.jar
   spark-submit --deploy-mode client --num-executors 2 --class query3_df target/scala-2.12/atds_queries_2.12-1.0.jar
   spark-submit --deploy-mode client --num-executors 2 --class query3_rdd target/scala-2.12/atds_queries_2.12-1.0.jar
   spark-submit --deploy-mode client --num-executors 2 --class query4 target/scala-2.12/atds_queries_2.12-1.0.jar
   spark-submit --deploy-mode client --num-executors 2 --class query5 target/scala-2.12/atds_queries_2.12-1.0.jar
done

for i in {0..20}
do
   spark-submit --deploy-mode client --num-executors 4 --class query1 target/scala-2.12/atds_queries_2.12-1.0.jar
   spark-submit --deploy-mode client --num-executors 4 --class query2 target/scala-2.12/atds_queries_2.12-1.0.jar
   spark-submit --deploy-mode client --num-executors 4 --class query3_df target/scala-2.12/atds_queries_2.12-1.0.jar
   spark-submit --deploy-mode client --num-executors 4 --class query3_rdd target/scala-2.12/atds_queries_2.12-1.0.jar
   spark-submit --deploy-mode client --num-executors 4 --class query4 target/scala-2.12/atds_queries_2.12-1.0.jar
   spark-submit --deploy-mode client --num-executors 4 --class query5 target/scala-2.12/atds_queries_2.12-1.0.jar
done

for i in {0..20}
do
   spark-submit --deploy-mode client --num-executors 8 --class query1 target/scala-2.12/atds_queries_2.12-1.0.jar
   spark-submit --deploy-mode client --num-executors 8 --class query2 target/scala-2.12/atds_queries_2.12-1.0.jar
   spark-submit --deploy-mode client --num-executors 8 --class query3_df target/scala-2.12/atds_queries_2.12-1.0.jar
   spark-submit --deploy-mode client --num-executors 8 --class query3_rdd target/scala-2.12/atds_queries_2.12-1.0.jar
   spark-submit --deploy-mode client --num-executors 8 --class query4 target/scala-2.12/atds_queries_2.12-1.0.jar
   spark-submit --deploy-mode client --num-executors 8 --class query5 target/scala-2.12/atds_queries_2.12-1.0.jar
done

