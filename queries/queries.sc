import org.apache.spark.sql.types._

val recordSchema = StructType(Array(
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
)

val lookupSchema = StructType(Array(
  StructField("LocationID", IntegerType, true),
  StructField("Borough", StringType, true),
  StructField("Zone", StringType, true),
  StructField("service_zone", StringType, true))
)

val recordRDD = spark.read.schema(recordSchema).parquet("../records").rdd //alternatively turn DF to RDD val recordRDD = recordDF.rdd
val lookupRDD = spark.read.schema(lookupSchema).options(Map("header"->"true")).csv("../lookup/taxi+_zone_lookup.csv").rdd //alternatively turn DF to RDD val lookupRDD = lookup

val recordDF = spark.read.schema(recordSchema).parquet("../records")
val lookupDF = spark.read.schema(lookupSchema).options(Map("header"->"true")).csv("../lookup/taxi+_zone_lookup.csv")
