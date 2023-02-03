import org.apache.spark.sql.types._
import org.apache.spark.sql.expressions.Window
import java.text.SimpleDateFormat

val sqlContext = new org.apache.spark.sql.SQLContext(sc)

/*val recordSchema = StructType(Array(
    StructField("VendorID", LongType, true),
    StructField("tpep_pickup_datetime", TimestampType, true),
    StructField("tpep_dropoff_datetime", TimestampType, true),
    StructField("Passenger_count", DoubleType, true),
    StructField("Trip_distance", DoubleType, true),
    StructField("PULocationID", LongType, true),
    StructField("DOLocationID", LongType, true),
    StructField("RateCodeID", DoubleType, true),
    StructField("Store_and_fwd_flag", StringType, true),
    StructField("Payment_type", LongType, true),
    StructField("Fare_amount", DoubleType, true),
    StructField("Extra", DoubleType, true),
    StructField("MTA_tax", DoubleType, true),
    StructField("Improvement_surcharge", DoubleType, true),
    StructField("Tip_amount", DoubleType, true),
    StructField("Tolls_amount", DoubleType, true),
    StructField("Total_amount", DoubleType, true),
    StructField("Congestion_Surcharge", DoubleType, true),
    StructField("Airport_fee", DoubleType, true))
)*/

val lookupSchema = StructType(Array(
    StructField("LocationID", IntegerType, true),
    StructField("Borough", StringType, true),
    StructField("Zone", StringType, true),
    StructField("service_zone", StringType, true))
)


//val recordsRDD = spark.read.parquet("../records").rdd //alternatively turn DF to RDD val recordsRDD = recordsDF.rdd
//val lookupRDD = sqlContext.read.schema(lookupSchema).options(Map("header"->"true")).csv("../lookup/taxi+_zone_lookup.csv").rdd //alternatively turn DF to RDD val lookupRDD = lookupDF.rdd


val recordsDF = spark.read.parquet("../records").filter(month(col("tpep_dropoff_datetime")) < 7 && year(col("tpep_pickup_datetime")) === 2022) 
val lookupDF = sqlContext.read.schema(lookupSchema).options(Map("header"->"true")).csv("../lookup/taxi+_zone_lookup.csv")

val recordsRDD = recordsDF.rdd
val lookupRDD = lookupDF.rdd

/* Q1 */
printf("|------------------- Q1 -------------------|\n")
/*
 spark.time(
       recordsDF.filter(month(col("tpep_pickup_datetime"))===3)
               .join(lookupDF, recordsDF("PULocationID").equalTo(lookupDF("LocationID")))
               .filter(col("Zone").equalTo("Battery Park"))
               .withColumnRenamed("Zone","PUZone")
               .drop("LocationID","Borough","service_zone")
               .orderBy(desc("Tip_amount"))
               .limit(1)
               .show()
                                                             )
 */

printf("|------------------------------------------|\n\n")


/* Q2 */
printf("|------------------- Q2 -------------------|\n")
/*
spark.time(
    recordsDF.groupBy(month(col("tpep_pickup_datetime")).as("Month"))
        .agg(max("Tolls_amount").as("Max_Tolls_amount"))
        .orderBy("Month")
        .show()
    )
*/
printf("|------------------------------------------|\n\n")


/* Q3 - RDD API */
printf("|--------------- Q3 RDD API ---------------|\n")

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

printf("time_period | avg(trip_distance) | avg(total_amount)\n")
/*spark.time(
  recordsRDD.filter(T => T.getAs[Long]("PULocationID") != T.getAs[Long]("DOLocationID"))
  .map(T => (
    tperiod(dateFormat.format(T.getTimestamp(1)).toInt)
    ,
    (T.getAs[Double]("trip_distance"), T.getAs[Double]("total_amount")))
  )
  .aggregateByKey(((0.0, 0.0), (0.0, 0.0)))
  (
    (C, V:(Double, Double)) => ((C._1._1 + V._1, C._1._2 + 1.0), (C._2._1 + V._2, C._2._2 + 1.0)),
    (C1, C2) => ((C1._1._1 + C2._1._1, C1._1._2 + C2._1._2), (C1._2._1 + C2._2._1, C1._2._2 + C2._2._2))
  )
  .map(T => (T._1, T._2._1._1 / T._2._1._2, T._2._2._1 / T._2._2._2))
  .sortBy(T => T._1)
  .collect()
  .foreach(println)
)*/

printf("|------------------------------------------|\n\n")


/* Q3 - DataFrame/SQL API */
printf("|---------- Q3 DataFrame/SQL API ----------|\n")

spark.time(
  recordsDF.filter(col("PULocationID") =!= col("DOLocationID"))
  .groupBy(window(col("tpep_pickup_datetime"), "15 days", "15 days", "70 hours").as("time_period"))
  .avg("trip_distance", "total_amount")
  .orderBy("time_period")
  .show(false)
)

printf("|------------------------------------------|\n\n")


/* Q4 */
printf("|------------------- Q4 -------------------|\n")
/*
spark.time(
    recordsDF.withColumn("day_of_week", dayofweek(col("tpep_pickup_datetime")))
      .withColumn("hour", hour(col("tpep_pickup_datetime")))
      .groupBy("day_of_week", "hour")
      .agg(count("*").as("transactions"))
      .withColumn("dense_rank", dense_rank().over(Window.partitionBy("day_of_week").orderBy(col("transactions").desc)))
      .filter(col("dense_rank") < 4)
      .show(50))
*/
printf("|------------------------------------------|\n\n")


/* Q5 */
printf("|------------------- Q5 -------------------|\n")

/*spark.time(
  recordsDF.withColumn("tip_percentage",
    col("tip_amount") / col("total_amount") * 100
  )
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
  .show(30)
)*/

printf("|------------------------------------------|\n\n")
