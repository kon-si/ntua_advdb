import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.Window
import org.apache.spark.rdd.PairRDDFunctions

import java.text.SimpleDateFormat
import java.io.PrintWriter

object dataCleaning {
  def main(args: Array[String]) {
    val spark = SparkSession.builder.appName("dataCleaning").getOrCreate()

    val recordsDF = spark.read.parquet("orig_records")
      .filter(month(col("tpep_dropoff_datetime")) < 7 && year(col("tpep_pickup_datetime")) === 2022)
      .write.parquet("hdfs://192.168.0.1:9000/user/user/clean_records/records.parquet") 

    spark.stop()
  }
}

object query1 {
  def main(args: Array[String]) {
    val spark = SparkSession.builder.appName("query1").getOrCreate()
    val conf = new Configuration()
    val fs = FileSystem.get(conf)
    val output = fs.append(new Path("hdfs://192.168.0.1:9000/user/user/queries/query1.txt"))

    val sqlContext = spark.sqlContext
    val lookupSchema = StructType(Array(
      StructField("LocationID", IntegerType, true),
      StructField("Borough", StringType, true),
      StructField("Zone", StringType, true),
      StructField("service_zone", StringType, true))
    )

    val lookupDF = sqlContext.read.schema(lookupSchema).options(Map("header"->"true")).csv("hdfs://192.168.0.1:9000/user/user/lookup/taxi+_zone_lookup.csv")
      .filter(col("Zone").equalTo("Battery Park")).drop("Borough","service_zone")
    val recordsDF = spark.read.parquet("clean_records/records.parquet")

    val time = System.nanoTime
    val result = recordsDF
      .filter(month(col("tpep_pickup_datetime")) === 3)
      .join(broadcast(lookupDF), recordsDF("DOLocationID").equalTo(lookupDF("LocationID")))
      .filter(col("Zone").equalTo("Battery Park"))
      .withColumnRenamed("Zone", "DOZone")
      .orderBy(desc("Tip_amount"))
      .limit(1)
      .unpersist()
      .show()
    val duration = (System.nanoTime - time) / 1e9d

    val writer = new PrintWriter(output)
    writer.println(duration.toString)
    writer.close()

    spark.stop()
  }
}

object query2 {
  def main(args: Array[String]) {
    val spark = SparkSession.builder.appName("query2").getOrCreate()
    val conf = new Configuration()
    val fs = FileSystem.get(conf)
    val output = fs.append(new Path("hdfs://192.168.0.1:9000/user/user/queries/query2.txt"))

    val recordsDF = spark.read.parquet("clean_records/records.parquet")

    val time = System.nanoTime
    val result = recordsDF
      .groupBy(month(col("tpep_pickup_datetime")).as("Month"))
      .agg(max("Tolls_amount").as("Max_Tolls_amount"))
      .orderBy("Month")
      .unpersist()
      .show()
    val duration = (System.nanoTime - time) / 1e9d
    
    val writer = new PrintWriter(output)
    writer.println(duration)
    writer.close()

    spark.stop()
  }
}

object query3_rdd {
  def main(args: Array[String]) {
    val spark = SparkSession.builder.appName("query3_rdd").getOrCreate()
    val conf = new Configuration()
    val fs = FileSystem.get(conf)
    val output = fs.append(new Path("hdfs://192.168.0.1:9000/user/user/queries/query3_rdd.txt"))
    var dateFormat = new SimpleDateFormat("D");

    def tperiod(x:Int) : String = {
      if (x <= 15)
        "{2022-01-01 00:00:00, 2022-01-16 00:00:00}"
      else if (x <= 30)
        "{2022-01-16 00:00:00, 2022-01-31 00:00:00}"
      else if (x <= 45)
        "{2022-01-31 00:00:00, 2022-02-15 00:00:00}"
      else if (x <= 60)
        "{2022-02-15 00:00:00, 2022-03-02 00:00:00}"
      else if (x <= 75)
        "{2022-03-02 00:00:00, 2022-03-17 00:00:00}"
      else if (x <= 90)
        "{2022-03-17 00:00:00, 2022-04-01 01:00:00}"
      else if (x <= 105)
        "{2022-04-01 01:00:00, 2022-04-16 01:00:00}"
      else if (x <= 120)
        "{2022-04-16 01:00:00, 2022-05-01 01:00:00}"
      else if (x <= 135)
        "{2022-05-01 01:00:00, 2022-05-16 01:00:00}"
      else if (x <= 150)
        "{2022-05-16 01:00:00, 2022-05-31 01:00:00}"
      else if (x <= 165)
        "{2022-05-31 01:00:00, 2022-06-15 01:00:00}"
      else if (x <= 180)
        "{2022-06-15 01:00:00, 2022-06-30 01:00:00}"
      else
        "{2022-06-30 01:00:00, 2022-07-15 01:00:00}"
    }

    val recordsRDD = spark.read.parquet("clean_records/records.parquet").rdd

    val time = System.nanoTime
    val result = recordsRDD
      .filter(t => t.getAs[Long]("PULocationID") != t.getAs[Long]("DOLocationID"))
      .map(t => (tperiod(dateFormat.format(t.getTimestamp(1)).toInt), (t.getAs[Double]("trip_distance"), t.getAs[Double]("total_amount"))))
      .aggregateByKey(((0.0, 0.0), (0.0, 0.0))) (
        (acc: ((Double, Double), (Double, Double)), v: (Double, Double)) => 
          ((acc._1._1 + v._1, acc._1._2 + 1.0), (acc._2._1 + v._2, acc._2._2 + 1.0)),
        (acc1: ((Double, Double), (Double, Double)), acc2: ((Double, Double), (Double, Double))) => 
          ((acc1._1._1 + acc2._1._1, acc1._1._2 + acc2._1._2), (acc1._2._1 + acc2._2._1, acc1._2._2 + acc2._2._2))
      )
      .map{ case (key, ((sum1, count1), (sum2, count2))) => (key, sum1/count1, sum2/count2) }
      .sortBy(t => t._1)
      .unpersist()
      .collect()
      .foreach(println)
    val duration = (System.nanoTime - time) / 1e9d

    val writer = new PrintWriter(output)
    writer.println(duration)
    writer.close()

    spark.stop()
  }
}

