import org.apache.spark.sql.types._

val recordSchema = StructType(Array(
  StructField("VendorID", LongType, true),
  StructField("tpep_pickup_datetime", TimestampType, true),
  StructField("tpep_dropoff_datetime", TimestampType, true),
  StructField("Passenger_count", DoubleType, true),
  StructField("Trip_distance", FloatType, true),
  StructField("PULocationID", StringType, true),
  StructField("DOLocationID", StringType, true),
  StructField("RateCodeID", IntegerType, true),
  StructField("Store_and_fwd_flag", StringType, true),
  StructField("Payment_type", IntegerType, true),
  StructField("Fare_amount", FloatType, true),
  StructField("Extra", FloatType, true),
  StructField("MTA_tax", FloatType, true),
  StructField("Improvement_surcharge", FloatType, true),
  StructField("Tip_amount", FloatType, true),
  StructField("Tolls_amount", FloatType, true),
  StructField("Total_amount", FloatType, true),
  StructField("Congestion_Surcharge", FloatType, true),
  StructField("Airport_fee", FloatType, true))
)

val lookupSchema = StructType(Array(
  StructField("LocationID", IntegerType, true),
  StructField("Borough", StringType, true),
  StructField("Zone", StringType, true),
  StructField("service_zone", StringType, true))
)

val recordDF = spark.read.schema(recordSchema).parquet("../records")
val lookupDF = spark.read.schema(lookupSchema).csv("../lookup/taxi+_zone_lookup.csv")

val recordRDD = spark.read.schema(recordSchema).parquet("../records").rdd
val lookupRDD = spark.read.schema(lookupSchema).csv("../lookup/taxi+_zone_lookup.csv").rdd