object query3_df {
  def main(args: Array[String]) {
    val spark = SparkSession.builder.appName("query3_df").getOrCreate()
    val conf = new Configuration()
    val fs = FileSystem.get(conf)
    val output = fs.append(new Path("hdfs://192.168.0.1:9000/user/user/queries/query3_df.txt"))

    val recordsDF = spark.read.parquet("clean_records/records.parquet")

    val time = System.nanoTime
    val result = recordsDF
      .filter(col("PULocationID") =!= col("DOLocationID"))
      .groupBy(window(col("tpep_pickup_datetime"), "15 days", "15 days", "70 hours").as("time_period"))
      .avg("trip_distance", "total_amount")
      .orderBy("time_period")
      .unpersist()
      .show()
    val duration = (System.nanoTime - time) / 1e9d

    val writer = new PrintWriter(output)
    writer.println(duration)
    writer.close()

    spark.stop()
  }
}

object query4 {
  def main(args: Array[String]) {
    val spark = SparkSession.builder.appName("query4").getOrCreate()
    val conf = new Configuration()
    val fs = FileSystem.get(conf)
    val output = fs.append(new Path("hdfs://192.168.0.1:9000/user/user/queries/query4.txt"))

    val recordsDF = spark.read.parquet("clean_records/records.parquet")

    val time = System.nanoTime
    val result = recordsDF
      .withColumn("day_of_week", dayofweek(col("tpep_pickup_datetime")))
      .withColumn("hour", hour(col("tpep_pickup_datetime")))
      .groupBy("day_of_week", "hour")
      .agg(avg(col("Passenger_count")).as("Avg_Passenger_Count"))
      .withColumn("rank", row_number().over(Window.partitionBy("day_of_week").orderBy(col("Avg_Passenger_Count").desc)))
      .filter(col("rank") < 4)
      .unpersist()
      .show(21)
    val duration = (System.nanoTime - time) / 1e9d

    val writer = new PrintWriter(output)
    writer.println(duration)
    writer.close()

    spark.stop()
  }
}

object query5 {
  def main(args: Array[String]) {
    val spark = SparkSession.builder.appName("query5").getOrCreate()
    val conf = new Configuration()
    val fs = FileSystem.get(conf)
    val output = fs.append(new Path("hdfs://192.168.0.1:9000/user/user/queries/query5.txt"))

    val recordsDF = spark.read.parquet("clean_records/records.parquet")

    val time = System.nanoTime
    val result = recordsDF
      .withColumn("tip_percentage", col("tip_amount") / col("total_amount") * 100)
      .withColumn("date", to_date(col("tpep_dropoff_datetime")))
      .groupBy("date")
      .avg("tip_percentage")
      .withColumn("row", 
        row_number()
        .over(Window
          .partitionBy(month(col("date")))
          .orderBy(desc("avg(tip_percentage)"))
        )
      )
      .filter(col("row") <= 5)
      .select("date", "avg(tip_percentage)")
      .orderBy("date") 
      .unpersist()
      .show(30)
    val duration = (System.nanoTime - time) / 1e9d

    val writer = new PrintWriter(output)
    writer.println(duration)
    writer.close()

    spark.stop()
  }
}
